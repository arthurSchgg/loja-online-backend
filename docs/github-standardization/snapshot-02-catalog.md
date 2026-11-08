# Snapshot pré-padronização — lote 02-catalog

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08. Estados e descrições observados abaixo para comparação futura. Não atesta aprovação do lote e não representa alteração nas issues.

## Issue #7

- URL: https://github.com/arthurSchgg/loja-online/issues/7
- Título antes: [BACKEND] Modelar entidades e relacionamentos de catálogo
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:16:12Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** modelar entidades e relacionamentos de catálogo,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
definir Product, Brand, Category, ProductImage, StoreInformation; relacionamentos, constraints, índices, timestamps e estratégia para inativação/visibilidade.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] ERD e dicionário de dados
- [ ] Constraints reais no PostgreSQL
- [ ] IDs e nomes padronizados
- [ ] Migrações revisadas
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `migration` (issue correspondente do backlog)
- `project` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** database
- **Story Points:** 5
- **Responsável:** a definir
- **Labels propostas:** `backend`, `database`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #9

- URL: https://github.com/arthurSchgg/loja-online/issues/9
- Título antes: [BACKEND] Implementar listagem pública de produtos publicados
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:46:59Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar listagem pública de produtos publicados,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar GET /api/v1/products, com paginação, ordenação determinística e exclusão de produtos não publicados.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Retorna apenas produtos publicados
- [ ] paginação e limites válidos
- [ ] coleção vazia é 200
- [ ] testes de service/repository/controller.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07
- Issue #08

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `catalog`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #10

- URL: https://github.com/arthurSchgg/loja-online/issues/10
- Título antes: [BACKEND] Implementar busca textual e filtros combináveis de produtos
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:53:28Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar busca textual e filtros combináveis de produtos,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Busca por nome e descrição; filtros por marca, categoria e disponibilidade; considerar faixa de preço mediante validação; usar Specifications JPA.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Filtros podem ser combinados
- [ ] busca ignora caixa
- [ ] ordenação estável
- [ ] entradas inválidas 400
- [ ] testes cobrindo interseções.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #09

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `catalog`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #11

- URL: https://github.com/arthurSchgg/loja-online/issues/11
- Título antes: [BACKEND] Implementar consulta pública de detalhes de produto
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:02Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar consulta pública de detalhes de produto,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar GET /api/v1/products/{id}; incluir dados publicados de preço, marca, categorias, imagens e disponibilidade sem atributos privados.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Produto publicado retorna 200
- [ ] oculto/inexistente retorna 404
- [ ] DTO evita dados internos
- [ ] OpenAPI e testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07
- Issue #08

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `catalog`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #12

- URL: https://github.com/arthurSchgg/loja-online/issues/12
- Título antes: [BACKEND] Implementar listagem pública de marcas
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:05Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar listagem pública de marcas,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar GET /api/v1/brands para marcas ativas; incluir identificação estável e nome de exibição.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Não expõe marcas desativadas
- [ ] resposta ordenada
- [ ] sem duplicações
- [ ] testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `catalog`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #13

- URL: https://github.com/arthurSchgg/loja-online/issues/13
- Título antes: [BACKEND] Implementar listagem pública de categorias
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:07Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar listagem pública de categorias,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar GET /api/v1/categories com categorias ativas, ordenação e campos suficientes para filtros.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Coleção vazia 200
- [ ] inativas não retornam
- [ ] documentação e testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `catalog`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #14

- URL: https://github.com/arthurSchgg/loja-online/issues/14
- Título antes: [BACKEND] Definir armazenamento e validação das imagens de produtos
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:24Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** definir armazenamento e validação das imagens de produtos,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Decidir com responsável se o MVP usa URLs ou provedor de imagens; modelar múltiplas imagens, ordenação e capa; impedir referências malformadas.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Estratégia e custos registrados
- [ ] URL segura ou upload validado
- [ ] fallback quando não houver imagem
- [ ] nunca salvar segredos.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `media`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #19

- URL: https://github.com/arthurSchgg/loja-online/issues/19
- Título antes: [BACKEND] Implementar CRUD administrativo de marcas
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:16Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar crud administrativo de marcas,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar rotas /api/v1/admin/brands para cadastrar, listar, editar e inativar marcas; manter integridade com catálogo.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Acesso apenas admin
- [ ] nome único conforme regra
- [ ] 201/200/400/404/409
- [ ] testes e docs.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #12

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `admin`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #20

- URL: https://github.com/arthurSchgg/loja-online/issues/20
- Título antes: [BACKEND] Implementar CRUD administrativo de categorias
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:19Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar crud administrativo de categorias,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar rotas /api/v1/admin/categories com validação, edição, ordenação e inativação segura; não excluir vínculo de produtos.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Somente admin
- [ ] FK mantida
- [ ] nome/slug único se aplicável
- [ ] erros corretos e testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #13

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `admin`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #21

- URL: https://github.com/arthurSchgg/loja-online/issues/21
- Título antes: [BACKEND] Implementar CRUD administrativo de produtos e publicação
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:25Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar crud administrativo de produtos e publicação,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar POST/GET/PUT/PATCH/DELETE ou inativação em /api/v1/admin/products; cadastro completo, atualização, rascunho, publicar e despublicar.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Só admin
- [ ] relacionamentos válidos
- [ ] preço não-negativo
- [ ] estado de publicação consistente
- [ ] GET público reflete status
- [ ] testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #19
- Issue #20
- Issue #07

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `admin`, `MVP`, `feature`
- **Esforço (Story Points):** 8
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #22

- URL: https://github.com/arthurSchgg/loja-online/issues/22
- Título antes: [BACKEND] Gerenciar imagens de produtos na área administrativa
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:45Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** gerenciar imagens de produtos na área administrativa,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar operações de vincular/remover/reordenar imagens, selecionar capa e validar domínio/formatos conforme estratégia aprovada.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Somente admin
- [ ] ordem estável
- [ ] imagens não quebram publicações
- [ ] testes de consistência.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #21
- Issue #14

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `admin`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #29

- URL: https://github.com/arthurSchgg/loja-online/issues/29
- Título antes: [BACKEND] Gerenciar imagens de produtos na área administrativa
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:36Z
- Corpo anterior:

~~~markdown
Duplicada de #22. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

