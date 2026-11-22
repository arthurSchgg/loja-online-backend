import json
from pathlib import Path
import tempfile
import unittest
from import_catalog import API, apply, check_manifest, clean, price, validate_image


class CatalogImportTests(unittest.TestCase):
    def write(self, content):
        temp = tempfile.TemporaryDirectory()
        path = Path(temp.name) / "products.json"
        path.write_text(json.dumps(content), encoding="utf-8")
        self.addCleanup(temp.cleanup)
        return str(path)

    def valid(self):
        return {"products": [{
            "name": "Produto de teste",
            "brand": "Marca de teste",
            "price": "R$ 89,90",
            "description": "Apenas exemplo de teste",
            "categories": ["Categoria de teste"],
            "images": [{"url": "https://example.org/teste.webp", "altText": "Foto teste"}],
        }]}

    def test_price_brazilian_and_dot_decimal(self):
        self.assertEqual(price("R$ 1.234,56"), "1234.56")
        self.assertEqual(price("15.9"), "15.90")
        for value in ("", "0", "0.00", "-1", "abc", "15,999", "9999999999999"):
            with self.subTest(value=value), self.assertRaises(ValueError):
                price(value)

    def test_manifest_never_publishes_by_default(self):
        record = check_manifest(self.write(self.valid()))[0]
        self.assertFalse(record["published"])
        self.assertFalse(record["available"])
        self.assertEqual(record["price"], "89.90")
        self.assertEqual(record["images"][0]["position"], 0)
        self.assertEqual(record["categories"], ["Categoria de teste"])

    def test_pending_price_is_explicit(self):
        data = self.valid()
        data["products"][0].update(price=None, priceStatus="ON_REQUEST")
        record = check_manifest(self.write(data))[0]
        self.assertIsNone(record["price"])
        self.assertEqual(record["priceStatus"], "ON_REQUEST")
        data["products"][0]["price"] = "0"
        with self.assertRaisesRegex(ValueError, "sob consulta"):
            check_manifest(self.write(data))

    def test_rejects_duplicated_products(self):
        data = self.valid()
        data["products"].append({**data["products"][0], "name": "  PRODUTO   DE TESTE "})
        with self.assertRaisesRegex(ValueError, "repetido"):
            check_manifest(self.write(data))

    def test_rejects_auto_publication_and_missing_images(self):
        for changed in ({"published": True}, {"images": []},
                        {"images": ["http://example.org/a.jpg"]},
                        {"images": ["https://localhost/a.jpg"]}):
            data = self.valid()
            data["products"][0].update(changed)
            with self.subTest(changed=changed), self.assertRaises(ValueError):
                check_manifest(self.write(data))

    def test_relative_catalog_webp_is_accepted_and_path_traversal_is_rejected(self):
        valid = "assets/images/catalogo/skelt/maquiagem-corporal.webp"
        self.assertEqual(validate_image(valid), valid)
        for invalid in ("assets/images/catalogo/../secret.webp",
                        "/assets/images/catalogo/skelt/a.webp",
                        "assets/images/catalogo/skelt/a.svg",
                        "assets/images/catalogo/skelt/a.webp?hack=1"):
            with self.subTest(invalid=invalid), self.assertRaises(ValueError):
                validate_image(invalid)

    def test_rejects_private_image_ips(self):
        for uri in ("https://127.0.0.1/a.jpg", "https://192.168.0.2/a.jpg",
                    "https://user:pass@example.org/photo.jpg"):
            with self.subTest(uri=uri), self.assertRaises(ValueError):
                validate_image(uri)

    def test_api_never_targets_remote_host(self):
        for target in ("https://example.org", "http://example.org",
                       "http://localhost:9999", "https://localhost:8443/evil"):
            with self.subTest(target=target), self.assertRaises(ValueError):
                API(target)
        self.assertIsInstance(API("http://localhost:8080"), API)

    def test_import_uses_admin_api_not_sql_and_prevents_automatic_availability(self):
        from unittest.mock import patch

        class FakeApi:
            def __init__(self):
                self.rows = []
                self.created = []
            def auth(self, user, password):
                self.created.append(("auth", user))
                return {"X-CSRF-TOKEN": "dummy"}
            def request(self, method, path, payload=None, csrf=None):
                if method == "GET":
                    if path == "/admin/brands" or path == "/admin/categories":
                        return []
                    if path.startswith("/admin/products?"):
                        return {"content": self.rows, "totalPages": 1, "last": True}
                if method == "POST":
                    if path == "/admin/brands":
                        return {"id": 1, "name": payload["name"]}
                    if path == "/admin/categories":
                        return {"id": 2, "name": payload["name"]}
                    if path == "/admin/products":
                        self.created.append((path, payload.copy()))
                        self.rows.append({"id": 3, "name": payload["name"], "brand": {"id": 1}})
                        return {"id": 3}
                raise AssertionError(f"Unexpected API operation {method} {path}")

        record = check_manifest(self.write(self.valid()))[0]
        api = FakeApi()
        with patch("builtins.input", return_value="admin"), patch("getpass.getpass", return_value="private"):
            apply(api, [record], local_preview=False)
        payload = api.created[-1][1]
        self.assertFalse(payload["published"])
        self.assertFalse(payload["available"])
        self.assertEqual(payload["images"][0]["url"], "https://example.org/teste.webp")

        with patch("builtins.input", return_value="admin"), patch("getpass.getpass", return_value="private"):
            apply(api, [record], local_preview=True)
        self.assertEqual(len([x for x in api.created if x[0] == "/admin/products"]), 1)

        another = FakeApi()
        with patch("builtins.input", return_value="admin"), patch("getpass.getpass", return_value="private"):
            apply(another, [record], local_preview=True)
        visible = another.created[-1][1]
        self.assertTrue(visible["published"])
        self.assertFalse(visible["available"])

    def test_name_normalization(self):
        self.assertEqual(clean("  PERFUME   Floral "), "perfume floral")


if __name__ == "__main__":
    unittest.main()
