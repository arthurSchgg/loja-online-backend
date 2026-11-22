# Arquitetura de referência — código atual e critérios ainda pendentes

> **Estado: revisão documental para aprovação, não homologação.**
> Código de referência: `develop` no commit
> `de93e2d14189548e57126e8a951a6d5b1086bcef` (PR #152).
> Ver [revisão de evidências da Fase 1](phase-1-evidence-review-2026-11-09.md).

## Arquitetura observada

Backend Java 21 com Spring Boot 3.5.7, Spring MVC, Spring Data JPA/Hibernate,
PostgreSQL, Flyway, Maven, Spring Security (sessão HTTP + CSRF), Springdoc OpenAPI,
Jakarta Validation e testes JUnit/MockMvc. Os módulos de catálogo, estoque,
promoções, experiências, serviços, conteúdo, auditoria e segurança ficam em
`src/main/java/br/com/lojinhadakatia/`; contratos HTTP usam controllers/DTOs,
regras de negócio em services e persistência em repositories e migrations.

O histórico de migrações vai **além de V6**, incluindo V13 (conta administrativa
persistente), V14 (recuperação/outbox) e V15 (alteração de e-mail).
Os testes CI validam Flyway em PostgreSQL e atualização com dados fictícios.
Não editar migrações já aplicadas: criar novas versões para alterações futuras.

## Segurança e fluxo do painel

- Administradora única com conta persistida; credenciais do ambiente
  somente fazem bootstrap quando não há conta existente.
- Login por formulário `POST /api/v1/auth/login`, sessão com cookie
  `JSESSIONID` HttpOnly, Secure em staging e SameSite=Lax.
- `GET /api/v1/auth/csrf` entrega o token; writes autenticados exigem CSRF.
- Sessão expira em 30 minutos de inatividade; alteração de credenciais
  incrementa versão persistida e invalida sessões antigas.
- Fluxos de recuperação e verificação de e-mail enviam links de uso único
  via outbox cifrado e SMTP; no CI, os e-mails são capturados pelo Mailpit.
- `SecurityConfiguration` nega rotas não autorizadas por padrão; leitura
  pública tem CORS restrito e sem credenciais; painel e API administrativa
  precisam compartilhar origem.
- Não há autenticação HTTP Basic nem JWT no contrato atual.

## Topologia por ambiente

- Local: Docker Compose com banco próprio e servidor HTTP restrito ao host.
- Staging de teste: Docker Compose, PostgreSQL, backend e Caddy com HTTPS
  local e Mailpit. O job `staging-https-smoke` valida certificado local,
  login, CSRF, sessão, logout e recuperação/troca de e-mail. Não é staging
  externo homologado.
- Produção: arquitetura AWS proposta em `docs/AWS-DEPLOYMENT.md`;
  infraestrutura, domínio, provedor SMTP/TLS, IAM, segredos, retenção e
  restauração exigem aprovação e implantação explícitas.

## Decisões que ainda exigem reconciliação

- `README.md` e documentos antigos em maiúsculas podem descrever escopo
  pré-estoque ou opções antigas Basic/JWT; a presente arquitetura deve
  ser comparada às decisões finais e aos contratos atuais.
- Produtos/promoções/estoque/WhatsApp possuem implementação em `develop`,
  mas a conformidade comercial e E2E frontend ainda não foi aprovada.
- Envio SMTP é *at least once*, não entrega exatamente uma vez; a operação
  de múltiplas réplicas exige solução compartilhada para sessões e limitação
  de abuso, se adotadas.
- A especificação normativa, RN únicas, sete lotes de issues,
  [AUTH-001](https://github.com/arthurSchgg/loja-online/issues/80)
  e HML-001–007 continuam exigindo evidência e assinatura.
