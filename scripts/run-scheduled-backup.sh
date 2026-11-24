#!/usr/bin/env bash
# Load the owner's private local configuration; credentials never enter arguments.
set -euo pipefail
umask 077
[[ "$#" -eq 1 ]] || { echo 'Informe o caminho absoluto do arquivo privado de configuração.' >&2; exit 2; }
config="$1"
[[ "$config" == /* && -f "$config" && ! -L "$config" ]] || { echo 'Configuração inexistente ou não segura.' >&2; exit 2; }
owner="$(stat -c %u -- "$config")"
mode="$(stat -c %a -- "$config")"
[[ "$owner" == "$(id -u)" && ( "$mode" == 600 || "$mode" == 400 ) ]] || {
  echo 'A configuração deve pertencer ao usuário da tarefa e ter permissão 600 ou 400.' >&2; exit 2;
}
set -a
# shellcheck disable=SC1090
source "$config"
set +a
: "${BACKUP_DAILY_DIR:?Configure BACKUP_DAILY_DIR}"
: "${BACKUP_WEEKLY_DIR:?Configure BACKUP_WEEKLY_DIR}"
: "${PGHOST:?Configure a conexão direta do banco}"
exec python3 "$(dirname -- "$0")/rotate-encrypted-backups.py" \
  --daily-dir "$BACKUP_DAILY_DIR" --weekly-dir "$BACKUP_WEEKLY_DIR"
