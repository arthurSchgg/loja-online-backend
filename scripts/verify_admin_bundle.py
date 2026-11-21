#!/usr/bin/env python3
"""Verify the versioned, generated same-origin admin artifact without secrets."""
from hashlib import sha256
from pathlib import Path, PurePosixPath

ROOT = Path(__file__).resolve().parent.parent
STATIC = ROOT / "src/main/resources/static"
MANIFEST = ROOT / "docs/ADMIN-BUNDLE-MANIFEST.sha256"
PAGES = {"admin.html", "admin-experiences.html", "reset-password.html", "confirm-email.html"}
SHARED = {"assets/css/base.css", "assets/css/tokens.css", "assets/css/components.css",
          "assets/images/brand/logo-katia.svg"}

expected = {}
for line in MANIFEST.read_text(encoding="utf-8").splitlines():
    digest, relative = line.split("  ", 1)
    path = PurePosixPath(relative)
    if path.is_absolute() or ".." in path.parts or len(digest) != 64:
        raise SystemExit("Invalid admin bundle manifest entry")
    if relative in expected:
        raise SystemExit("Duplicate admin bundle manifest entry")
    expected[relative] = digest

actual = {path.relative_to(STATIC).as_posix() for path in STATIC.rglob("*") if path.is_file()}
if actual != set(expected) or not (PAGES | SHARED).issubset(actual):
    raise SystemExit("Missing or unexpected files in same-origin admin bundle")
for relative in sorted(actual):
    path = STATIC / relative
    if path.is_symlink() or sha256(path.read_bytes()).hexdigest() != expected[relative]:
        raise SystemExit("Unexpected admin bundle hash: " + relative)
    if relative not in PAGES | SHARED and not (
        relative.startswith("admin/") and len(PurePosixPath(relative).parts) == 2
        and path.suffix in {".js", ".css"}
    ):
        raise SystemExit("Non-admin/static content in restricted bundle: " + relative)
    if relative in PAGES:
        source = path.read_text(encoding="utf-8")
        if "script-src 'self'" not in source or "connect-src 'self'" not in source:
            raise SystemExit("Required CSP missing: " + relative)
print(f"Same-origin admin bundle: {len(actual)} files validated; public catalog excluded.")
