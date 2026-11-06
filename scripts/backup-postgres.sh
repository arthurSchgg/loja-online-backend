#!/usr/bin/env bash
# Cria um backup PostgreSQL no formato custom sem revelar credenciais.
# Uso: PGHOST=... PGPORT=... PGUSER=... PGPASSWORD=... PGDATABASE=... bash scripts/backup-postgres.sh
set -euo pipefail
umask 077

: "${PGDATABASE:?Configure PGDATABASE}"
: "${PGUSER:?Configure PGUSER}"
: "${PGPASSWORD:?Configure PGPASSWORD}"

backup_dir="${BACKUP_DIR:-./backups}"
mkdir -p "$backup_dir"
timestamp="$(date -u +%Y%m%dT%H%M%SZ)"
backup_file="$backup_dir/${PGDATABASE}_$timestamp.dump"

pg_dump --format=custom --no-owner --no-acl --file="$backup_file" "$PGDATABASE"
pg_restore --list "$backup_file" >/dev/null
sha256sum "$backup_file" > "${backup_file}.sha256"

printf 'Backup verificado: %s\n' "$backup_file"
printf 'Checksum SHA-256: %s\n' "${backup_file}.sha256"
# O período de retenção e o armazenamento criptografado remoto dependem do provedor.
