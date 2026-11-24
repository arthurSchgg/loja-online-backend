# Backup e recuperação — PostgreSQL

## Objetivo

Manter uma rotina verificável de exportação e restauração para a API da Lojinha da Kátia. A rotina de execução, cópia semanal e retenção foi implementada; **a execução agendada ainda depende de equipamento, destino e chave autorizados pela proprietária**. O snapshot pré-release no Neon foi restaurado e validado em 21/11/2026. Não substituir esse snapshot sem nova proteção confirmada.

## Backup manual

Pré-requisitos: PostgreSQL Client (`pg_dump`, `pg_restore`, `psql`), conta autorizada e ambiente protegido.

Configurar `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` de maneira segura no ambiente; **nunca deixar a senha em arquivos versionados**.

```bash
BACKUP_DIR=./backups bash scripts/backup-postgres.sh
```

O script gera arquivo `.dump` de formato custom (sem propriedade/ACL), valida a estrutura com `pg_restore --list` e produz checksum SHA-256. Os arquivos devem ser transferidos para armazenamento criptografado, com controle de acesso e retenção definida.

**Atenção:** um checksum é útil para detectar alteração acidental, mas não substitui criptografia, proteção contra exclusão, imutabilidade ou verificação real de restauração. Para dados reais, use o procedimento cifrado abaixo; o dump simples é adequado a ambientes fictícios isolados.

## Backup independente cifrado

Use a conexão direta do Neon (não o pooler), TLS com verificação de certificado e a **chave pública GPG** do responsável, cuja impressão digital deve ser conferida por um canal confiável. A chave privada permanece sob controle do responsável; não colocá-la no GitHub, Render ou no mesmo destino do backup. Nunca compartilhar senhas pelo chat.

```bash
export PGSSLMODE=verify-full
# Configure PGSSLROOTCERT com a CA confiável, PGHOST/PGUSER/PGDATABASE/PGPASSWORD
# e BACKUP_GPG_RECIPIENT com a impressão digital completa da chave pública.
BACKUP_DIR=/caminho/privado/independente bash scripts/backup-postgres-encrypted.sh
```

O dump é cifrado em fluxo, sem gravar o arquivo original em disco. O resultado é `.dump.gpg` e checksum relativo. No computador de manutenção, com a chave privada, teste a restauração para um banco novo e isolado:

```bash
RESTORE_TARGET_DATABASE=lojinha_restore_test bash scripts/verify-encrypted-restore.sh arquivo.dump.gpg
```

## Ativar a rotina diária e semanal

