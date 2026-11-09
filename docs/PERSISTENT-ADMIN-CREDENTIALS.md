# Credenciais persistentes do administrador (V13)

A partir da V13, a senha administrativa fica armazenada em `admin_credentials`
como hash bcrypt (não em texto puro). `ADMIN_USERNAME`, `ADMIN_PASSWORD` e
`ADMIN_EMAIL` servem apenas para **criar a primeira conta em banco vazio**.
Um reinício da aplicação **não redefine** a senha já gravada. É obrigatório
guardar um backup seguro do banco antes de migrar ou substituir a credencial do
ambiente, e validar a primeira autenticação com dados reais em homologação.

## Troca de senha
`POST /api/v1/admin/security/change-password`, com sessão administrativa e
token CSRF, recebe `currentPassword`, `newPassword`, `confirmPassword`.
A aplicação valida senha atual, confirmação, comprimento 12–128 e diferença
da senha anterior, atualiza bcrypt e incrementa `credential_version`.
A sessão que efetuou a mudança é invalidada; sessões autenticadas pelo
formLogin e portadoras da versão anterior são rejeitadas no próximo acesso.
Não expor senhas em logs ou métricas.

**Atenção:** sessões criadas antes da implantação deste mecanismo não possuem
atributo de versão: planeje encerrá-las ao atualizar o backend (reinício
da aplicação ou invalidação das sessões). Em implantação com várias réplicas,
as sessões devem ser compartilhadas ou fixadas de forma segura.

## Recuperação de senha (V14)
- `POST /api/v1/auth/password-reset/request` recebe `{ "email": "..." }`, responde 202
  sem revelar se a conta existe. Há limite por origem e e-mail em memória.
- `POST /api/v1/auth/password-reset/confirm` recebe `{ "token": "...",
  "newPassword": "...", "confirmPassword": "..." }` e responde 204 se válido.
- Ambos os endpoints exigem CSRF; obtenha o token em `GET /api/v1/auth/csrf`.
- O token tem 256 bits aleatórios, é armazenado apenas como hash SHA-256,
  expira em 15 minutos, é de uso único e se torna inválido após qualquer mudança de senha.
- A mensagem com link é criptografada com AES-256-GCM no outbox SQL e enviada por
  worker SMTP com retentativas; não guardar a chave no banco nem no repositório.
- Configure `ADMIN_EMAIL`, `ADMIN_PASSWORD_RESET_URL` (HTTPS e sem query string),
  `EMAIL_OUTBOX_KEY` (32 bytes aleatórios codificados em Base64), `MAIL_FROM`,
  e parâmetros `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME` e
  `SPRING_MAIL_PASSWORD`, além de TLS do servidor de e-mail.
- Se envio ou configuração não estiverem disponíveis, a solicitação é recusada com
  503 em vez de emitir links que não possam ser enviados.
- Mantenha `EMAIL_OUTBOX_KEY` estável enquanto existirem mensagens pendentes.
  Rotacionar a chave requer estratégia de recriptografia e recuperação das mensagens.

## Ainda NÃO implementado / bloqueios para produção
- Alteração de e-mail com confirmação por link e avisos de atividades suspeitas.
- Proteção distribuída contra abuso em múltiplas réplicas.
- Verificação de senhas comuns/comprometidas, além do comprimento.
- Testes reais de SMTP, transporte TLS e recuperação do outbox após falha;
  revisar se os e-mails são entregues antes da expiração do token.
- Endurecimento da política de sessão em infraestrutura distribuída,
  proteção de secrets, testes de migração PostgreSQL com dados pré-existentes.
- Homologação e aprovação formal AUTH-001.

**Estado:** implementado em branch isolada para CI e revisão, sem merge ou homologação.
Não é conclusão da segurança administrativa do MVP.
