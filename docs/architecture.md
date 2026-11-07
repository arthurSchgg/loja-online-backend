# Arquitetura de referência — estado do código vs. requisitos

**Estado:** rascunho documental, sem decisão de reestruturação do código.

## Estrutura observada em `develop`
Backend modular Java 21, Spring Boot 3.5.7, MVC, Spring Data JPA, PostgreSQL, Flyway V1–V6, Maven, Swagger/OpenAPI, Spring Security e Docker/Caddy staging. Pacotes: `catalog`, `store`, `experience`, `offering`, `content`, `campaign`, `security`, `shared`. Fonte: árvore do repositório, `pom.xml`, README e documentação preexistente.

A camada HTTP usa controllers/DTOs, persistência JPA/repositories, regras em services e migrations Flyway. O frontend não está neste repositório, portanto seu comportamento **não foi verificado**.

## Requisitos de arquitetura aprovados, nem todos implementados
- Backend fonte da verdade para preço, promoção, estoque e disponibilidade.
- Confirmação e revalidação de toda solicitação ao WhatsApp.
- Sessão HTTP com CSRF para a administradora única; mesmo domínio para frontend administrativo e API.
- Migrações Flyway versionadas; ambientes DEV, STAGING e PROD isolados.
- Testes e documentação vinculados a US, fase e homologação.

## Diferenças identificadas a verificar
- O README antigo declara não existir estoque transacional, embora o escopo recente aprove estoque e movimentações.
- `docs/ARCHITECTURE.md` ainda menciona HTTP Basic/JWT como decisão pendente, enquanto `docs/SECURITY.md` confirma sessão HTTP e CSRF. O último é a arquitetura vigente para autenticação.
- `docs/WHATSAPP.md` descreve GETs de link sem confirmação no contrato atual; novo fluxo exige confirmação e revalidação.
- `docs/CAMPAIGNS.md` / módulo campaign exigem reconciliação entre agenda de banners e promoções de preço.
- Não presumir que requisitos de qualidade e operação estão satisfeitos apenas pela existência de CI/arquivos.
