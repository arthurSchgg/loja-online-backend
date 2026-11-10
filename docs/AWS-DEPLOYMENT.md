# AWS — arquitetura e roteiro de implantação

> **Estado:** preparado tecnicamente para implantação, **não publicado**. A conta AWS, domínio e detalhes comerciais permanecem a definir. Nenhum recurso com cobrança foi criado.

## Decisão de serviços (novembro de 2026)
- **API backend:** Amazon ECS **Express Mode** com contêiner construído a partir do `Dockerfile`, imagem privada no **Amazon ECR**, HTTPS/TLS e Auto Scaling. Evitar AWS App Runner para um projeto novo: a AWS encerrou novos cadastros do serviço em abril/2026.
- **Banco:** Amazon **RDS PostgreSQL** privado, conexão de ECS pelo security group, credenciais no Secrets Manager, backups/retention planejados; configurar a automação de exportação externa/cifragem separadamente para cumprir a política de quatro cópias semanais.
- **Frontend:** repositório GitHub separado, **AWS Amplify Hosting** para HTML/CSS/JS estático. O DNS e distribuição devem oferecer **mesma origem ao frontend administrativo e à API administrativa**. Se a vitrine pública tiver origem Amplify distinta, ela só acessa endpoints públicos via CORS de leitura. Não habilitar login administrativo com cookies cross-origin até configurar o domínio único.
- **Domínio:** CloudFront com origem para assets estáticos e roteamento `/api/*`, `/actuator/health` para a API; **não cachear** caminhos da API nem respostas com Set-Cookie; encaminhar cookies, headers e query strings requeridos (incluindo CSRF). O painel e login devem estar na mesma origem do backend.
- **Operação:** CloudWatch Logs, alertas essenciais, AWS Budgets, Secrets Manager e retenção; acesso administrativo por IAM de menor privilégio.

Links AWS oficiais:
- https://docs.aws.amazon.com/AmazonECS/latest/developerguide/express-service-overview.html
- https://docs.aws.amazon.com/AmazonECS/latest/developerguide/express-service-getting-started.html
- https://docs.aws.amazon.com/amplify/latest/userguide/
- https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html

## Pré-requisitos e custos
- Uma conta AWS apta a operar, com cobrança configurada e limites de orçamento/alerta. **ECS Express Mode não tem tarifa adicional**, mas cobra Fargate, ALB, CloudWatch, rede; RDS, ECR, Amplify e transferência cobram à parte. Os valores variam por região, capacidade e uso: simular no AWS Pricing Calculator **antes de criar**.
- Região preferencial proposta: `sa-east-1` (São Paulo), **sujeita a cotação**, disponibilidade de capacidade e aprovação por quem paga.
- Definir domínio real e quem gerencia DNS. Não criar/substituir dados comerciais falsos.
- Configurar conta/projeto frontend separado antes de ligar o Amplify.
- Manter apenas uma réplica do backend **enquanto as sessões ficam em memória**. Para várias réplicas, implementar/revisar Spring Session + Redis ou arquitetura de sessões equivalente. Sem isso, sessões podem desaparecer ao mudar de réplica.

## Banco RDS
1. Criar banco PostgreSQL com `Public access = No` em sub-redes privadas.
2. Limitar inbound do security group do RDS na porta 5432 **somente ao security group do ECS**. Sem abertura `0.0.0.0/0`.
3. Usuário de aplicação exclusivo, senha no Secrets Manager, TLS de conexão devidamente validado. Usar hostname de RDS, não IP fixo.
4. Habilitar backups automáticos, retenção e restauração point-in-time conforme orçamento; configurar exportação cifrada externa e ensaios mensais para completar os requisitos.
5. Aplicar Flyway com controle de concorrência durante deploy; revisar snapshots antes de qualquer migração com risco de perda.

