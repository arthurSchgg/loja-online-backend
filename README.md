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
4. Execute `mvn spring-boot:run`.
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

Todas as rotas usam o prefixo `/api/v1/admin` e exigem **ADMIN** (HTTP Basic, credenciais por ambiente):

- `/products`: GET/POST e GET/PUT/DELETE por ID;
- `/brands` e `/categories`: POST, PUT e desativação por DELETE;
- `/store`: PUT de contatos e dados públicos;
- `/experiences`: GET/POST e GET/PUT/DELETE por ID;
- `/offerings`: GET/POST e GET/PUT/DELETE por ID;
- `/content-sections`: GET/POST e PUT/DELETE por ID;
- `/campaigns`: GET/POST e PUT/DELETE por ID.

Exemplo para criar uma marca de demonstração, após configurar credenciais:

```bash
curl -u "$ADMIN_USERNAME:$ADMIN_PASSWORD" -X POST http://localhost:8080/api/v1/admin/brands \
  -H "Content-Type: application/json" -d '{"name":"Marca demonstrativa"}'
```

**Nunca utilize HTTP sem HTTPS em produção.** Uma autenticação definitiva (sessão ou JWT), proteção contra abuso e política de administradores ainda exigem aprovação e implementação antes de uma liberação pública.

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

As funcionalidades principais estão implementadas em `develop`, organizadas em branches `feature/*` e integradas por PRs. **A implementação não significa que a plataforma esteja aprovada para produção.** Ainda são necessários homologação comercial, credenciais definitivas, endurecimento de segurança, revisão de privacidade, testes de casos-limite, backup e aprovação de imagens/contatos/preços reais.
