#!/usr/bin/env python3
"""Daily encrypted dump and weekly independent copy. No production writes.

Run on a trusted Linux/WSL maintenance host. Roots must already exist, contain
their respective markers, and be on different mounted filesystems. Only this
script's validated, dated two-file bundles are eligible for rotation.
"""

import argparse
import contextlib
from datetime import date, datetime
import fcntl
import hashlib
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile
from zoneinfo import ZoneInfo


SCRIPTS = Path(__file__).resolve().parent
DAILY_MARKER = ".lojinha-backup-daily-v1"
WEEKLY_MARKER = ".lojinha-backup-weekly-v1"
MARKER_TEXT = "lojinha-da-katia-backup-v1\n"


class BackupError(RuntimeError):
    pass


def checked_root(path, marker):
    path = Path(path)
    if not path.is_absolute() or not path.is_dir() or path.is_symlink():
        raise BackupError(f"Destino absoluto, existente e não simbólico obrigatório: {path}")
    root = path.resolve(strict=True)
    if root == Path(root.anchor):
        raise BackupError("A raiz de um disco não pode ser o diretório de backup.")
    proof = root / marker
    if proof.is_symlink() or not proof.is_file() or proof.read_text(encoding="utf-8") != MARKER_TEXT:
        raise BackupError(f"Marcador do destino ausente ou incorreto: {marker}")
    return root


def require_independent(daily, weekly):
    if daily == weekly or daily in weekly.parents or weekly in daily.parents:
        raise BackupError("O destino semanal precisa ser independente do diário.")
    if daily.stat().st_dev == weekly.stat().st_dev:
        raise BackupError("O destino semanal deve estar em outro disco ou sistema de arquivos montado.")


def checksum(path):
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for block in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def verify_bundle(bundle):
    if bundle.is_symlink() or not bundle.is_dir():
        raise BackupError(f"Pacote inválido: {bundle.name}")
    entries = list(bundle.iterdir())
    archives = [entry for entry in entries if entry.name.endswith(".dump.gpg")]
    if len(entries) != 2 or len(archives) != 1:
        raise BackupError(f"O pacote deve conter apenas arquivo cifrado e checksum: {bundle.name}")
    encrypted = archives[0]
    proof = encrypted.with_name(encrypted.name + ".sha256")
    if any(entry.is_symlink() or not entry.is_file() for entry in (encrypted, proof)):
        raise BackupError(f"Arquivo ou checksum inválido: {bundle.name}")
    if encrypted.stat().st_size == 0 or proof.stat().st_size > 4096:
        raise BackupError(f"Arquivo vazio ou checksum inválido: {bundle.name}")
    parts = proof.read_text(encoding="utf-8").split()
    if (len(parts) != 2 or not re.fullmatch(r"[a-fA-F0-9]{64}", parts[0])
            or parts[1].lstrip("*") != encrypted.name or checksum(encrypted) != parts[0].lower()):
        raise BackupError(f"Checksum não confere: {bundle.name}")
    return encrypted, proof


def write_bundle(root, name, populate):
    target = root / name
    if target.exists() or target.is_symlink():
        verify_bundle(target)
        return target, False
    # Stage on the destination filesystem; never put an unverified bundle under
    # its final name, and never overwrite a dated bundle.
    staging = Path(tempfile.mkdtemp(prefix=".pending-", dir=root))
    try:
        populate(staging)
        verify_bundle(staging)
        if target.exists() or target.is_symlink():
            raise BackupError(f"Pacote apareceu durante a criação: {name}")
        staging.rename(target)
        verify_bundle(target)
        return target, True
    finally:
        if staging.is_dir() and not staging.is_symlink():
            shutil.rmtree(staging)


def dump_encrypted(staging):
    subprocess.run(["bash", str(SCRIPTS / "backup-postgres-encrypted.sh")],
                   env={**os.environ, "BACKUP_DIR": str(staging)}, check=True)


def copy_bundle(source, staging):
    archive, proof = verify_bundle(source)
    for path in (archive, proof):
        shutil.copyfile(path, staging / path.name)
    copy, _ = verify_bundle(staging)
    if checksum(copy) != checksum(archive):
        raise BackupError("A cópia externa não corresponde à origem.")


def bundle_date(name, kind):
    if kind == "daily" and re.fullmatch(r"daily-\d{4}-\d{2}-\d{2}", name):
        try:
            return date.fromisoformat(name.removeprefix("daily-"))
        except ValueError:
            return None
    match = re.fullmatch(r"weekly-(\d{4})-W(\d{2})", name) if kind == "weekly" else None
    if match:
        try:
            return date.fromisocalendar(int(match[1]), int(match[2]), 1)
        except ValueError:
            return None
    return None


def rotate(root, kind, keep, current):
    verified = []
    for item in root.iterdir():
        when = bundle_date(item.name, kind)
        if when is None or item.is_symlink() or not item.is_dir():
            continue
        try:
            verify_bundle(item)
        except (OSError, BackupError, UnicodeError) as error:
            print(f"Preservado para inspeção: {item.name} ({error})", file=sys.stderr)
            continue
        verified.append((when, item))
    verified.sort()
    # A backwards clock must not cause an older run to expire newer copies.
    if not verified or verified[-1][0] > current:
        raise BackupError("Data do host inconsistente; não foi feita rotação.")
    for _, old in verified[:-keep]:
        archive, proof = verify_bundle(old)
        # Never recursively delete an old directory or any unexpected content.
        archive.unlink()
        proof.unlink()
        old.rmdir()
        print(f"Retenção aplicada: {old.name}")


@contextlib.contextmanager
def locked(root):
    lock = root / ".backup.lock"
    flags = os.O_CREAT | os.O_WRONLY | getattr(os, "O_NOFOLLOW", 0)
    fd = os.open(lock, flags, 0o600)
    try:
        try:
            fcntl.flock(fd, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except BlockingIOError as error:
            raise BackupError("Outra execução já está em andamento.") from error
        yield
    finally:
        os.close(fd)


def run_rotation(daily_dir, weekly_dir, today, writer=dump_encrypted):
    daily = checked_root(daily_dir, DAILY_MARKER)
    with locked(daily):
        current, created = write_bundle(daily, f"daily-{today.isoformat()}", writer)
        print(f"Cópia diária {'criada' if created else 'já conferida'}: {current.name}")
        rotate(daily, "daily", 7, today)

        # A disconnected external disk must not prevent the daily copy. Report
        # the weekly failure and retry on the next run, without manufacturing
        # a directory at an unmounted path or removing old weekly copies.
        weekly = checked_root(weekly_dir, WEEKLY_MARKER)
        require_independent(daily, weekly)
        year, week, _ = today.isocalendar()
        week_start = date.fromisocalendar(year, week, 1)
        external, created = write_bundle(
            weekly, f"weekly-{year}-W{week:02d}", lambda dest: copy_bundle(current, dest))
        print(f"Cópia semanal {'criada' if created else 'já conferida'}: {external.name}")
        rotate(weekly, "weekly", 4, week_start)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--daily-dir", required=True, type=Path)
    parser.add_argument("--weekly-dir", required=True, type=Path)
    parser.add_argument("--timezone", default="America/Sao_Paulo")
    args = parser.parse_args()
    os.umask(0o077)
    try:
        run_rotation(args.daily_dir, args.weekly_dir, datetime.now(ZoneInfo(args.timezone)).date())
    except (BackupError, OSError, ValueError, subprocess.CalledProcessError) as error:
        print(f"Rotina de backup incompleta: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
