#!/usr/bin/env python3
"""Read-only public API contract smoke against disposable HTTPS staging.

This mirrors the *HTTP expectations* of the frontend's
scripts/public-api-smoke.mjs at frontend develop SHA b9b8bc1.
The frontend repository is private to the backend CI token, so it must not be
checked out with new secrets or a personal access token. Maintain both tests
as their public JSON contracts evolve.
"""
import json
import os
import ssl
import sys
import urllib.error
import urllib.parse
import urllib.request
from decimal import Decimal, InvalidOperation


class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def verify_page(data):
    if not isinstance(data, dict) or not isinstance(data.get("content"), list):
        raise AssertionError("Public catalog must return a paginated content list")
    if type(data.get("last")) is not bool:
        raise AssertionError("Catalog page must contain a boolean last flag")
    return data["content"], data["last"]


def verify_products(products):
    previous_price = None
    previous_id = None
    for product in products:
        if not isinstance(product, dict):
            raise AssertionError("Invalid product DTO")
        product_id = product.get("id")
        if type(product_id) is not int or product_id <= 0:
            raise AssertionError("Invalid product ID")
        raw = product.get("effectivePrice", product.get("price"))
        try:
            price = Decimal(str(raw))
        except (InvalidOperation, ValueError):
            raise AssertionError("Invalid effective price") from None
        if not price.is_finite() or price < 0:
            raise AssertionError("Invalid effective price")
        if previous_price is not None and (
            price < previous_price or (price == previous_price and product_id >= previous_id)
        ):
            raise AssertionError("Public catalog must order by effective price and descending ID")
        previous_price, previous_id = price, product_id


def verify_experience_detail(detail):
    if not isinstance(detail, dict) or not isinstance(detail.get("questions"), list):
        raise AssertionError("Public experience must include its questions")
    if "results" in detail:
        raise AssertionError("Public experience must not expose admin results")
    for question in detail["questions"]:
        if not isinstance(question, dict):
            raise AssertionError("Invalid question DTO")
        for option in question.get("options") or []:
            if not isinstance(option, dict) or "resultKey" in option:
                raise AssertionError("Public experience must not expose result keys")


def run(base_url, ca_file):
    parsed = urllib.parse.urlsplit(base_url)
    if parsed.scheme != "https" or parsed.hostname != "localhost" or not parsed.port:
        raise ValueError("Public contract smoke must use loopback HTTPS staging")
    if parsed.path not in ("", "/") or parsed.query or parsed.fragment or parsed.username:
        raise ValueError("Pass only the HTTPS staging origin")
    opener = urllib.request.build_opener(
        urllib.request.HTTPSHandler(context=ssl.create_default_context(cafile=ca_file)),
        NoRedirect(),
    )
    origin = base_url.rstrip("/")
    calls = 0

    def get(path):
        nonlocal calls
        if not path.startswith("/api/v1/"):
            raise AssertionError("Only explicit public API routes are allowed")
        request = urllib.request.Request(
            origin + path, headers={"Accept": "application/json"}, method="GET"
        )
        with opener.open(request, timeout=8) as response:
            if response.status != 200:
                raise AssertionError("Unexpected public API HTTP status")
            payload = response.read(200_000)
        calls += 1
        return json.loads(payload)

    page0, last = verify_page(get("/api/v1/products?sort=price-asc&page=0&size=12"))
    products = list(page0)
    if not last:
        page1, _ = verify_page(get("/api/v1/products?sort=price-asc&page=1&size=12"))
        products.extend(page1)
    verify_products(products)

    categories = get("/api/v1/categories")
    if not isinstance(categories, list):
        raise AssertionError("Categories must be an array")
    store = get("/api/v1/store")
    if not isinstance(store, dict):
        raise AssertionError("Store must be an object")
    offerings = get("/api/v1/offerings")
    if not isinstance(offerings, list):
        raise AssertionError("Offerings must be an array")
    experiences = get("/api/v1/experiences")
    if not isinstance(experiences, list):
        raise AssertionError("Experiences must be an array")
    if experiences:
        first_id = experiences[0].get("id") if isinstance(experiences[0], dict) else None
        if type(first_id) is not int or first_id <= 0:
            raise AssertionError("Invalid public experience ID")
        verify_experience_detail(get(f"/api/v1/experiences/{first_id}"))

    # Never print URLs, session values or response bodies from staging.
    print(f"PASS public frontend contract over trusted HTTPS ({calls} read-only GET requests)")


if __name__ == "__main__":
    try:
        run(os.getenv("STAGING_TEST_BASE_URL", "https://localhost:18443"), os.environ["STAGING_TEST_CA"])
    except Exception as error:
        print(f"FAIL {type(error).__name__}: {error}", file=sys.stderr)
        sys.exit(1)
