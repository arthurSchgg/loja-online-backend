# Lojinha da Kátia — Backend

Backend REST do site **Lojinha da Kátia**: vitrine digital, experiências interativas, espaço das velas e administração de conteúdo. O objetivo é apresentar ofertas e estimular conversas pelo WhatsApp. **Não existe carrinho, checkout, pedido, cobrança, agendamento automático ou gestão de estoque transacional.**

## Tecnologias

Java 21 • Spring Boot 3.5.7 • Spring MVC • Spring Data JPA/Hibernate • PostgreSQL • Flyway • Jakarta Validation • Spring Security • OpenAPI/Swagger • Maven • JUnit/MockMvc.

## Estrutura

```text
src/main/java/br/com/lojinhadakatia/
├── LojaOnlineApplication.java
├── catalog/       # Produtos, marcas, categorias, fotos, busca e filtros
├── store/         # Contatos e informações oficiais da loja
├── experience/    # Quizzes, perguntas, alternativas e resultados
├── offering/      # Velas artesanais, oficinas e locações
├── content/       # Blocos e seções institucionais publicáveis
├── campaign/      # Banners e destaques com período de publicação
├── security/      # ADMIN, CORS e auditoria mínima
└── shared/        # OpenAPI e respostas de erro ProblemDetail

src/main/resources/
├── application.yml
└── db/migration/  # Migrações Flyway V1 a V6

src/test/          # Testes MockMvc/JPA/H2 e PostgreSQL real em CI
docs/              # Contratos, operações, segurança, testes e Git Flow
```

Cada domínio usa `controller` (HTTP), `dto` (contratos), `entity` (persistência), `repository` (Spring Data JPA) e `service` (regras de negócio). As entidades **não** são serializadas diretamente nas respostas.

## Execução local e staging (sem nuvem)

Existem duas configurações Docker independentes: **local HTTP em 127.0.0.1** e **staging HTTPS em localhost**. Elas utilizam bancos e volumes PostgreSQL diferentes. O Dockerfile compila o Spring Boot internamente com Maven/Java 21.

**Local:**

```bash
cp .env.example .env
# Edite .env e troque as senhas de exemplo.
docker compose --env-file .env -f compose.yaml up --build -d
```

- API: `http://localhost:8080/actuator/health`
- Swagger (apenas local): `http://localhost:8080/swagger-ui.html`

**Staging:**

```bash
cp .env.staging.example .env.staging
# Defina senhas exclusivas para staging.
docker compose --env-file .env.staging -f compose.staging.yaml up --build -d
```

- API: `https://localhost:8443/actuator/health`
- HTTPS usa autoridade certificadora local do Caddy; confie explicitamente no certificado antes de acessar no navegador. Consulte o guia para exportar o certificado e verificar com `curl --cacert`.
- Porta publicada somente em `127.0.0.1`; backend/PostgreSQL não ficam expostos e o Swagger está desativado.

Consulte **[docs/LOCAL-STAGING.md](docs/LOCAL-STAGING.md)** para configurações, certificados, verificação dos dois Compose, logs, parada sem perda de dados e limitações de segurança. Não há deploy externo nem frontend neste repositório.

## Endpoints públicos

| Método | Rota | Função |
|---|---|---|
| GET | `/api/v1/products` | Produtos publicados, busca e filtros |
| GET | `/api/v1/products/{id}` | Detalhes do produto publicado |
| GET | `/api/v1/products/{id}/whatsapp` | Link contextualizado para WhatsApp |
| GET | `/api/v1/brands` | Marcas ativas |
| GET | `/api/v1/categories` | Categorias ativas |
| GET | `/api/v1/store` | Contato e dados institucionais |
| GET | `/api/v1/experiences` | Quizzes publicados |
| GET | `/api/v1/experiences/{id}` | Perguntas e alternativas |
| POST | `/api/v1/experiences/{id}/evaluate` | Avaliação anônima |
| GET | `/api/v1/offerings` | Velas, oficinas e locações |
| GET | `/api/v1/offerings/{id}` | Detalhes de oferta |
| GET | `/api/v1/offerings/{id}/whatsapp` | Link contextualizado para WhatsApp |
| GET | `/api/v1/content-sections` | Conteúdo institucional publicado |
| GET | `/api/v1/campaigns` | Banners ativos |

Filtros de produtos: `q`, `brandId`, `categoryId`, `available`, `page`, `size` (máximo 50). Ofertas aceitam `type=CANDLE|WORKSHOP|RENTAL`.

## Endpoints de administração

Todas as rotas usam o prefixo `/api/v1/admin` e exigem **ADMIN** por sessão HTTP segura e proteção CSRF. A proprietária faz login pelo endpoint `/api/v1/auth/login`; o painel deve compartilhar a mesma origem da API:

- `/products`: GET/POST e GET/PUT/DELETE por ID;
- `/brands` e `/categories`: POST, PUT e desativação por DELETE;
- `/store`: PUT de contatos e dados públicos;
- `/experiences`: GET/POST e GET/PUT/DELETE por ID;
- `/offerings`: GET/POST e GET/PUT/DELETE por ID;
- `/content-sections`: GET/POST e PUT/DELETE por ID;
- `/campaigns`: GET/POST e PUT/DELETE por ID.

Para integrar o formulário HTML/JavaScript, consulte [docs/SECURITY.md](docs/SECURITY.md), que contém o fluxo de obtenção do CSRF, login por formulário, chamadas autenticadas e logout. O frontend administrativo e a API devem estar sob a **mesma origem**, ainda que em repositórios separados.