## Contêiner/ECS
1. Criar repositório Amazon ECR privado `lojinha-backend` com scan e retenção de imagens.
2. Criar IAM role de execução de task e role de infraestrutura do ECS Express Mode conforme documentação oficial. Evitar chaves AWS estáticas.
3. Configurar vCPU/memória/porta **8080**, health path `/actuator/health` e pelo menos uma task.
4. Injetar segredos e variáveis de ambiente; não gravar credenciais no Dockerfile nem logs.
5. Configurar acesso privado ao RDS, verificar rota de rede e banco. Para pull da imagem/Secrets Manager usar conexões/roles adequadas.
6. Testar HTTPS, cookies HttpOnly+Secure, CSRF, rotas públicas e bloqueio de `/api/v1/admin/**` sem sessão.
7. Registrar imagem por digest, release/tag, backup pré-migração e plano de rollback antes de publicar alterações.

## Variáveis de ambiente necessárias
| Nome | Origem | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE=prod` | ECS task | ativa configurações prod |
| `DB_URL` | configuração segura | `jdbc:postgresql://RDS_HOST:5432/DB_NAME?sslmode=verify-full` com CA apropriada no truststore |
| `DB_USERNAME` | Secrets Manager | conta exclusiva |
| `DB_PASSWORD` | Secrets Manager | segredo |
| `ADMIN_USERNAME` | Secrets Manager | administradora única |
| `ADMIN_PASSWORD` | Secrets Manager | senha forte (mín. 12), nunca commit |
| `CORS_ALLOWED_ORIGINS` | ECS config | origens HTTPS exatas apenas da vitrine pública |
| `SERVER_PORT=8080` | ECS config | porta do Spring |
| `SESSION_COOKIE_SECURE=true` | ECS config | cookie somente via HTTPS |

**Importante:** fluxos de recuperação de senha, confirmação de e-mail e outbox já possuem implementação e testes automatizados no backend. Isso **não** confirma integração com SMTP/TLS real, configuração do provedor, entregabilidade, segredos, cookies e origem de produção. O painel continua bloqueado para publicação até testes E2E e aprovação da homologação.

## Build local
```bash
mvn -B verify
docker build -t lojinha-backend:local .
docker run --rm --env-file .env -e SPRING_PROFILES_ACTIVE=prod -p 127.0.0.1:8080:8080 lojinha-backend:local
```
Esse comando apenas testa o contêiner, não cria AWS.

## Pipeline de publicação (deliberadamente manual)
O arquivo `.github/workflows/aws-ecr-publish.yml` publica uma imagem **somente** quando acionado manualmente na branch `main`, após revisão, e requer GitHub Environment `production` com reviewers. Ele **não cria/atualiza ECS automaticamente**: deploy é uma operação separada depois das homologações.

Pré-configuração GitHub Actions (variables/secrets não versionados):
- Environment `production` com restrição à branch `main` e aprovadores.
- Variables: `AWS_REGION`, `AWS_ECR_REPOSITORY`; secret `AWS_ROLE_ARN` (ARN de role OIDC de permissão mínima).
- Trust policy AWS restrita ao repo e environment, com `aud=sts.amazonaws.com`; permissão apenas ECR Push para o repositório esperado.
- A action deve usar somente OIDC e não credenciais AWS long-lived.

