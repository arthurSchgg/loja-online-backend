# Render Free + Neon PostgreSQL — preparação de deploy

**Status:** configuração versionada e Blueprint planejado no Render, **ainda não aprovada/implantada**. O banco Neon já existe, mas está vazio. Nenhum dado comercial foi publicado.

## Recursos já criados

- Neon Free: projeto `lojinha-da-katia` (projeto `bold-star-03713670`), PostgreSQL 16 em `aws-us-east-1`.
- Banco: `lojinha_da_katia`, role principal: `lojinha`. Migrations Flyway ainda **não foram executadas**.
- Render Blueprint (somente plano Free): https://dashboard.render.com/blueprint/exs-dbuaaqmc6qcc73c52lfg/sync/exe-dbuaar6c6qcc73c52n7g
- Serviço proposto: `lojinha-da-katia-api-rjvx`, Docker e região Virginia, autodeploy desativado.
- Frontend: branch `feature/DEPLOY-001-cloudflare-pages` do repositório `loja-online-frontend`, inclui Pages Function para API pública same-origin.

## Segredos do Render

No Dashboard de Blueprint, insira as quatro variáveis obrigatórias somente nos campos protegidos **após revisar o plano**:

| Variável | Uso |
|---|---|
| `DB_URL` | `jdbc:postgresql://<host-direto-neon>:5432/lojinha_da_katia?sslmode=require`. Usar conexão **direta**, não a conexão `-pooler`, porque Flyway migra o schema na inicialização. |
| `DB_PASSWORD` | Senha do usuário `lojinha` obtida no Neon. Não copiar para GitHub. |
| `ADMIN_PASSWORD` | Senha administrativa forte, exclusiva, 12+ caracteres, criada pela pessoa responsável. |
| `ADMIN_EMAIL` | Endereço da administradora real, aprovado para receber alertas e recuperação. |

O render.yaml já declara `DB_USERNAME=lojinha`, `ADMIN_USERNAME=admin`, `SPRING_PROFILES_ACTIVE=prod`, `SESSION_COOKIE_SECURE=true`, `BREACHED_PASSWORD_CHECK_ENABLED=true`, `CORS_ALLOWED_ORIGINS=""`. Outbox key é gerada no Render, não versionada. Nenhuma senha deve aparecer em logs, issues, prints ou chat.

A URL HTTPS pública de produção será fornecida pelo Render após implantação; não presumir que nomes de preview sejam URLs definitivas.

## Operação e revisão

1. Conferir limite Free, espaço disponível e política de suspensão do Render; quotas Free e tempos de cold start podem prejudicar a vitrine.
2. Não aprovar o Blueprint antes de resolver falhas de CI e os gates de homologação. O Blueprint continua sem sincronização automática.
3. Confirmar que `/actuator/health` retorna 200 quando o serviço inicia; conferir Flyway schema no Neon sem expor credenciais.
4. Não criar tabelas manualmente no Neon. O Spring Boot usa Flyway em `prod`; caso migrações falhem, corrigir schema/código e reexecutar em **branch de banco de homologação** primeiro.
5. Conferir `GET /api/v1/products` e endpoints de admin rejeitando acesso anônimo. Cadastro de produtos Skelt deve ser controlado e auditado; fotos da gravação são provisórias.
6. Para envio real de e-mails administrativos (recuperação de senha e confirmação de e-mail), configurar provedor SMTP apropriado antes de uso comercial. Sem SMTP, a recuperação não estará funcional.
7. O painel admin precisa **mesma origem HTTPS que a API** (issue backend #180). A API pode ser implantada separadamente para teste, mas isso não torna o painel remoto utilizável. Não desabilitar CSRF/cookies nem expor credenciais para facilitar a integração.
8. Fazer backup, testar recuperação, validar segurança, performance e critérios de release antes de usar dados comerciais reais.
9. Cloudflare Pages terá `API_BASE_URL=/api/v1` como variável de build e `PUBLIC_API_ORIGIN=https://<render-host>` como variável de Pages Functions. O proxy público bloqueia `/admin`, `/auth` e sessões; a função não precisa receber secrets.

## Para diagnosticar

- Render → Events / Deploy Logs; não compartilhar valores secretos de environment.
- Neon → SQL Editor: visualizar `flyway_schema_history` só **depois** do primeiro boot bem-sucedido.
- Com ambiente de teste autorizado, `https://<render-host>/actuator/health` e `https://<cloudflare-host>/api/v1/products`.

A hospedagem em GitHub/Render não copia automaticamente os dados locais do Docker. Não execute restauração do banco local em produção sem revisar credenciais, dados, aprovações e backup.
