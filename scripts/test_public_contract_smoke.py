"""Contract smoke unit tests; do not require a running server or any credentials."""
import importlib.util
from pathlib import Path
import unittest

module_path = Path(__file__).with_name("smoke-public-contract.py")
spec = importlib.util.spec_from_file_location("smoke_public_contract", module_path)
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


class PublicContractTest(unittest.TestCase):
    def test_empty_page_is_valid(self):
        content, last = module.verify_page({"content": [], "last": True})
        self.assertEqual(content, [])
        self.assertTrue(last)
        module.verify_products(content)

    def test_prices_are_globally_sorted_and_ties_use_descending_ids(self):
        module.verify_products([
            {"id": 7, "price": "9.90", "effectivePrice": "8.90"},
            {"id": 3, "price": "9.90", "effectivePrice": "8.90"},
            {"id": 9, "price": "11.90", "effectivePrice": "11.90"},
        ])
        with self.assertRaises(AssertionError):
            module.verify_products([{"id": 1, "effectivePrice": 9}, {"id": 3, "effectivePrice": 8}])
        with self.assertRaises(AssertionError):
            module.verify_products([{"id": 2, "effectivePrice": 9}, {"id": 3, "effectivePrice": 9}])

    def test_invalid_prices_ids_and_pagination_fail_closed(self):
        for price in ["NaN", "Infinity", -1, 0, None, "not-a-price"]:
            with self.subTest(price=price), self.assertRaises(AssertionError):
                module.verify_products([{"id": 1, "effectivePrice": price}])
        for id_value in [0, -1, "1", True]:
            with self.subTest(id_value=id_value), self.assertRaises(AssertionError):
                module.verify_products([{"id": id_value, "effectivePrice": 10}])
        for page in [[], {}, {"content": [], "last": 0}]:
            with self.assertRaises(AssertionError):
                module.verify_page(page)

    def test_explicit_unknown_prices_are_last_and_not_available(self):
        unknown={"id": 9, "price": None, "effectivePrice": None, "priceStatus": "ON_REQUEST", "available": False}
        module.verify_products([{"id":1, "effectivePrice":8},unknown])
        with self.assertRaises(AssertionError):
            module.verify_products([unknown,{"id":1,"effectivePrice":8}])
        with self.assertRaises(AssertionError):
            module.verify_products([{**unknown,"available":True}])

    def test_public_experience_cannot_leak_result_keys(self):
        module.verify_experience_detail({"questions": [{"options": [{"id": 3, "label": "Floral"}]}]})
        for data in [
            {"questions": [], "results": [{"key": "private"}]},
            {"questions": [{"options": [{"id": 4, "resultKey": "private"}]}]},
            {"questions": "wrong"},
        ]:
            with self.assertRaises(AssertionError):
                module.verify_experience_detail(data)


if __name__ == "__main__":
    unittest.main()
