# E-mail administrativo por HTTPS

O Render Free bloqueia as portas SMTP usuais. O backend oferece `smtp` (padrão) e `resend` (HTTPS, endpoint fixo `https://api.resend.com/emails`). O transporte escolhido não faz fallback para outro. O conteúdo continua cifrado no outbox, o cliente HTTP não segue redirecionamentos e cada envio leva uma chave de idempotência estável durante retries. A aplicação nunca registra o corpo da mensagem, resposta do provedor ou token. Referências: [Render Free](https://render.com/docs/free), [API de e-mail](https://resend.com/docs/api-reference/emails/send-email).

## Ativar na produção existente

1. A proprietária ou responsável deve criar/selecionar uma conta de envio autorizada e verificar o domínio/remetente oficial (não inventar um e-mail de contato da loja).
2. No serviço do Render, adicionar `EMAIL_TRANSPORT=resend`, `MAIL_FROM` com o remetente verificado e `RESEND_API_KEY` com uma chave de envio restrita. Inserir a chave diretamente em Environment, não no GitHub/chat. Manter a `EMAIL_OUTBOX_KEY` existente, pois ela cifra as mensagens pendentes. Não redefinir credenciais do banco ou admin.
3. Os links de produção já apontam às páginas `reset-password.html` e `confirm-email.html` na origem HTTPS do serviço. Para domínio personalizado, configurar `ADMIN_PASSWORD_RESET_URL` e `ADMIN_EMAIL_CHANGE_URL` com URLs oficiais HTTPS sem query/fragmento.
4. Confirmar que o e-mail persistido da administradora é o destinatário correto. `ADMIN_EMAIL` é usado apenas no bootstrap de uma base vazia; não muda a conta existente.
5. Após um deploy controlado, a responsável solicita uma recuperação real para sua própria caixa, verifica recebimento, expiração e troca do e-mail conforme a política. Não enviar testes a clientes.

O transporte HTTPS tem testes de payload, endpoint, retry e falha fechada. Os fluxos completos de autenticação, recuperação, rotação de senha, alteração de e-mail e revogação de sessão foram executados sobre HTTPS local com PostgreSQL descartável e Mailpit de captura (destinos `example.invalid`). A credencial de um provedor real, o domínio e o destinatário não foram inventados e a entrega em produção depende dessa configuração oficial. Não desabilitar a verificação de senhas comprometidas em produção.
