#!/usr/bin/env python3
"""Import a REVIEWED catalog manifest through the existing Spring Boot admin API.

No WhatsApp scraping, direct SQL, image hotlink guessing, or automatic publication.
Safe by default: validates a JSON file and prints a plan; --apply is required
for any writes. Requires a local backend with a staging/local administrator.
"""
import argparse
from decimal import Decimal, InvalidOperation
import getpass
import http.cookiejar
import ipaddress
import json
from pathlib import Path
import re
import ssl
import sys
import urllib.error
import urllib.parse
import urllib.request


def clean(value):
    return " ".join(value.split()).casefold()


def price(value):
    raw = str(value).strip().replace("R$", "").replace(" ", "")
    if "," in raw:
        raw = raw.replace(".", "").replace(",", ".")
    if not re.fullmatch(r"\d{1,10}(?:\.\d{1,2})?", raw):
        raise ValueError("Preço inválido: use 89.90 ou R$ 89,90")
    try:
        amount = Decimal(raw)
    except InvalidOperation as exc:
        raise ValueError("Preço inválido") from exc
    if not amount.is_finite():
        raise ValueError("Preço não finito")
    return str(amount.quantize(Decimal("0.01")))


def validate_image(url):
    if not isinstance(url, str) or not 1 <= len(url) <= 2048:
        raise ValueError("URL da imagem ausente ou longa demais")
    parsed = urllib.parse.urlsplit(url)
    if parsed.scheme.lower() != "https" or not parsed.hostname or parsed.username or parsed.password or parsed.port:
        raise ValueError("Imagens exigem URLs HTTPS públicas sem credenciais ou porta personalizada")
    host = parsed.hostname.lower()
    if host == "localhost" or host.endswith(".localhost") or host.endswith(".local"):
        raise ValueError("A imagem não pode apontar para uma máquina local")
    try:
        if not ipaddress.ip_address(host).is_global:
            raise ValueError("A imagem não pode apontar para IP privado")
    except ValueError as exc:
        if str(exc) == "A imagem não pode apontar para IP privado":
            raise
    return url


def check_manifest(path):
    manifest = json.loads(Path(path).read_text(encoding="utf-8-sig"))
    if not isinstance(manifest, dict) or not isinstance(manifest.get("products"), list):
        raise ValueError("O JSON deve conter um array products")
    products = []
    seen = set()
    for idx, item in enumerate(manifest["products"], 1):
        try:
            if not isinstance(item, dict):
                raise ValueError("Produto deve ser um objeto")
            name, brand = item.get("name"), item.get("brand")
            if not isinstance(name, str) or not name.strip() or len(name) > 180:
                raise ValueError("Nome obrigatório, até 180 caracteres")
            if not isinstance(brand, str) or not brand.strip() or len(brand) > 120:
                raise ValueError("Marca obrigatória, até 120 caracteres")
            key = (clean(brand), clean(name))
            if key in seen:
                raise ValueError("Produto repetido no arquivo (nome + marca)")
            seen.add(key)
            categories = item.get("categories", [])
            if not isinstance(categories, list) or any(
                not isinstance(c, str) or not c.strip() or len(c) > 120 for c in categories
            ):
                raise ValueError("Categorias devem ser nomes não vazios até 120 caracteres")
            if len(set(map(clean, categories))) != len(categories):
                raise ValueError("Categorias repetidas")
            description = item.get("description") or ""
            if not isinstance(description, str) or len(description) > 4000:
                raise ValueError("Descrição deve ter até 4000 caracteres")
            amount = price(item.get("price", ""))
            images = item.get("images")
            if not isinstance(images, list) or not 1 <= len(images) <= 12:
                raise ValueError("Informe de 1 a 12 imagens por produto")
            processed_images = []
            for position, image in enumerate(images):
                if isinstance(image, str):
                    image = {"url": image}
                if not isinstance(image, dict):
                    raise ValueError("Imagem deve ser URL ou objeto com url/altText")
                url = validate_image(image.get("url"))
                alt = image.get("altText", name)
                if not isinstance(alt, str) or len(alt) > 180:
                    raise ValueError("Alt text longo demais")
                processed_images.append({"url": url, "position": position, "altText": alt})
            if "available" in item and not isinstance(item["available"], bool):
                raise ValueError("available deve ser true ou false")
            if item.get("published") is True:
                raise ValueError("Importação não publica produtos; remova published=true")
            products.append({
                "name": name.strip(), "brand": brand.strip(),
                "categories": [c.strip() for c in categories],
                "description": description.strip(), "price": amount,
                "available": item.get("available", False),
                "images": processed_images, "published": False
            })
        except (ValueError, TypeError) as exc:
            raise ValueError(f"Produto {idx}: {exc}") from exc
    return products


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, request, fp, code, msg, headers, newurl):
        return None


