import json
from pathlib import Path
import tempfile
import unittest
from import_catalog import API, check_manifest, clean, price, validate_image


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
        for value in ("", "-1", "abc", "15,999", "9999999999999"):
            with self.subTest(value=value), self.assertRaises(ValueError):
                price(value)

    def test_manifest_never_publishes_by_default(self):
        record = check_manifest(self.write(self.valid()))[0]
        self.assertFalse(record["published"])
        self.assertFalse(record["available"])
        self.assertEqual(record["price"], "89.90")
        self.assertEqual(record["images"][0]["position"], 0)
        self.assertEqual(record["categories"], ["Categoria de teste"])

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

    def test_name_normalization(self):
        self.assertEqual(clean("  PERFUME   Floral "), "perfume floral")


if __name__ == "__main__":
    unittest.main()
