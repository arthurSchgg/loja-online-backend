"""Isolated tests for staging login readiness; no server or credentials required."""
import pathlib
import runpy
import unittest

module = runpy.run_path(str(pathlib.Path(__file__).with_name("smoke-staging-https.py")), run_name="staging_smoke_unit")
login = module["login_after_admin_bootstrap"]
UnexpectedHttpStatus = module["UnexpectedHttpStatus"]


class StagingLoginReadinessTest(unittest.TestCase):
    def test_transient_401_refreshes_csrf_then_succeeds(self):
        submitted = []
        slept = []
        def csrf():
            return "X-CSRF-TOKEN", f"token-{len(submitted)}"
        def submit(header, token):
            submitted.append((header, token))
            if len(submitted) < 3:
                raise UnexpectedHttpStatus("POST", "/api/v1/auth/login", (200,), 401)
        login(csrf, submit, sleep=slept.append)
        self.assertEqual(len(submitted), 3)
        self.assertEqual(slept, [1, 2])
        self.assertEqual([token for _, token in submitted], ["token-0", "token-1", "token-2"])

    def test_permanently_invalid_credentials_fail_after_bounded_attempts(self):
        attempts = []
        def submit(*args):
            attempts.append(args)
            raise UnexpectedHttpStatus("POST", "/api/v1/auth/login", (200,), 401)
        with self.assertRaises(UnexpectedHttpStatus) as error:
            login(lambda: ("X-CSRF-TOKEN", "token"), submit, sleep=lambda _: None)
        self.assertEqual(error.exception.actual, 401)
        self.assertEqual(len(attempts), 3)

    def test_csrf_or_throttle_failures_never_retry(self):
        for status in (403, 429):
            with self.subTest(status=status):
                attempts = []
                def submit(*args):
                    attempts.append(args)
                    raise UnexpectedHttpStatus("POST", "/api/v1/auth/login", (200,), status)
                with self.assertRaises(UnexpectedHttpStatus):
                    login(lambda: ("X-CSRF-TOKEN", "token"), submit, sleep=lambda _: self.fail("must not sleep"))
                self.assertEqual(len(attempts), 1)


if __name__ == "__main__":
    unittest.main()
