#!/usr/bin/env bash
# Run only on the owner's trusted machine with their private key available.
set -euo pipefail
umask 077
artifact="${1:?Informe o arquivo .dump.gpg}"
: "${RESTORE_TARGET_DATABASE:?Configure um banco novo terminado em _restore_test}"
[[ "$RESTORE_TARGET_DATABASE" =~ ^[a-z][a-z0-9_]*_restore_test$ ]] || { echo 'Banco de teste inválido.' >&2; exit 2; }
[[ -f "$artifact" && -f "$artifact.sha256" ]] || { echo 'Backup ou checksum ausente.' >&2; exit 2; }
read -r expected _ < "$artifact.sha256"
[[ "$expected" =~ ^[0-9a-fA-F]{64}$ ]] || { echo 'Checksum inválido.' >&2; exit 2; }
actual="$(sha256sum -- "$artifact")"; actual="${actual%% *}"
[[ "$actual" == "$expected" ]] || { echo 'Backup não confere com o checksum.' >&2; exit 2; }
plain="$(mktemp)"
trap 'rm -f -- "$plain"' EXIT
gpg --batch --yes --decrypt --output "$plain" "$artifact"
pg_restore --list "$plain" >/dev/null
bash "$(dirname -- "$0")/verify-postgres-restore.sh" "$plain"
