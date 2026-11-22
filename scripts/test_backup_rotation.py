"""Test retention with synthetic ciphertext, never production data."""

from datetime import date, timedelta
import importlib.util
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch


SCRIPT_DIR = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("backup_rotation", SCRIPT_DIR / "rotate-encrypted-backups.py")
rotation = importlib.util.module_from_spec(spec)
spec.loader.exec_module(rotation)


def synthetic_bundle(destination):
    archive = destination / "fixture.dump.gpg"
    archive.write_bytes(b"synthetic ciphertext; no database credentials or records")
    proof = archive.with_name(archive.name + ".sha256")
    proof.write_text(f"{rotation.checksum(archive)}  {archive.name}\n", encoding="utf-8")


class RotationTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.daily = self.root / "daily"
        self.weekly = self.root / "external"
        for root, marker in ((self.daily, rotation.DAILY_MARKER), (self.weekly, rotation.WEEKLY_MARKER)):
            root.mkdir()
            (root / marker).write_text(rotation.MARKER_TEXT, encoding="utf-8")

    def create(self, root, name):
        result, _ = rotation.write_bundle(root, name, synthetic_bundle)
        return result

    def test_daily_and_weekly_idempotent(self):
        writes = []

        def writer(dest):
            writes.append(dest)
            synthetic_bundle(dest)

        # Use isolated test roots on the same temporary filesystem. Mount
        # independence is separately tested and enforced in the CLI.
        with patch.object(rotation, "require_independent"):
            rotation.run_rotation(self.daily, self.weekly, date(2026, 11, 22), writer)
            rotation.run_rotation(self.daily, self.weekly, date(2026, 11, 22), writer)
        self.assertEqual(len(writes), 1)
        d = self.daily / "daily-2026-11-22"
        w = self.weekly / "weekly-2026-W47"
        self.assertEqual(rotation.checksum(rotation.verify_bundle(d)[0]),
                         rotation.checksum(rotation.verify_bundle(w)[0]))

    def test_retains_seven_daily_and_preserves_unknown_or_broken(self):
        start = date(2026, 11, 1)
        for offset in range(9):
            self.create(self.daily, f"daily-{start + timedelta(days=offset)}")
        unrelated = self.daily / "old-family-photo.jpg"
        unrelated.write_bytes(b"do not touch")
        bad = self.daily / "daily-2026-10-30"
        bad.mkdir()
        (bad / "unrecognized.txt").write_text("do not remove")
        link = self.daily / "daily-2026-10-31"
        link.symlink_to(self.daily / "daily-2026-11-01", target_is_directory=True)
        rotation.rotate(self.daily, "daily", 7, date(2026, 11, 9))
        self.assertFalse((self.daily / "daily-2026-11-01").exists())
        self.assertFalse((self.daily / "daily-2026-11-02").exists())
        self.assertEqual(len([p for p in self.daily.glob("daily-2026-11-*") if p.is_dir()]), 7)
        self.assertTrue(unrelated.exists() and bad.exists() and link.is_symlink())

    def test_retains_four_iso_weeks(self):
        for week in range(43, 48):
            self.create(self.weekly, f"weekly-2026-W{week}")
        rotation.rotate(self.weekly, "weekly", 4, date.fromisocalendar(2026, 47, 1))
        self.assertFalse((self.weekly / "weekly-2026-W43").exists())
        self.assertEqual(len(list(self.weekly.glob("weekly-*"))), 4)

    def test_bad_today_never_replaces_or_rotates_existing_backups(self):
        old = self.create(self.daily, "daily-2026-11-21")
        current = self.create(self.daily, "daily-2026-11-22")
        archive, _ = rotation.verify_bundle(current)
        archive.write_bytes(b"damaged")
        with self.assertRaisesRegex(rotation.BackupError, "Checksum"):
            rotation.run_rotation(self.daily, self.weekly, date(2026, 11, 22), synthetic_bundle)
        self.assertTrue(old.exists() and current.exists())
        self.assertFalse(list(self.weekly.glob("weekly-*")))

    def test_missing_external_marker_keeps_daily_and_never_creates_weekly(self):
        (self.weekly / rotation.WEEKLY_MARKER).unlink()
        older = self.create(self.weekly, "weekly-2026-W46")
        with self.assertRaisesRegex(rotation.BackupError, "Marcador"):
            rotation.run_rotation(self.daily, self.weekly, date(2026, 11, 22), synthetic_bundle)
        self.assertTrue((self.daily / "daily-2026-11-22").exists())
        self.assertTrue(older.exists())
        self.assertFalse((self.weekly / "weekly-2026-W47").exists())

    def test_rejects_same_filesystem_and_nested_paths(self):
        with self.assertRaisesRegex(rotation.BackupError, "outro disco"):
            rotation.require_independent(self.daily, self.weekly)
        with self.assertRaisesRegex(rotation.BackupError, "independente"):
            rotation.require_independent(self.daily, self.daily / "nested")

    def test_failed_dump_never_publishes_a_partial_bundle(self):
        def failed(dest):
            (dest / "incomplete.dump.gpg").write_bytes(b"incomplete")
            raise subprocess.CalledProcessError(1, "synthetic")

        with self.assertRaises(subprocess.CalledProcessError):
            rotation.write_bundle(self.daily, "daily-2026-11-22", failed)
        self.assertFalse((self.daily / "daily-2026-11-22").exists())
        self.assertEqual(list(self.daily.iterdir()), [self.daily / rotation.DAILY_MARKER])

    def test_rejects_concurrent_run_and_future_clock_before_rotation(self):
        with rotation.locked(self.daily):
            with self.assertRaisesRegex(rotation.BackupError, "Outra execução"):
                with rotation.locked(self.daily):
                    self.fail("lock must not be re-entered")
        future = self.create(self.daily, "daily-2026-11-23")
        with self.assertRaisesRegex(rotation.BackupError, "Data do host"):
            rotation.rotate(self.daily, "daily", 7, date(2026, 11, 22))
        self.assertTrue(future.exists())

    def test_wrapper_refuses_world_readable_config(self):
        cfg = self.root / "backup.env"
        cfg.write_text("# synthetic non-secret fixture\n", encoding="utf-8")
        cfg.chmod(0o644)
        result = subprocess.run(["bash", str(SCRIPT_DIR / "run-scheduled-backup.sh"), str(cfg)],
                                capture_output=True, text=True, check=False)
        self.assertEqual(result.returncode, 2)
        self.assertIn("permissão 600 ou 400", result.stderr)


if __name__ == "__main__":
    unittest.main()
