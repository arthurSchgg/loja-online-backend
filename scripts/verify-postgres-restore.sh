#!/usr/bin/env bash
# Testa restauração SOMENTE em um banco novo cujo nome termine com _restore_test.
# Exemplo: RESTORE_TARGET_DATABASE=lojinha_restore_test bash scripts/verify-postgres-restore.sh ./backups/arquivo.dump
set -euo pipefail

backup_file="${1:?Informe o arquivo .dump a restaurar}"
target="${RESTORE_TARGET_DATABASE:?Configure RESTORE_TARGET_DATABASE}"
: "${PGUSER:?Configure PGUSER}"
: "${PGPASSWORD:?Configure PGPASSWORD}"

if [[ ! "$target" =~ ^[a-z][a-z0-9_]*_restore_test$ ]]; then
  echo "Nome de teste inválido: use um banco terminado em _restore_test." >&2
  exit 2
fi
if [[ ! -f "$backup_file" ]]; then
  echo "Arquivo de backup não encontrado." >&2
  exit 2
fi

# Não sobrescrever bancos existentes, mesmo bancos de teste.
if psql --dbname=postgres --tuples-only --no-align --command="SELECT datname FROM pg_database" | grep -Fxq "$target"; then
  echo "Banco de teste já existe; restauração bloqueada para não sobrescrever dados." >&2
  exit 2
fi

createdb --maintenance-db=postgres "$target"
pg_restore --no-owner --no-acl --exit-on-error --dbname="$target" "$backup_file"

# Confirma existência das tabelas essenciais e histórico de migrações.
check="$(psql --dbname="$target" --tuples-only --no-align --command="
SELECT COUNT(*) FROM (
    SELECT to_regclass('public.products') AS name
    UNION ALL SELECT to_regclass('public.brands')
    UNION ALL SELECT to_regclass('public.flyway_schema_history')
) AS tables WHERE name IS NOT NULL;")"
if [[ "$check" != "3" ]]; then
  echo "Restauração incompleta: faltam tabelas essenciais." >&2
  exit 1
fi
echo "Restauração validada no banco isolado: $target"
echo "Por segurança, o banco de teste não foi apagado automaticamente."
