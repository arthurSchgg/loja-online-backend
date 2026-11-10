#!/usr/bin/env python3
"""Validate local Java package declarations and imports without Maven dependencies.

This is an early diagnostic, not a substitute for javac or Spring Boot tests.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ROOTS = (ROOT / "src/main/java", ROOT / "src/test/java")
PREFIX = "br.com.lojinhadakatia"
PACKAGE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
IMPORT = re.compile(r"^\s*import\s+(static\s+)?([\w.*]+)\s*;", re.MULTILINE)


def validate() -> list[str]:
    errors: list[str] = []
    files = [file for root in ROOTS if root.is_dir() for file in root.rglob("*.java")]
    sources: set[str] = set()
    packages: set[str] = set()
    declared: dict[str, Path] = {}
    for file in files:
        text = file.read_text(encoding="utf-8")
        match = PACKAGE.search(text)
        relative = file.relative_to(next(root for root in ROOTS if file.is_relative_to(root)))
        if not match:
            errors.append(f"{file.relative_to(ROOT)}: missing package declaration")
            continue
        package = match.group(1)
        expected = ".".join(relative.with_suffix("").parts)
        actual = package + "." + file.stem
        if expected != actual:
            errors.append(f"{file.relative_to(ROOT)}: package/path mismatch: {actual} != {expected}")
        if actual in declared:
            errors.append(f"duplicate Java source {actual}: {declared[actual]} and {file}")
        declared[actual] = file
        sources.add(actual)
        packages.add(package)

    for file in files:
        text = file.read_text(encoding="utf-8")
        for match in IMPORT.finditer(text):
            static = bool(match.group(1))
            imported = match.group(2)
            if not (imported == PREFIX or imported.startswith(PREFIX + ".")):
                continue
            # A nested type or static member may follow an existing top-level class.
            parts = imported.removesuffix(".*").split(".")
            if imported.endswith(".*") and not static:
                base = ".".join(parts)
                if base in packages or base in sources:
                    continue
            elif static:
                if any(".".join(parts[:index]) in sources for index in range(len(parts), 0, -1)):
                    continue
            elif any(".".join(parts[:index]) in sources for index in range(len(parts), 0, -1)):
                continue
            errors.append(f"{file.relative_to(ROOT)}: unresolved project import {imported}")
    print(f"Checked {len(files)} Java sources and {len(sources)} declared types.")
    return errors


if __name__ == "__main__":
    failures = validate()
    for message in failures:
        print("ERROR:", message, file=sys.stderr)
    if failures:
        print(f"Found {len(failures)} source-reference issue(s).", file=sys.stderr)
        sys.exit(1)
    print("Project package and import preflight: OK")
