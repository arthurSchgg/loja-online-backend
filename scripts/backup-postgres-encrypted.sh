#!/usr/bin/env bash
# Independent encrypted logical backup. Use the owner's verified PUBLIC GPG key.
set -euo pipefail
umask 077
: "${PGDATABASE:?Configure PGDATABASE}"
: "${PGUSER:?Configure PGUSER}"
: "${PGPASSWORD:?Configure PGPASSWORD}"
: "${BACKUP_DIR:?Escolha um destino independente e privado}"
: "${BACKUP_GPG_RECIPIENT:?Configure a impressão digital da chave pública do responsável}"
[[ "$PGDATABASE" =~ ^[a-zA-Z0-9_]+$ ]] || { echo 'Nome de banco inválido.' >&2; exit 2; }
[[ "$BACKUP_GPG_RECIPIENT" =~ ^[0-9a-fA-F]{40,64}$ ]] || { echo 'Use a impressão digital completa verificada.' >&2; exit 2; }
if [[ "${PGHOST:-}" == *-pooler* ]]; then
  echo 'Use a conexão direta, não o pooler, para backup.' >&2; exit 2
fi
if [[ -n "${PGHOST:-}" && "$PGHOST" != localhost && "$PGHOST" != 127.0.0.1 && "$PGHOST" != /* && "${PGSSLMODE:-}" != verify-full ]]; then
  echo 'Para banco remoto, configure PGSSLMODE=verify-full e uma CA confiável.' >&2; exit 2
fi
mkdir -p -- "$BACKUP_DIR"
stamp="$(date -u +%Y%m%dT%H%M%SZ)"
temp="$(mktemp "$BACKUP_DIR/.${PGDATABASE}_${stamp}.XXXXXX")"
trap 'if [[ -n "${temp:-}" ]]; then rm -f -- "$temp"; fi' EXIT
final="${temp%/*}/${temp##*/.}.dump.gpg"
if [[ -e "$final" ]]; then echo 'Arquivo de backup já existe.' >&2; exit 1; fi
# No raw dump is written to disk. The caller must already trust the owner's key.
pg_dump --format=custom --no-owner --no-acl --dbname="$PGDATABASE" \
  | gpg --batch --yes --encrypt --recipient "$BACKUP_GPG_RECIPIENT" --output "$temp"
test -s "$temp"
mv -- "$temp" "$final"
temp=""
(cd "$(dirname -- "$final")" && sha256sum -- "$(basename -- "$final")" > "$(basename -- "$final").sha256")
printf 'Backup cifrado: %s\nValide uma restauração isolada com a chave privada do responsável.\n' "$final"