O projeto atualmente tem uma janela de recuperação Neon de seis horas e um snapshot manual pré-release preservado. Em 22/11/2026, o agendamento de snapshots estava vazio; o [Neon oferece snapshots automáticos em planos pagos](https://neon.com/docs/guides/backup-restore). Para não gerar cobranças, use a rotina independente abaixo em um computador de manutenção que tenha acesso autorizado ao banco.

1. Use Linux ou WSL Ubuntu no computador Windows do responsável. Instale o cliente PostgreSQL 16 ou posterior compatível, GnuPG, `python3` (3.10+) e certificados CA. Confirme a versão do servidor antes de um dump. Crie uma chave GPG própria em um equipamento confiável e faça uma cópia protegida da chave **privada**, separada dos backups. Importe e confira apenas a chave pública e sua impressão digital na conta que fará o backup. Não guarde a chave privada no equipamento de agendamento, no GitHub ou no Render.
2. Escolha dois diretórios **dedicados e privados**, um para os diários e outro em um disco separado ou sistema de arquivos externo montado, com espaço e acesso controlados. Os exemplos abaixo são caminhos ilustrativos; use seus caminhos reais. Os marcadores precisam ser criados com o disco externo efetivamente montado. Não crie um marcador em caminho vazio de montagem.

   ```bash
   mkdir -p "$HOME/lojinha-backups/diarios"
   chmod 700 "$HOME/lojinha-backups/diarios"
   printf 'lojinha-da-katia-backup-v1\n' > "$HOME/lojinha-backups/diarios/.lojinha-backup-daily-v1"
   # Com o disco externo autorizado já montado, prepare o diretório nele:
   # printf 'lojinha-da-katia-backup-v1\n' > /mnt/e/lojinha-backups/.lojinha-backup-weekly-v1
   ```

3. Em `~/.config/lojinha/backup.env` (fora de qualquer repositório, propriedade do usuário da tarefa, permissões `600` ou `400`), preencha variáveis **com valores reais do responsável**, nunca em commits ou no chat:

   | Variável | Finalidade |
   |---|---|
   | `PGHOST`, `PGPORT`, `PGUSER`, `PGDATABASE`, `PGPASSWORD` | Conexão dedicada autorizada do banco Neon, sem `-pooler`. |
   | `PGSSLMODE=verify-full`, `PGSSLROOTCERT` | Verificação TLS e CA confiável do sistema. |
   | `BACKUP_GPG_RECIPIENT` | Impressão digital completa da chave pública previamente conferida. |
   | `BACKUP_DAILY_DIR` | Caminho absoluto para o diretório diário marcado. |
   | `BACKUP_WEEKLY_DIR` | Caminho absoluto para o diretório externo marcado. |

   O arquivo é uma configuração Bash local confiável (linhas `CHAVE='valor'`); mantenha-o inacessível a outros usuários. Para backups sem interação, a chave pública deve estar disponível e ser confiável no chaveiro GPG do usuário da tarefa. A chave privada permanece fora dele.

4. Antes de agendar, execute manualmente a rotina e confira a cópia cifrada e o checksum nos dois destinos:

   ```bash
   bash scripts/run-scheduled-backup.sh "$HOME/.config/lojinha/backup.env"
   ```

   A rotina executa no fuso `America/Sao_Paulo`, guarda um pacote por data e copia para o destino externo na primeira execução bem-sucedida da semana ISO. Conserva os sete diários e quatro semanais válidos mais recentes. Se o disco externo estiver desconectado, o diário é preservado, o erro é retornado e a cópia semanal é tentada outra vez no próximo dia. O script recusa diretórios no mesmo sistema de arquivos, impede execuções concorrentes, nunca substitui o pacote diário/semanal existente e só remove pares completos antigos após verificar o mais novo. Pacotes incompletos ou desconhecidos são preservados para inspeção. A verificação SHA-256 não substitui o teste de restauração da próxima etapa.

5. Após a primeira execução bem-sucedida, use um agendador confiável: **diariamente às 03:00, horário de Brasília**. No Windows, use o Agendador de Tarefas sob o mesmo usuário do WSL: programa `wsl.exe`, argumentos `-d Ubuntu --exec bash /caminho/real/loja-online-backend/scripts/run-scheduled-backup.sh /home/usuario/.config/lojinha/backup.env` (adapte a distribuição e os caminhos). Configure execução no próximo horário disponível se o PC estiver desligado e não inicie uma segunda instância. No Linux, use cron ou um timer systemd do usuário com a mesma chamada de caminho absoluto. Confira código de saída e existência do pacote no primeiro dia após a configuração. Não há garantias de backup se o computador permanecer desligado.
6. Todo mês o responsável deve restaurar uma cópia semanal em um ambiente PostgreSQL local novo e isolado com a chave privada. **Não use a conexão de produção para a restauração.** Confira tabelas, preços/estoques, versão Flyway e acesso administrativo com dados protegidos; registre data, nome do pacote, sucesso e tempo da restauração. Preserve uma cópia semanal de restauração validada separadamente antes de qualquer alteração relevante. Remova o banco de testes somente após confirmar que é descartável e não faz parte de produção.

Os testes de rotação usam apenas arquivos sintéticos. `python3 -m unittest discover -s scripts -p 'test_backup*.py'` verifica retenção, destinos distintos, checksum, idempotência, concorrência, perda do disco e falhas sem substituir cópias. Nenhum dump de produção é enviado ao GitHub, ChatGPT ou outros destinos não autorizados.

## Testar restauração

No ambiente de restauração **local e isolado**, configure as variáveis `PG*` para esse ambiente (nunca as do Neon de produção) e escolha um nome de banco novo que termine em `_restore_test`, por exemplo `lojinha_restore_test`.

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
