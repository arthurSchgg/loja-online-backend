#!/usr/bin/env python3
"""Local full-stack homologation smoke, backed by real Spring Boot + PostgreSQL.

Only targets https://localhost with an explicitly trusted Caddy CA.
Does not call cloud services or send real emails. Creates one synthetic brand
and deactivates it again; use ONLY with the isolated HML Compose project.
"""
import http.cookiejar
import json
import os
from pathlib import Path
import ssl
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid


def load_setting(path: str, key: str) -> str:
    for line in Path(path).read_text(encoding="utf-8").splitlines():
        stripped = line.strip()
        if not stripped.startswith("#") and stripped.startswith(key + "="):
            raw = stripped.partition("=")[2].strip()
            if raw[:1] == raw[-1:] and raw.startswith(("'", '"')):
                raw = raw[1:-1]
            if raw and not raw.startswith("replace-with-"):
                return raw
    raise RuntimeError(f"Configure {key} in your untracked HML env file")


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, request, fp, code, msg, headers, newurl):
        return None


class Client:
    def __init__(self, base: str, ca_file: str):
        parsed = urllib.parse.urlparse(base)
        if parsed.scheme != "https" or parsed.hostname != "localhost" or parsed.port not in (8443, 18443):
            raise RuntimeError("HML smoke only accepts https://localhost:8443 or :18443")
        self.base = base.rstrip("/")
        context = ssl.create_default_context(cafile=ca_file)
        self.cookies = http.cookiejar.CookieJar()
        self.transport = urllib.request.build_opener(
            urllib.request.HTTPSHandler(context=context),
            urllib.request.HTTPCookieProcessor(self.cookies), NoRedirect()
        )

    def request(self, method: str, path: str, expected: int, *, payload=None, headers=None):
        data = None
        request_headers = dict(headers or {})
        if payload is not None:
            if isinstance(payload, dict):
                data = json.dumps(payload).encode("utf-8")
                request_headers["Content-Type"] = "application/json"
            elif isinstance(payload, bytes):
                data = payload
            else:
                raise TypeError("Unexpected body")
        request = urllib.request.Request(self.base + path, data=data,
                                         headers=request_headers, method=method)
        try:
            with self.transport.open(request, timeout=12) as result:
                status, body = result.status, result.read(150_000)
        except urllib.error.HTTPError as error:
            status, body = error.code, b""
        if status != expected:
            raise AssertionError(f"{method} {path}: HTTP {status}; expected {expected}")
        return body

    def get_json(self, path: str, expected=200):
        return json.loads(self.request("GET", path, expected))

    def csrf(self):
        csrf = self.get_json("/api/v1/auth/csrf")
        if csrf.get("headerName") != "X-CSRF-TOKEN" or not csrf.get("token"):
            raise AssertionError("Missing or invalid CSRF header")
        return {"X-CSRF-TOKEN": csrf["token"]}


def run():
    base = os.environ.get("HML_BASE_URL", "https://localhost:8443")
    ca_file = os.environ.get("STAGING_TEST_CA", "staging-local-ca.crt")
    env_file = os.environ.get("STAGING_TEST_ENV_FILE", ".env.staging")
    username = load_setting(env_file, "ADMIN_USERNAME")
    password = load_setting(env_file, "ADMIN_PASSWORD")
    client = Client(base, ca_file)

    for page, marker in [
        ("/", b"Lojinha da K"),
        ("/catalogo.html", b"<html"),
        ("/contato.html", b"<html"),
        ("/admin.html", b"login-form"),
        ("/admin-experiences.html", b"experiences-workspace"),
    ]:
        if marker not in client.request("GET", page, 200):
            raise AssertionError("Unexpected or missing frontend asset: " + page)
    config = client.request("GET", "/assets/js/config.js", 200)
    if b'mode:"live"' not in config or b'apiBaseUrl:"/api/v1"' not in config:
        raise AssertionError("HML bundle must use live, same-origin API")
    print("PASS public and admin static pages served through one HTTPS origin")

    products = client.get_json("/api/v1/products?page=0&size=12")
    if not isinstance(products.get("content"), list):
        raise AssertionError("Public catalog page contract invalid")
    if not isinstance(client.get_json("/api/v1/store"), dict):
        raise AssertionError("Public store contract invalid")
    client.request("GET", "/api/v1/auth/me", 401)
    client.request("GET", "/api/v1/admin/brands", 401)
    print("PASS real public API and denied anonymous administration")

    form = urllib.parse.urlencode({"username": username, "password": password}).encode("utf-8")
    login_headers = {"Content-Type": "application/x-www-form-urlencoded", **client.csrf()}
    client.request("POST", "/api/v1/auth/login", 200, payload=form, headers=login_headers)
    identity = client.get_json("/api/v1/auth/me")
    if identity.get("authenticated") is not True or identity.get("username") != username:
        raise AssertionError("Admin session not confirmed after login")
    if not isinstance(client.get_json("/api/v1/admin/experiences"), list):
        raise AssertionError("Administrative experiences listing invalid")
    if not isinstance(client.get_json("/api/v1/admin/brands"), list):
        raise AssertionError("Administrative brands listing invalid")
    print("PASS real login, HTTP session and administrative reads")

    brand_id = None
    try:
        test_name = "HML-TEST-" + uuid.uuid4().hex[:10].upper()
        brand = json.loads(client.request("POST", "/api/v1/admin/brands", 201,
                                         payload={"name": test_name}, headers=client.csrf()))
        brand_id = brand.get("id")
        if not isinstance(brand_id, int) or brand_id <= 0:
            raise AssertionError("Brand insert did not return persisted ID")
        brands = client.get_json("/api/v1/admin/brands")
        if not any(item.get("id") == brand_id for item in brands):
            raise AssertionError("PostgreSQL did not return the brand just created")
        print("PASS real API writes and reads PostgreSQL data")
    finally:
        if brand_id is not None:
            client.request("DELETE", f"/api/v1/admin/brands/{brand_id}", 204,
                           headers=client.csrf())
            print("PASS synthetic brand deactivated after test")

    client.request("POST", "/api/v1/auth/logout", 204, headers=client.csrf())
    client.request("GET", "/api/v1/auth/me", 401)
    client.request("GET", "/api/v1/admin/brands", 401)
    print("PASS CSRF-protected logout and authorization revocation")
    print("HML AUTOMATED SMOKE PASS — awaiting user manual acceptance")


if __name__ == "__main__":
    try:
        run()
    except Exception as exc:
        # Do not print secrets, cookies, CSRF, response bodies or the env file.
        print("HML FAIL: " + type(exc).__name__ + ": " + str(exc), file=sys.stderr)
        sys.exit(1)
