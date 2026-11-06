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

## Iniciando localmente

Pré-requisitos: JDK 21, Maven 3.9+ e PostgreSQL (ou Docker Compose).

1. Copie `.env.example` para `.env` e **substitua os valores de exemplo por credenciais fortes**.
2. Exporte ao terminal `DB_PASSWORD`, `ADMIN_USERNAME` e `ADMIN_PASSWORD` (mínimo 12 caracteres), além de `DB_URL` e `DB_USERNAME` se diferirem do padrão. O Spring **não carrega `.env` automaticamente**.
3. Execute `docker compose --env-file .env up -d`.
4. Para testar sem HTTPS **somente em localhost**, execute `mvn spring-boot:run -Dspring-boot.run.profiles=local`. O perfil `local` permite o cookie de sessão sem `Secure`; **nunca use esse perfil em produção**. Fora do localhost, use HTTPS e o perfil padrão.
5. Confira `http://localhost:8080/swagger-ui.html` e `http://localhost:8080/actuator/health`.

No PowerShell, defina variáveis com `$env:ADMIN_USERNAME="admin"`, `$env:ADMIN_PASSWORD="senha-longa-e-exclusiva"` etc. Credenciais jamais devem ser comitadas.

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
- [Git Flow](docs/GIT-FLOW.md)
- [Aprovações necessárias antes da publicação](docs/REVIEW-DECISIONS.md)
- [Backup e restauração](docs/BACKUP-RECOVERY.md)
- [Gestão administrativa de marcas e categorias](docs/ADMIN-LOOKUPS.md)
- [Matriz de testes](docs/TEST-COVERAGE.md)

## Situação do projeto

As funcionalidades principais estão implementadas em `develop`, organizadas em branches `feature/*` e integradas por PRs. **A implementação não significa que a plataforma esteja aprovada para produção.** Ainda são necessários homologação comercial, endurecimento de segurança, revisão de privacidade, configuração de backups reais e aprovação de imagens/contatos/preços.
