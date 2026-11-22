#!/usr/bin/env python3
"""Local HTTPS smoke for the disposable staging Docker Compose stack.

Uses only stdlib, validates the Caddy private CA rather than disabling TLS
verification, and deliberately never prints credentials or session tokens.
"""
import http.cookiejar
import json
import os
import pathlib
import ssl
import sys
import time
import urllib.error
import urllib.parse
import urllib.request


def fixture_password(filename: str) -> str:
    for line in pathlib.Path(filename).read_text(encoding="utf-8").splitlines():
        if line.startswith("ADMIN_PASSWORD="):
            password = line.split("=", 1)[1].strip()
            if len(password) >= 12:
                return password
    raise RuntimeError("Staging CI fixture lacks an administrative password")


class UnexpectedHttpStatus(AssertionError):
    def __init__(self, method: str, path: str, expected: tuple[int, ...], actual: int):
        super().__init__(f"{method} {path}: expected HTTP {expected}, got {actual}")
        self.actual = actual


def login_after_admin_bootstrap(refresh_csrf, submit_login, sleep=time.sleep) -> None:
    """Bounded readiness retry on HTTP 401 ONLY, never bypass 403/429 or invalid credentials.

    Actuator health can become UP before an ApplicationRunner finishes seeding
    the staging-only administrator. Three attempts are below the five-failure
    login-throttle threshold; permanently invalid credentials still fail closed.
    """
    for attempt in range(3):
        header, token = refresh_csrf()
        try:
            submit_login(header, token)
            return
        except UnexpectedHttpStatus as error:
            if error.actual != 401 or attempt == 2:
                raise
            sleep(attempt + 1)


class Smoke:
    def __init__(self, base_url: str, ca_file: str):
        self.base = base_url.rstrip("/")
        if not self.base.startswith("https://localhost:"):
            raise RuntimeError("Smoke must target loopback HTTPS staging only")
        context = ssl.create_default_context(cafile=ca_file)
        self.cookies = http.cookiejar.CookieJar()
        self.client = urllib.request.build_opener(
            urllib.request.HTTPSHandler(context=context),
            urllib.request.HTTPCookieProcessor(self.cookies),
        )

    def request(self, method: str, path: str, expected: int | tuple[int, ...], *,
                body: bytes | None = None, headers: dict | None = None) -> bytes:
        request = urllib.request.Request(
            self.base + path, data=body, headers=headers or {}, method=method
        )
        try:
            response = self.client.open(request, timeout=8)
            with response:
                status = response.status
                content = response.read(64_000)
        except urllib.error.HTTPError as error:
            status = error.code
            content = b""  # Do not expose error bodies or tokens in CI logs.
        acceptable = expected if isinstance(expected, tuple) else (expected,)
        if status not in acceptable:
            raise UnexpectedHttpStatus(method, path, acceptable, status)
        return content

    def csrf(self) -> tuple[str, str]:
        data = json.loads(self.request("GET", "/api/v1/auth/csrf", 200))
        if data.get("headerName") != "X-CSRF-TOKEN" or not data.get("token"):
            raise AssertionError("Missing HTTP session CSRF token")
        return data["headerName"], data["token"]

    def assert_cookies(self) -> None:
        cookies = [cookie for cookie in self.cookies if cookie.name == "JSESSIONID"]
        if not cookies:
            raise AssertionError("HTTPS staging did not issue a session cookie")
        for cookie in cookies:
            attributes = {key.lower(): value for key, value in cookie._rest.items()}
            if not cookie.secure or "httponly" not in attributes:
                raise AssertionError("Session cookie must be Secure and HttpOnly")
            if str(attributes.get("samesite", "")).lower() != "lax":
                raise AssertionError("Session cookie must have SameSite=Lax")

    def run(self, username: str, password: str) -> None:
        health = json.loads(self.request("GET", "/actuator/health", 200))
        if health.get("status") != "UP":
            raise AssertionError("Backend health endpoint is not UP")
        print("PASS HTTPS via trusted Caddy staging certificate and health endpoint")

        self.request("GET", "/api/v1/auth/me", 401)
        self.request("GET", "/api/v1/admin/brands", 401)
        print("PASS unauthenticated admin routes are inaccessible")

        header_name, token = self.csrf()
        self.assert_cookies()
        print("PASS CSRF endpoint and secure HttpOnly SameSite=Lax cookie")

        payload = urllib.parse.urlencode({
            "username": username,
            "password": password,
        }).encode("utf-8")
        self.request("POST", "/api/v1/auth/login", (401, 403),
                     body=payload, headers={"Content-Type": "application/x-www-form-urlencoded"})
        self.request("GET", "/api/v1/auth/me", 401)
        print("PASS login without CSRF cannot authenticate")

        login_after_admin_bootstrap(
            self.csrf,
            lambda fresh_header, fresh_token: self.request(
                "POST", "/api/v1/auth/login", 200, body=payload, headers={
                    "Content-Type": "application/x-www-form-urlencoded",
                    fresh_header: fresh_token,
                }
            ),
        )
        self.assert_cookies()
        identity = json.loads(self.request("GET", "/api/v1/auth/me", 200))
        if identity.get("username") != username or identity.get("authenticated") is not True:
            raise AssertionError("Authenticated identity does not match staging administrator")
        print("PASS administrator form login and authenticated identity")

        self.request("POST", "/api/v1/auth/logout", 403)
        header_name, token = self.csrf()
        self.request("POST", "/api/v1/auth/logout", 204, headers={header_name: token})
        self.request("GET", "/api/v1/auth/me", 401)
        print("PASS logout requires CSRF and invalidates the authentication session")


if __name__ == "__main__":
    try:
        base = os.environ.get("STAGING_TEST_BASE_URL", "https://localhost:18443")
        ca_file = os.environ["STAGING_TEST_CA"]
        username = "staging-ci-admin"
        password = fixture_password(os.environ["STAGING_TEST_ENV_FILE"])
        Smoke(base, ca_file).run(username, password)
    except Exception as exc:
        print(f"FAIL {type(exc).__name__}: {exc}", file=sys.stderr)
        sys.exit(1)
