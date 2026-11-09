# Gate automático de staging HTTPS — autenticação administrativa

Este gate complementa o `mvn verify` e os testes PostgreSQL/Mailpit já existentes,
iniciando uma pilha **completa e descartável** de homologação local no GitHub Actions:
PostgreSQL, backend Java/Spring Boot, Mailpit (captura local) e proxy HTTPS Caddy.

O teste `scripts/smoke-staging-https.py` valida pelo caminho HTTPS real:

- Certificado emitido pela CA interna do Caddy, validado pelo cliente Python
  e pelo curl com `--cacert` (não ignora TLS).
- `GET /actuator/health` retorna UP.
- Acesso anônimo a `/api/v1/auth/me` e `/api/v1/admin/brands`
  é rejeitado com HTTP 401.
- `GET /api/v1/auth/csrf` entrega `X-CSRF-TOKEN`, e o cookie `JSESSIONID`
  possui `Secure`, `HttpOnly` e `SameSite=Lax`.
- Login por formulário, sem CSRF, não autentica (401 ou 403 conforme o contexto anônimo); com CSRF,
  retorna 200 e permite ler a identidade administrativa.
- Logout exige CSRF e, ao final, a sessão deixa de autenticar o usuário.

A stack de CI usa **apenas dados fictícios** e portas restritas ao loopback
(`18443` para HTTPS e `18025` para Mailpit). Credenciais e chave AES são
geradas no runner e salvas em arquivo temporário ignorado pelo Git;
nenhuma senha ou token é impressa pelo script.

A única exceção intencional é
`BREACHED_PASSWORD_CHECK_ENABLED=false` **nesse job descartável**:
sem isso, o bootstrap do teste dependeria da disponibilidade de um
serviço público de terceiros. O padrão do backend continua sendo
`BREACHED_PASSWORD_CHECK_ENABLED=true` e **não deve ser desativado** no
staging real sem alternativa equivalente aprovada.

## Como interpretar o resultado

Um CI verde confirma apenas os critérios automatizados acima.
**Não significa** que o frontend completo, a entrega externa SMTP/TLS, a
infraestrutura AWS, a gestão dos segredos, a restauração de um banco real,
a validação de acessibilidade ou a autorização de produção estejam completos.

As sete homologações formais devem ter evidências por fase, responsável
técnico e aprovação explícita em issues próprias, conforme
`docs/development-phases.md`. A issue `AUTH-001 #80` permanece bloqueante
até atender as pré-condições documentais.

Não efetuar merge do PR, marcar HML como aprovado nem executar deploy
automático a partir da aprovação do job.
