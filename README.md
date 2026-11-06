# Lojinha da Kátia — API REST

Backend do catálogo digital da **Lojinha da Kátia**. O site apresenta produtos e direciona clientes ao WhatsApp; **não possui carrinho, pedidos, checkout nem pagamentos**.

## Stack

Java 21 · Spring Boot 3.5.7 · Spring Web MVC · Spring Data JPA (Hibernate) · PostgreSQL 16 · Flyway · Jakarta Validation · Spring Security · Springdoc OpenAPI · Maven.

## Estrutura por domínio

```text
src/main/java/br/com/lojinhadakatia/
├── catalog/
│   ├── controller/    # Consultas públicas e operações admin
│   ├── dto/           # Contratos HTTP
│   ├── entity/        # Entidades persistentes
│   ├── repository/    # Acesso ao banco via Spring Data JPA
│   └── service/       # Casos de uso e regras de negócio
├── store/             # Institucional, contato e WhatsApp
├── security/          # Autorização ADMIN e CORS
├── shared/            # OpenAPI, erros padronizados
└── LojaOnlineApplication.java
```

## Executar localmente

Pré-requisitos: Java 21, Maven 3.9+, Docker e Docker Compose.

1. Copie `.env.example` para `.env` e altere **DB_PASSWORD** e **ADMIN_PASSWORD** para valores fortes. O arquivo `.env` não deve ser comitado.
2. Exporte as variáveis de `.env` para o ambiente do terminal (o Spring Boot **não lê automaticamente** o arquivo `.env`). No PowerShell, use `$env:DB_PASSWORD="..."`, `$env:ADMIN_USERNAME="..."`, `$env:ADMIN_PASSWORD="..."` e, se necessário, as outras variáveis.
3. Execute `docker compose --env-file .env up -d`.
4. Rode `mvn spring-boot:run`.

**Windows PowerShell:** além das variáveis de admin e banco, informe `$env:DB_URL="jdbc:postgresql://localhost:5432/lojinha_da_katia"` se sua configuração diferir do padrão.

- API: `http://localhost:8080/api/v1/products`
- OpenAPI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

## Rotas do MVP

| Método | Endpoint | Acesso |
|---|---|---|
| GET | `/api/v1/products` | Público |
| GET | `/api/v1/products/{id}` | Público |
| GET | `/api/v1/brands` | Público |
| GET | `/api/v1/categories` | Público |
| GET | `/api/v1/store` | Público |
| GET | `/api/v1/admin/products` | ADMIN |
| POST, PUT, DELETE | `/api/v1/admin/products[/{id}]` | ADMIN |
| POST, PUT, DELETE | `/api/v1/admin/brands[/{id}]` | ADMIN |
| POST, PUT, DELETE | `/api/v1/admin/categories[/{id}]` | ADMIN |
| PUT | `/api/v1/admin/store` | ADMIN |

A busca aceita `q`, `brandId`, `categoryId`, `available`, `page` e `size` (até 50). A API oferece fotos via URL HTTPS aprovada, sem upload binário.

### Exemplo: cadastro administrativo

```bash
curl -u "$ADMIN_USERNAME:$ADMIN_PASSWORD" -X POST http://localhost:8080/api/v1/admin/brands \
  -H 'Content-Type: application/json' -d '{"name":"Marca de Exemplo"}'
```

Antes de cadastrar um produto, crie uma marca e uma categoria e utilize os IDs retornados. O campo `published=false` mantém rascunhos ocultos; `available` apenas informa disponibilidade visual, **não controla estoque**.

## Testes e entrega

`mvn clean verify` executa testes de integração rápidos em H2 (perfil `test`). A integração real com PostgreSQL/Testcontainers continua como melhoria planejada. O GitHub Actions valida os PRs.

## Segurança e decisões

Atualmente o administrador utiliza **HTTP Basic + BCrypt**, credenciais fornecidas por variáveis de ambiente, sem credenciais padrão. **Use apenas sob HTTPS** fora do computador local. A estratégia definitiva de autenticação aguarda aprovação; veja [docs/SECURITY.md](docs/SECURITY.md). Não publique um painel administrativo sem rever essa decisão.

Há campos de negócio intencionalmente vazios: telefone, Instagram, preços e imagens reais dependem de informações da proprietária. As experiências interativas e a área completa de velas estão planejadas para funcionalidades posteriores.

## Fluxo de trabalho

`main` para versões estáveis; `develop` para integração; `feature/<escopo>` para mudanças; PRs para revisão e CI. Mensagens de commit seguem Conventional Commits.