class API:
    def __init__(self, base, ca=None):
        parsed = urllib.parse.urlsplit(base)
        if parsed.query or parsed.fragment or parsed.path not in ("", "/"):
            raise ValueError("URL base não pode conter rota, query ou fragmento")
        allowed = {
            ("http", "localhost", 8080), ("http", "127.0.0.1", 8080),
            ("https", "localhost", 8443), ("https", "localhost", 18443)
        }
        if (parsed.scheme, parsed.hostname, parsed.port) not in allowed:
            raise ValueError("Importação restrita ao backend localhost:8080 ou HTTPS local:8443/18443")
        self.base = base.rstrip("/")
        if parsed.scheme == "https":
            if not ca:
                raise ValueError("Informe --ca com o certificado local do Caddy")
            context = ssl.create_default_context(cafile=ca)
            transport = urllib.request.HTTPSHandler(context=context)
        else:
            transport = urllib.request.HTTPHandler()
        self.session = urllib.request.build_opener(
            transport, urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()), NoRedirect()
        )

    def request(self, method, path, payload=None, csrf=None):
        headers = {"Accept": "application/json"}
        if csrf:
            headers.update(csrf)
        body = None
        if isinstance(payload, dict):
            body = json.dumps(payload, ensure_ascii=False).encode("utf-8")
            headers["Content-Type"] = "application/json"
        elif isinstance(payload, bytes):
            body = payload
            headers["Content-Type"] = "application/x-www-form-urlencoded"
        req = urllib.request.Request(self.base + "/api/v1" + path, body, headers, method=method)
        try:
            with self.session.open(req, timeout=15) as response:
                status, data = response.status, response.read(250_000)
        except urllib.error.HTTPError as exc:
            raise RuntimeError(f"{method} {path}: HTTP {exc.code} (sem alterações adicionais)") from None
        if status not in (200, 201, 204):
            raise RuntimeError(f"{method} {path}: HTTP inesperado {status}")
        return json.loads(data) if data else None

    def auth(self, username, password):
        token = self.request("GET", "/auth/csrf")
        headers = {token["headerName"]: token["token"]}
        self.request("POST", "/auth/login", urllib.parse.urlencode(
            {"username": username, "password": password}).encode("utf-8"), headers)
        user = self.request("GET", "/auth/me")
        if not isinstance(user, dict) or user.get("authenticated") is not True:
            raise RuntimeError("Sessão administrativa não confirmada")
        return headers


def index_unique(items):
    result = {}
    for item in items:
        name = clean(item["name"])
        if name in result:
            raise RuntimeError("Nomes de marcas/categorias duplicados na API")
        result[name] = item["id"]
    return result


def admin_products(api):
    items, page = [], 0
    while True:
        data = api.request("GET", f"/admin/products?page={page}&size=50")
        content = data.get("content")
        if not isinstance(content, list):
            raise RuntimeError("Resposta de produtos administrativos inesperada")
        items.extend(content)
        if data.get("last") is True or page + 1 >= data.get("totalPages", 1):
            return items
        page += 1
        if page > 10000:
            raise RuntimeError("Paginação administrativa inesperada")


def apply(api, records, update_existing=False):
    import os
    user = os.environ.get("CATALOG_ADMIN_USERNAME") or input("Usuário administrador local: ").strip()
    pwd = getpass.getpass("Senha do administrador local (não aparece): ")
    headers = api.auth(user, pwd)
    brands = index_unique(api.request("GET", "/admin/brands"))
    categories = index_unique(api.request("GET", "/admin/categories"))
    existing = {}
    for row in admin_products(api):
        key = (row.get("brand", {}).get("id"), clean(row["name"]))
        if key in existing:
            raise RuntimeError("Há produtos com nome+marca duplicados no banco: revise manualmente")
        existing[key] = row["id"]
    created, skipped, updated = 0, 0, 0
    for record in records:
        brand_name = clean(record["brand"])
        if brand_name not in brands:
            brand = api.request("POST", "/admin/brands",
                                {"name": record["brand"]}, headers)
            brands[brand_name] = brand["id"]
        cat_ids = []
        for title in record["categories"]:
            key = clean(title)
            if key not in categories:
                cat = api.request("POST", "/admin/categories", {"name": title}, headers)
                categories[key] = cat["id"]
            cat_ids.append(categories[key])
        payload = {k: record[k] for k in
                   ("name", "description", "price", "available", "published", "images")}
        payload["brandId"] = brands[brand_name]
        payload["categoryIds"] = cat_ids
        key = (brands[brand_name], clean(record["name"]))
        if key in existing:
            if update_existing:
                api.request("PUT", f"/admin/products/{existing[key]}", payload, headers)
                updated += 1
            else:
                skipped += 1
            continue
        result = api.request("POST", "/admin/products", payload, headers)
        existing[key] = result["id"]
        created += 1
    print(f"Concluído: {created} rascunho(s) criado(s), {updated} atualizado(s), "
          f"{skipped} já existente(s) ignorado(s). Nenhum produto publicado.")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("manifest", help="Arquivo JSON revisado com produtos e URLs")
    parser.add_argument("--apply", action="store_true", help="Cadastrar via API (padrão é conferência sem escrita)")
    parser.add_argument("--update-existing", action="store_true", help="Atualizar produtos existentes por nome+marca")
    parser.add_argument("--base-url", default="http://localhost:8080")
    parser.add_argument("--ca", help="CA local do proxy Caddy para URLs HTTPS")
    args = parser.parse_args()
    if args.update_existing and not args.apply:
        parser.error("--update-existing requer --apply")
    products = check_manifest(args.manifest)
    print(f"Manifesto validado: {len(products)} produto(s), sem publicação automática.")
    if not args.apply:
        print("Prévia concluída: nenhum dado alterado. Use --apply somente após revisar.")
        return
    if not products:
        raise ValueError("Manifesto vazio: nada para importar")
    apply(API(args.base_url, args.ca), products, args.update_existing)


if __name__ == "__main__":
    try:
        main()
    except (ValueError, RuntimeError, OSError, json.JSONDecodeError) as exc:
        print("Falha na importação: " + str(exc), file=sys.stderr)
        sys.exit(1)
