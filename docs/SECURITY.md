# Segurança administrativa

## Situação atual

A API utiliza HTTP Basic stateless e BCrypt para uma única conta administrativa configurada exclusivamente por variáveis de ambiente. Isso **não é JWT** e não implementa múltiplos administradores ou recuperação de senha. É uma proteção mínima para desenvolvimento e homologação; antes do lançamento público, a proprietária deve validar o mecanismo definitivo. Jamais disponibilize administração por HTTP sem TLS.

Defina `ADMIN_USERNAME` e `ADMIN_PASSWORD` (12 ou mais caracteres) para iniciar a aplicação. A API falha no startup se não estiverem configurados.

### Autorização

- `GET /api/v1/**`: consulta pública, exceto `/api/v1/admin/**`.
- `/api/v1/admin/**`: requer usuário com papel `ADMIN`.
- Qualquer outra rota: negada por padrão.
- CORS: origens declaradas em `CORS_ALLOWED_ORIGINS`. Não há cookies/sessões.
- Senhas não são persistidas no banco e não são registradas nos logs.

### Pendências antes de produção

Decidir sobre sessão ou JWT, proteção contra força bruta, auditoria, política de contas, CSRF caso cookies sejam adotados, domínio HTTPS, rotação de segredos e controle de acesso ao Swagger. Endpoint GET permitido por padrão deve ser refinado caso APIs privadas adicionais sejam criadas fora de `/admin`.
