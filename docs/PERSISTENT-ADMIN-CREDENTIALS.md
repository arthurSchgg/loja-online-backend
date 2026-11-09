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

## Ainda NÃO implementado / bloqueios para produção
- Recuperação por e-mail com token único de 15 minutos, hash, uso único e
  entrega por outbox segura (US-008).
- Notificações de segurança e alteração de e-mail com confirmação por link.
- Verificação de senhas comuns/comprometidas, além do comprimento.
- Endurecimento da política de sessão em infraestrutura distribuída,
  proteção de secrets, testes de migração PostgreSQL com dados pré-existentes.
- Homologação e aprovação formal AUTH-001.

Este recurso não equivale à conclusão da segurança administrativa do MVP.
