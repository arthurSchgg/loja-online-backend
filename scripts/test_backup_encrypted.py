"""Exercise encryption and safe guards with synthetic bytes; never connect to production."""
import hashlib
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

SCRIPTS = Path(__file__).resolve().parent


@unittest.skipUnless(shutil.which("gpg"), "GPG not installed")
class EncryptedBackupTests(unittest.TestCase):
    def test_encrypts_to_verified_key_and_detects_tampered_checksum(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            home = root / "gnupg"
            home.mkdir(mode=0o700)
            bin_dir = root / "bin"
            bin_dir.mkdir()
            dump = bin_dir / "pg_dump"
            dump.write_text("#!/bin/sh\nprintf 'synthetic-backup-fixture'\n", encoding="utf-8")
            dump.chmod(0o700)
            env = {**os.environ, "GNUPGHOME": str(home), "PATH": str(bin_dir) + os.pathsep + os.environ["PATH"],
                   "PGDATABASE": "fixture", "PGUSER": "fixture", "PGPASSWORD": "fixture-only",
                   "PGHOST": "localhost", "BACKUP_DIR": str(root / "backup")}
            generated = subprocess.run(["gpg", "--batch", "--pinentry-mode", "loopback", "--passphrase", "",
                            "--quick-generate-key", "Synthetic Backup <backup@example.invalid>",
                            "default", "default", "1d"], env=env, capture_output=True, timeout=40)
            if generated.returncode and b"No agent running" in generated.stderr:
                self.skipTest("Execution environment cannot start gpg-agent; run on authorized maintenance host")
            generated.check_returncode()
            keys = subprocess.check_output(["gpg", "--batch", "--with-colons", "--list-keys"],
                                           env=env, text=True)
            env["BACKUP_GPG_RECIPIENT"] = next(line.split(":")[9] for line in keys.splitlines() if line.startswith("fpr:"))
            subprocess.run(["bash", str(SCRIPTS / "backup-postgres-encrypted.sh")], env=env,
                           check=True, capture_output=True, timeout=15)
            backup = next((root / "backup").glob("*.dump.gpg"))
            plaintext = subprocess.check_output(["gpg", "--batch", "--decrypt", str(backup)], env=env,
                                                stderr=subprocess.PIPE, timeout=15)
            self.assertEqual(plaintext, b"synthetic-backup-fixture")
            self.assertNotIn(plaintext, backup.read_bytes())
            checksum = Path(str(backup) + ".sha256")
            self.assertEqual(checksum.read_text().split()[0], hashlib.sha256(backup.read_bytes()).hexdigest())
            self.assertEqual(len(list((root / "backup").iterdir())), 2)

            checksum.write_text("0" * 64 + "  tampered\n", encoding="utf-8")
            rejected = subprocess.run(["bash", str(SCRIPTS / "verify-encrypted-restore.sh"), str(backup)],
                env={**env, "RESTORE_TARGET_DATABASE": "fixture_restore_test"}, capture_output=True, text=True, timeout=10)
            self.assertNotEqual(rejected.returncode, 0)
            self.assertIn("checksum", rejected.stderr.lower())

            rejected = subprocess.run(["bash", str(SCRIPTS / "backup-postgres-encrypted.sh")],
                env={**env, "PGHOST": "fixture-pooler.example.invalid"}, capture_output=True, text=True, timeout=10)
            self.assertNotEqual(rejected.returncode, 0)
            self.assertIn("conexão direta", rejected.stderr)


class EncryptedBackupGuards(unittest.TestCase):
    def test_rejects_pooler_and_tampered_backup_before_external_commands(self):
        with tempfile.TemporaryDirectory() as folder:
            env = {**os.environ, "PGDATABASE":"fixture", "PGUSER":"fixture", "PGPASSWORD":"test-only",
                   "BACKUP_GPG_RECIPIENT":"A"*40, "PGHOST":"fixture-pooler.example.invalid", "BACKUP_DIR":folder}
            rejected = subprocess.run(["bash", str(SCRIPTS / "backup-postgres-encrypted.sh")], env=env,
                text=True, capture_output=True, timeout=5)
            self.assertEqual(rejected.returncode, 2)
            self.assertIn("conexão direta", rejected.stderr)
            encrypted = Path(folder)/"fixture.dump.gpg"
            encrypted.write_bytes(b"synthetic fixture")
            Path(str(encrypted)+".sha256").write_text("0"*64+"  fixture\n",encoding="utf-8")
            rejected = subprocess.run(["bash",str(SCRIPTS / "verify-encrypted-restore.sh"),str(encrypted)],
                env={**env,"RESTORE_TARGET_DATABASE":"fixture_restore_test"},text=True,capture_output=True,timeout=5)
            self.assertEqual(rejected.returncode,2)
            self.assertIn("checksum",rejected.stderr.lower())


if __name__ == "__main__":
    unittest.main()
