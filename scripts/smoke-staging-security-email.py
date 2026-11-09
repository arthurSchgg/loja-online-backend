#!/usr/bin/env python3
"""End-to-end security email validation over a trusted local HTTPS proxy.

Uses only fictitious addresses and the loopback Mailpit capture. Never prints
passwords, CSRF values, recovery links, tokens or message contents.
"""
import json
import os
import pathlib
import re
import runpy
import sys
import time
import urllib.parse
import urllib.request

support = runpy.run_path(
    str(pathlib.Path(__file__).with_name("smoke-staging-https.py")),
    run_name="staging_smoke_support",
)
Smoke = support["Smoke"]
fixture_password = support["fixture_password"]

ADMIN_EMAIL = "staging-ci-admin@example.invalid"
NEW_EMAIL = "staging-confirmed@example.invalid"
UPDATED_PASSWORD = "Staging integration phrase 2026 unique test only!"
MAILPIT_API = "http://127.0.0.1:18025/api/v1"


class EmailSecuritySmoke(Smoke):
    def post_json(self, path: str, payload: dict, expected: int) -> None:
        header, token = self.csrf()
        self.request(
            "POST", path, expected,
            body=json.dumps(payload).encode("utf-8"),
            headers={
                "Content-Type": "application/json",
                header: token,
            },
        )

    def form_login(self, username: str, password: str, expected: int) -> None:
        header, token = self.csrf()
        body = urllib.parse.urlencode({
            "username": username, "password": password
        }).encode("utf-8")
        self.request(
            "POST", "/api/v1/auth/login", expected,
            body=body,
            headers={"Content-Type": "application/x-www-form-urlencoded", header: token},
        )

    @staticmethod
    def mailpit_json(path: str) -> dict:
        # This HTTP API is permitted only because it is bound to runner loopback.
        with urllib.request.urlopen(MAILPIT_API + path, timeout=5) as response:
            return json.load(response)

    def messages(self) -> list:
        messages = self.mailpit_json("/messages?limit=100").get("messages", [])
        if not isinstance(messages, list):
            raise AssertionError("Mailpit returned invalid messages format")
        return messages

    def await_message(self, recipient: str, subject: str, excluded_ids=None) -> dict:
        excluded_ids = excluded_ids or set()
        deadline = time.monotonic() + 105
        while time.monotonic() < deadline:
            for message in self.messages():
                if (
                    message.get("ID") not in excluded_ids
                    and subject in message.get("Subject", "")
                    and any(
                        to.get("Address", "").lower() == recipient.lower()
                        for to in message.get("To", [])
                    )
                ):
                    return self.mailpit_json(
                        "/message/" + urllib.parse.quote(message["ID"], safe="")
                    )
            time.sleep(2)
        raise AssertionError("Expected security email was not captured before timeout")

    @staticmethod
    def extract_token(email: dict) -> str:
        body = email.get("Text", "")
        match = re.search(r"[?&]token=([A-Za-z0-9_-]{40,100})", body)
        if match is None:
            raise AssertionError("Captured security email did not contain a valid token")
        return match.group(1)

    def run_email_flows(self, username: str, initial_password: str) -> None:
        self.post_json(
            "/api/v1/auth/password-reset/request",
            {"email": "unknown-staging@example.invalid"},
            202,
        )
        self.post_json(
            "/api/v1/auth/password-reset/request", {"email": ADMIN_EMAIL}, 202
        )
        reset_email = self.await_message(ADMIN_EMAIL, "Recuperação de acesso")
        token = self.extract_token(reset_email)
        if any("unknown-staging@example.invalid" in str(m.get("To", []))
               for m in self.messages()):
            raise AssertionError("Reset request disclosed or emailed unknown account")
        print("PASS recovery request returns 202 without account enumeration")

        reset_payload = {
            "token": token,
            "newPassword": UPDATED_PASSWORD,
            "confirmPassword": UPDATED_PASSWORD,
        }
        self.request(
            "POST", "/api/v1/auth/password-reset/confirm", (401, 403),
            body=json.dumps(reset_payload).encode("utf-8"),
            headers={"Content-Type": "application/json"},
        )
        self.post_json("/api/v1/auth/password-reset/confirm", reset_payload, 204)
        self.post_json("/api/v1/auth/password-reset/confirm", reset_payload, 400)
        print("PASS HTTPS password reset requires CSRF, expires token after first use")

        self.form_login(username, initial_password, 401)
        self.form_login(username, UPDATED_PASSWORD, 200)
        identity = json.loads(self.request("GET", "/api/v1/auth/me", 200))
        if identity.get("username") != username:
            raise AssertionError("New password did not authenticate expected administrator")
        print("PASS old password is rejected and rotated password authenticates")

        self.post_json(
            "/api/v1/admin/security/change-email",
            {"currentPassword": UPDATED_PASSWORD, "newEmail": NEW_EMAIL},
            202,
        )
        self.request("GET", "/api/v1/auth/me", 200)
        changed_email = self.await_message(NEW_EMAIL, "Confirmar novo e-mail")
        confirm_token = self.extract_token(changed_email)
        self.await_message(ADMIN_EMAIL, "Alteração de e-mail solicitada")
        print("PASS pending email change requires password and notifies both addresses")

        self.post_json(
            "/api/v1/auth/email-change/confirm", {"token": confirm_token}, 204
        )
        # The successful confirmation advances credential_version. The previous
        # authenticated browser session is rejected before reaching controllers.
        self.request("GET", "/api/v1/auth/me", 401)
        # A new anonymous session can still reach the public confirmation route,
        # but a consumed token cannot be reused.
        self.post_json(
            "/api/v1/auth/email-change/confirm", {"token": confirm_token}, 400
        )
        self.await_message(NEW_EMAIL, "Novo e-mail confirmado")
        self.await_message(ADMIN_EMAIL, "E-mail administrativo alterado")
        print("PASS confirmed email cannot be reused and revokes old admin session")

        before = {m.get("ID") for m in self.messages()}
        self.post_json(
            "/api/v1/auth/password-reset/request", {"email": NEW_EMAIL}, 202
        )
        self.await_message(NEW_EMAIL, "Recuperação de acesso", excluded_ids=before)
        print("PASS newly confirmed email is used for future password recovery")


if __name__ == "__main__":
    try:
        base = os.environ.get("STAGING_TEST_BASE_URL", "https://localhost:18443")
        certificate = os.environ["STAGING_TEST_CA"]
        username = "staging-ci-admin"
        password = fixture_password(os.environ["STAGING_TEST_ENV_FILE"])
        EmailSecuritySmoke(base, certificate).run_email_flows(username, password)
    except Exception as exc:
        # Never include passwords, security links, tokens or message bodies in CI logs.
        print(f"FAIL {type(exc).__name__}: {exc}", file=sys.stderr)
        sys.exit(1)
