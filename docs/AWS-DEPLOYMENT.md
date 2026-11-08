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

**Importante:** o backend atual recebe usuário/senha por ambiente, mas ainda não implementa integralmente recuperação por e-mail/outbox. Não colocar painel administrativo em produção até essas lacunas de segurança serem revisadas.

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
- Arquivos estáticos `index.html`, `styles.css`, `app.js`, assets e `amplify.yml`.
- Escolher Amplify Hosting > Deploy from Git provider > repo frontend > branch `main`.
- Não colocar número real WhatsApp, preços, fotos ou dados da loja sem aprovação comercial.
- Definir `API_BASE_URL` público em arquivo de configuração estático (sem segredos).
- A vitrine consulta API GET por CORS estrito; ações administrativas exigem **mesmo domínio**. Configurar roteamento antes de ativá-las.
- Habilitar cache longo apenas para assets versionados; `index.html` com cache curto e API sem cache.

## Checklist de publicação (não atendido)
- [ ] Criar/autorizar conta AWS e orçamento.
- [ ] Criar repo frontend independente e hospedar no Amplify.
- [ ] RDS privado, Secrets Manager, ECS Express Mode e ECR revisados.
- [ ] Testar CloudFront/caminhos/HTTPS/cookies/CSRF da origem unificada.
- [ ] Especificação aprovada e tag `spec-vX.Y.Z`.
- [ ] Diagnóstico e sete lotes tecnicamente aprovados; AUTH-001 autorizada.
- [ ] Implementação + homologações HML-001…007 completas.
- [ ] Dados e identidade comercial aprovados.
- [ ] Backups, restauração e rollback verificados.
- [ ] Aprovação técnica e comercial de release registrada.

**Nenhum valor comercial, domínio, serviço pago ou assinatura de aprovação foi presumido.**
