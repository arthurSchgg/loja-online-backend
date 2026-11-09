# Alteração verificada de e-mail administrativo — V15

A alteração do e-mail da administradora **não é imediata**: requer senha atual,
CSRF e comprovação de acesso ao novo endereço.

## Contrato

1. Enviar `POST /api/v1/admin/security/change-email` com sessão admin,
   `X-CSRF-TOKEN` e JSON `{"currentPassword":"...","newEmail":"..."}`.
   Retorna **202** quando o pedido é registrado. O e-mail anterior continua ativo.
2. O novo e-mail recebe um link de confirmação HTTPS com token aleatório de 256 bits,
   válido por **15 minutos**. O banco armazena apenas SHA-256 do token, e a mensagem
   de outbox é cifrada com AES-256-GCM. O e-mail antigo recebe aviso da solicitação.
3. A interface de confirmação solicita `GET /api/v1/auth/csrf` e faz
   `POST /api/v1/auth/email-change/confirm` com
   `{"token":"..."}` e `X-CSRF-TOKEN`. Retorna **204**.
4. Somente após confirmação o novo e-mail é salvo. A operação incrementa
   `credential_version`, revogando links de recuperação emitidos anteriormente
   e sessões anteriores. O token não pode ser reutilizado.
5. O endereço anterior e o novo recebem avisos da confirmação.

## Configuração

- `ADMIN_EMAIL_CHANGE_URL`: endereço **HTTPS**, sem query string nem fragmento,
  para a página frontend que confirma o token.
- Configurações SMTP/TLS, `MAIL_FROM` e `EMAIL_OUTBOX_KEY` devem estar disponíveis,
  conforme `docs/PERSISTENT-ADMIN-CREDENTIALS.md`.
- Nenhum link de confirmação ou chave criptográfica deve aparecer em logs.
- Confirmar que e-mails pendentes chegam **antes** de 15 minutos em homologação.
- Implantar Flyway V15 com backup e restauração verificada.

## Limites conhecidos

- Solicitação requer sessão autenticada e senha atual, mas não possui
  rate limit próprio além dos limites de autenticação e da infraestrutura.
- Em múltiplas réplicas, gerenciar sessões e limite de tentativas com armazenamento
  compartilhado.
- SMTP real, troca de endereço com provedor externo e restauração de backup
  ainda exigem homologação.
- A aprovação formal AUTH-001 continua independente da implementação técnica.
