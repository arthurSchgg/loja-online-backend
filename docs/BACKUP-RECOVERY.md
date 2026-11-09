# Backup e recuperação — PostgreSQL

## Objetivo

Manter uma rotina verificável de exportação e restauração para a API da Lojinha da Kátia. A automação disponibilizada **não configura backups periódicos em produção**: isso depende do provedor, da frequência aprovada, do volume do banco e das regras de retenção.

## Backup manual

Pré-requisitos: PostgreSQL Client (`pg_dump`, `pg_restore`, `psql`), conta autorizada e ambiente protegido.

Configurar `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` de maneira segura no ambiente; **nunca deixar a senha em arquivos versionados**.

```bash
BACKUP_DIR=./backups bash scripts/backup-postgres.sh
```

O script gera arquivo `.dump` de formato custom (sem propriedade/ACL), valida a estrutura com `pg_restore --list` e produz checksum SHA-256. Os arquivos devem ser transferidos para armazenamento criptografado, com controle de acesso e retenção definida.

**Atenção:** um checksum é útil para detectar alteração acidental, mas não substitui criptografia, proteção contra exclusão, imutabilidade ou verificação real de restauração.

## Testar restauração

Escolher um nome de banco novo que termine em `_restore_test`, por exemplo `lojinha_restore_test`.

```bash
RESTORE_TARGET_DATABASE=lojinha_restore_test bash scripts/verify-postgres-restore.sh ./backups/arquivo.dump
```

O script **recusa bancos existentes** e não apaga o banco recém-restaurado. Ele verifica três tabelas essenciais. Depois, a equipe pode executar testes de integridade, e somente então remover o banco isolado seguindo os procedimentos oficiais do PostgreSQL.

## Antes da liberação

- Decidir backup diário ou frequência adequada com a proprietária.
- Configurar retenção e armazenamento fora do servidor principal.
- Testar restauração, documentar RPO/RTO e responsabilidades.
- Verificar plano de resposta a indisponibilidade, acesso à conta administrativa e rotação de segredos.
- Garantir que dumps nunca sejam adicionados ao Git nem publicados como artefatos abertos de CI.