**Nunca publique o painel sem HTTPS.** A autenticação por sessão foi aprovada, mas limites de tentativas, configuração de hospedagem, operação e homologação ainda precisam de validação.

## Testes

```bash
mvn clean verify
```

Os testes locais rápidos usam H2. O workflow GitHub Actions instala PostgreSQL real e executa um teste de integração com Flyway/Hibernate no banco-alvo, sem depender de Docker. Veja [docs/TESTING.md](docs/TESTING.md).

## Especificação em consolidação (não aprovada)

Os documentos abaixo representam **requisitos planejados**, não funcionalidades já implementadas nem homologadas. A consolidação está em revisão e ainda depende da auditoria de identificadores e da aprovação técnica formal.

- [Inventário das regras de negócio](docs/business-rules.md)
- [Requisitos funcionais e não funcionais](docs/requirements.md)
- [Arquitetura de referência e divergências](docs/architecture.md)
- [Sete fases e critérios de aceitação](docs/development-phases.md)
- [GitHub Projects, issues e Git Flow](docs/github-workflow.md)
- [Políticas de segurança consolidadas](docs/security.md)
- [Operação, backups e incidentes](docs/operations.md)
- [Matriz inicial de rastreabilidade](docs/traceability.md)
- [Auditoria de consistência e bloqueios](docs/consistency-audit.md)
- [Inventário preliminar do código (sem diagnóstico formal)](docs/preliminary-code-inventory.md)
- [Lote 1 — Fundação e segurança](docs/github-standardization/batch-01-foundation.md)
- [Lote 2 — Catálogo e estoque](docs/github-standardization/batch-02-catalog.md)
- [Lote 3 — Promoções e WhatsApp](docs/github-standardization/batch-03-promotions.md)
- [Lote 4 — Experiências interativas](docs/github-standardization/batch-04-experiences.md)
- [Lote 5 — Velas, oficinas e locações](docs/github-standardization/batch-05-offerings.md)
- [Lote 6 — Conteúdo e frontend público](docs/github-standardization/batch-06-frontend.md)
- [Lote 7 — Administração e qualidade](docs/github-standardization/batch-07-administration.md)
- [Issue #79 — Consolidação e aprovação técnica](https://github.com/arthurSchgg/loja-online/issues/79)
- [Visão do projeto e diferenças do briefing](docs/vision.md)
- [Casos de uso e diagrama](docs/use-cases.md)
- [Modelo de dados e dicionário inicial](docs/data-model.md)
- [Contrato REST existente e evolução proposta](docs/api-contract-proposal.md)
- [Design System e Motion Design provisórios](docs/ui-design-system.md)
- [Plano de testes e homologação](docs/test-plan.md)
- [Diagnóstico preliminar da Fase 1 (NÃO APROVADO)](docs/phase-1-diagnostic.md)
- [Autorização AUTH-001 — NÃO AUTORIZADA](https://github.com/arthurSchgg/loja-online/issues/80)
- [PR #111 — JaCoCo 80%, testes complementares, sem merge](https://github.com/arthurSchgg/loja-online/pull/111)
- [Plano de preenchimento dos campos GitHub Projects](docs/project-board-plan.md)
- [Relatório final de preparação para revisão](docs/review-handoff.md)
- [Equivalência e preservação da documentação inicial](docs/legacy-baseline-crosswalk.md)

**Ordem obrigatória:** consolidar e aprovar especificação → diagnosticar código → aprovar sete lotes de issues → autorização AUTH-001 → iniciar Fase 1. Nenhuma destas aprovações está implícita neste README.

## Documentos

- [Arquitetura](docs/ARCHITECTURE.md)
- [Contrato da API](docs/API.md)
- [Segurança](docs/SECURITY.md)
- [Experiências](docs/EXPERIENCES.md)
- [Serviços e velas](docs/OFFERINGS.md)
- [Conteúdo institucional](docs/CONTENT.md)
- [Campanhas](docs/CAMPAIGNS.md)
- [WhatsApp](docs/WHATSAPP.md)
- [Testes](docs/TESTING.md)
- [Implantação](docs/DEPLOYMENT.md)
- [Docker local e staging HTTPS](docs/LOCAL-STAGING.md)
- [Git Flow](docs/GIT-FLOW.md)
- [Aprovações necessárias antes da publicação](docs/REVIEW-DECISIONS.md)
- [Backup e restauração](docs/BACKUP-RECOVERY.md)
- [Gestão administrativa de marcas e categorias](docs/ADMIN-LOOKUPS.md)
- [Matriz de testes](docs/TEST-COVERAGE.md)

## Situação do projeto

As funcionalidades principais estão implementadas em `develop`, organizadas em branches `feature/*` e integradas por PRs. **A implementação não significa que a plataforma esteja aprovada para produção.** Ainda são necessários homologação comercial, endurecimento de segurança, revisão de privacidade, configuração de backups reais e aprovação de imagens/contatos/preços.


## AWS (proposta de implantação)

A implantação planejada em ECS Express Mode, RDS PostgreSQL privado e Amplify Hosting para o frontend separado está documentada em [docs/AWS-DEPLOYMENT.md](docs/AWS-DEPLOYMENT.md). O workflow manual [aws-ecr-publish.yml](.github/workflows/aws-ecr-publish.yml) publica apenas uma imagem ECR aprovada, sem provisionar infraestrutura nem executar deploy. Nenhum recurso AWS foi criado; consultar orçamento e gates de homologação antes de ativar.