## Frontend (repositório GitHub separado)
- Frontend já versionado em repositório GitHub privado separado: [`arthurSchgg/loja-online-frontend`](https://github.com/arthurSchgg/loja-online-frontend), com `main`, `develop`, páginas públicas, painel administrativo e CI. O código de trabalho está em `develop`; **não** publicar a branch sem homologação.
- Depois de aprovar HML-FE-001 e a release, escolher Amplify Hosting > Deploy from Git provider > `arthurSchgg/loja-online-frontend` > branch `main`. Não usar `develop` como origem pública.
- Não colocar número real WhatsApp, preços, fotos ou dados da loja sem aprovação comercial.
- O build de release seguro exige `API_BASE_URL=/api/v1 npm run build:release` (frontend PR #41). O comando força `SITE_MODE=live`, rejeita API ausente e valida URL externa HTTPS; nenhuma credencial no artefato público. Configurar caminho real na mesma origem HTTPS via proxy ou URL de API pública externa permitida explicitamente. O `npm run build` padrão permanece **apenas para demonstração**.
- Publicar somente a saída `dist/` do comando `npm run build:release`; validar que não contenha `admin.html`, `admin-experiences.html`, `reset-password.html`, `confirm-email.html` ou `admin/`. O painel usa build separado `npm run build:admin` (`dist-admin/`) e deve ser servido na **mesma origem HTTPS** da API; definir acesso, autenticação, CSRF e roteamento antes de disponibilizá-lo.
- A vitrine consulta API GET por CORS estrito; ações administrativas exigem **mesmo domínio**. Configurar roteamento antes de ativá-las.
- Habilitar cache longo apenas para assets versionados; `index.html` com cache curto e API sem cache.

## Checklist de publicação (não atendido)
- [ ] Criar/autorizar conta AWS e orçamento.
- [x] Criar repositório frontend independente (privado: `arthurSchgg/loja-online-frontend`).
- [ ] Homologar o frontend e publicar no Amplify **somente após** GO/NO-GO favorável.
- [ ] RDS privado, Secrets Manager, ECS Express Mode e ECR revisados.
- [ ] Testar CloudFront/caminhos/HTTPS/cookies/CSRF da origem unificada.
- [ ] Especificação aprovada e tag `spec-vX.Y.Z`.
- [ ] Diagnóstico e sete lotes tecnicamente aprovados; AUTH-001 autorizada.
- [ ] Implementação + homologações HML-001…007 completas.
- [ ] Dados e identidade comercial aprovados.
- [ ] Backups, restauração e rollback verificados.
- [ ] Aprovação técnica e comercial de release registrada.

**Nenhum valor comercial, domínio, serviço pago ou assinatura de aprovação foi presumido.**

## Estado verificado em 2026-11-10

- Backend: [`arthurSchgg/loja-online-backend`](https://github.com/arthurSchgg/loja-online-backend), branch `develop` com CI [#42818529538](https://github.com/arthurSchgg/loja-online-backend/actions/runs/42818529538) aprovado em PostgreSQL/JaCoCo, backup/restauração e smoke HTTPS **descartável**.
- Frontend: [`arthurSchgg/loja-online-frontend`](https://github.com/arthurSchgg/loja-online-frontend), branch `develop` com CI [#42818921394](https://github.com/arthurSchgg/loja-online-frontend/actions/runs/42818921394) aprovado. As funcionalidades de quizzes foram integradas em backend PR #156 e frontend PR #28; README atualizado no PR #29.
- **Não existem evidências de deploy AWS, E2E com domínio e serviços externos reais, aprovação comercial ou homologações HML-001–007/HML-FE-001.** Não confundir smoke local de CI com operação em ambiente real.
- Git Flow: backend `main` e `develop` possuem histórico divergente (167 commits à frente e 6 atrás em 2026-11-10); preparar a release reconciliando o histórico. O estado das proteções `main`/`develop` não pôde ser consultado pela conexão (403), exigindo verificação com permissão administrativa.
- Revisão e autorização final são acompanhadas em [RELEASE-001 #135](https://github.com/arthurSchgg/loja-online-backend/issues/135); **nenhum recurso pago AWS deve ser criado apenas por este documento**.

## Atualização técnica adicional — 2026-11-10

- Backend [PR #160](https://github.com/arthurSchgg/loja-online-backend/pull/160): ordenação por preço vigente/promocional e limite de preço paginado, CI de integração [#42829696970](https://github.com/arthurSchgg/loja-online-backend/actions/runs/42829696970) aprovado.
- Frontend PRs [#31](https://github.com/arthurSchgg/loja-online-frontend/pull/31), [#33](https://github.com/arthurSchgg/loja-online-frontend/pull/33), [#35](https://github.com/arthurSchgg/loja-online-frontend/pull/35), [#37](https://github.com/arthurSchgg/loja-online-frontend/pull/37), [#39](https://github.com/arthurSchgg/loja-online-frontend/pull/39), [#41](https://github.com/arthurSchgg/loja-online-frontend/pull/41): catálogo real, contatos institucionais, smoke de contrato, home sem produtos fictícios em live e build de release explícito.
- Ainda não há dados comerciais aprovados, deploy, domínio unificado, homologação full stack, orçamento ou aprovação GO/NO-GO. Não executar provisionamento por conta destas entregas técnicas.
