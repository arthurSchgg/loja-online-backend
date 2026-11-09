# Credenciais persistentes do administrador (V13)

A partir da V13, a senha administrativa fica armazenada em `admin_credentials`
como hash PBKDF2 (não em texto puro), com verificação compatível de hashes BCrypt existentes. `ADMIN_USERNAME`, `ADMIN_PASSWORD` e
`ADMIN_EMAIL` servem apenas para **criar a primeira conta em banco vazio**.
Um reinício da aplicação **não redefine** a senha já gravada. É obrigatório
guardar um backup seguro do banco antes de migrar ou substituir a credencial do
ambiente, e validar a primeira autenticação com dados reais em homologação.

## Troca de senha
`POST /api/v1/admin/security/change-password`, com sessão administrativa e
token CSRF, recebe `currentPassword`, `newPassword`, `confirmPassword`.
A aplicação valida senha atual, confirmação, comprimento 12–128, política de senhas fracas/comprometidas e diferença
da senha anterior, atualiza o hash PBKDF2 e incrementa `credential_version`.
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
- Alteração de e-mail com confirmação por link foi implementada na V15; consultar `docs/ADMIN-EMAIL-CHANGE.md` para procedimentos de homologação.
- Alertas de login suspeito são enviados quando o limitador detecta repetidas falhas
  contra o nome da administradora, com supressão de duplicatas por 30 minutos por
  réplica. Em implantação distribuída, a deduplicação também deve ser compartilhada.
- Proteção distribuída contra abuso em múltiplas réplicas.
- Verificação local e consulta por k-anonimato de senhas comprometidas foram implementadas; validar conectividade e política de falha em staging. Consultar `docs/ADMIN-PASSWORD-POLICY.md` e `docs/ADMIN-BREACHED-PASSWORD-CHECK.md`.
- SMTP para Mailpit local já possui teste automático; ainda faltam testes com provedor real, transporte TLS e recuperação operacional do outbox após falha;
  revisar se os e-mails são entregues antes da expiração do token.
- Endurecimento da política de sessão em infraestrutura distribuída e proteção de secrets.
- Testes de upgrade com dados fictícios já existentes em PostgreSQL foram adicionados; resta homologar uma cópia restaurada de banco real, sem alterar produção.
- Homologação e aprovação formal AUTH-001.

**Estado:** funcionalidades V13–V15 integradas em `develop` após PRs aprovados, com testes automatizados.
A documentação técnica não equivale à homologação com SMTP real, ambiente de staging, autorização formal ou release de produção.
