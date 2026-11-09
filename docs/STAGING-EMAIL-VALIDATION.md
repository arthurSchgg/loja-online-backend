# Homologação local de e-mails administrativos — SMTP capturado

Este ambiente usa **Mailpit**, um servidor SMTP de testes que captura mensagens
sem enviá-las a endereços reais. Não exige conta de e-mail, nuvem ou custos de
provedor. A interface local é publicada apenas em `127.0.0.1:8025`;
a porta SMTP `1025` fica acessível somente entre contêineres do Compose.

**Não usar este Mailpit como SMTP de produção.** O SMTP da rede interna do
Compose não tem autenticação nem TLS e **não valida** um provedor real.
Ao migrar para staging remoto ou AWS, configurar SMTP/TLS do provedor autorizado
e impedir exposição da interface de testes à internet.

## Preparação segura

1. Copiar `.env.staging.example` para `.env.staging` (o arquivo é ignorado
   pelo Git), substituir `ADMIN_PASSWORD` e `DB_PASSWORD` por valores
   exclusivos para esse ambiente.
2. Gerar uma chave aleatória de **32 bytes** para `EMAIL_OUTBOX_KEY`,
   codificada em Base64, e colocá-la somente em `.env.staging`.
   Exemplo local: `python -c "import secrets,base64; print(base64.b64encode(secrets.token_bytes(32)).decode())"`.
   Guardar a chave enquanto houver mensagens pendentes; sem ela as funções
   de recuperação e alteração de e-mail retornam indisponibilidade.
3. Manter `ADMIN_EMAIL=admin-staging@example.invalid` (não é endereço real).
   Atenção: `ADMIN_EMAIL` é aplicado **apenas na primeira criação** da conta
   administrativa. Se o banco já contém a conta com outro e-mail, alterar essa
   conta pelo fluxo de confirmação ou usar uma instalação staging nova.
4. Usar `ADMIN_PASSWORD_RESET_URL` e `ADMIN_EMAIL_CHANGE_URL` com HTTPS.
   Os valores de exemplo apontam para páginas ainda não implementadas no
   frontend; durante este teste, o token pode ser usado diretamente na
   confirmação da API. Não copiar links para sistemas externos.

## Execução

```bash
docker compose --env-file .env.staging -f compose.staging.yaml up -d --build
docker compose --env-file .env.staging -f compose.staging.yaml ps
```

- Backend por proxy HTTPS: `https://localhost:8443` (certificado local).
- Captura de e-mails: `http://127.0.0.1:8025`, acessível apenas na máquina
  onde o Docker está rodando.
- SMTP entre backend e Mailpit: `mailpit:1025`, não publicado no host.

Obter CSRF no endpoint `GET /api/v1/auth/csrf`, preservando a mesma sessão
e cookie. Enviar `POST /api/v1/auth/password-reset/request` com
`{"email":"admin-staging@example.invalid"}` e o cabeçalho CSRF;
confirmar resposta `202 Accepted` e uma nova mensagem no Mailpit. Verificar
que a URL recebida contém um token temporário. Confirmar por
`POST /api/v1/auth/password-reset/confirm` usando o token, a nova senha e a
confirmação (com CSRF), e validar que o token não pode ser usado duas vezes.

Repetir para troca de e-mail autenticada: `POST /api/v1/admin/security/change-email`
deve exigir senha atual, sessão e CSRF; o endereço antigo continua ativo até
`POST /api/v1/auth/email-change/confirm`. Conferir alertas nos dois destinatários
de teste capturados pelo Mailpit. Usar outro e-mail `@example.invalid` como
destino temporário — nenhuma mensagem sai da rede Docker.

## Verificações automáticas

O CI liga um Mailpit isolado na própria máquina do runner e executa
`MailpitSmtpIntegrationTest` com `RUN_SMTP_INTEGRATION=true`. Esse teste
verifica que o corpo do e-mail é cifrado no banco e que o SMTP do backend
realmente entrega a mensagem ao capturador local. O CI também mantém testes
de falha/retry e a migração Flyway em PostgreSQL.

O repositório bloqueia a entrega concorrente do mesmo registro de outbox
usando lock pessimista no banco durante a transação de envio.
**Garantia real:** entrega no mínimo uma vez. Um crash após a aceitação
pelo SMTP, mas antes do commit do banco, ainda pode provocar reenvio.
Um provedor externo com mecanismo de deduplicação/idempotência será
necessário caso a garantia de entrega exata seja requisito de produção.

## Evidências exigidas antes da produção

- Registrar screenshots ou relatórios de homologação sem tokens e sem senhas.
- Testar SMTP **real** com TLS, autenticação, política de retry, prazos de
  expiração e mudança de chave; o teste com Mailpit não substitui esse passo.
- Verificar migração em cópia de banco existente, sessões, CSRF, frontend,
  documentação e aprovação AUTH-001 / HML-001–007.
- Não provisionar AWS, usar domínios reais nem enviar mensagens a clientes
  sem autorização específica.
