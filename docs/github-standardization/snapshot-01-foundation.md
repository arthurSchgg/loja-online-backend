# Snapshot pré-padronização — lote 01-foundation

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08. Estados e descrições observados abaixo para comparação futura. Não atesta aprovação do lote e não representa alteração nas issues.

## Issue #1

- URL: https://github.com/arthurSchgg/loja-online/issues/1
- Título antes: [BACKEND] Definir escopo do backend, regras de negócio e matriz de rastreabilidade
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:15:55Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** definir escopo do backend, regras de negócio e matriz de rastreabilidade,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
documentar escopo da API, requisitos funcionais/não funcionais, regras de negócio, personas e fronteiras com frontend; indicar MVP vs pós-MVP e decisões pendentes.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Docs/visao-backend.md, docs/requisitos.md, docs/regras-negocio.md e tabela RF → issue → endpoint/teste
- [ ] Nenhuma regra comercial presumida
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- Nenhuma dependência técnica prévia.

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** foundation
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `foundation`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #2

- URL: https://github.com/arthurSchgg/loja-online/issues/2
- Título antes: [BACKEND] Inicializar aplicação Java 21 / Spring Boot / Maven
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:46:52Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** inicializar aplicação java 21 / spring boot / maven,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
gerar projeto Spring Boot com Spring Web MVC, Spring Data JPA, Validation, PostgreSQL, springdoc-openapi e testes; estruturar pacote br.com.lojinhadakatia (ou nome definitivo decidido em configuração).

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Mvnw e pom.xml versionados
- [ ] Aplicação inicializa
- [ ] GET health endpoint ou Actuator planejado
- [ ] Sem endpoints administrativos públicos
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `project` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** foundation
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `foundation`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #3

- URL: https://github.com/arthurSchgg/loja-online/issues/3
- Título antes: [BACKEND] Configurar PostgreSQL, perfis e variáveis de ambiente
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:46:55Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** configurar postgresql, perfis e variáveis de ambiente,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
definir application.yml e perfis local/test/prod; usar variáveis de ambiente para credenciais; configurar datasource, timezone e logs sem segredos.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Conexão local funcional
- [ ] Arquivos example sem senhas
- [ ] Segredos excluídos do Git
- [ ] Teste executado em ambiente isolado
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `bootstrap` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** database
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `database`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #4

- URL: https://github.com/arthurSchgg/loja-online/issues/4
- Título antes: [BACKEND] Introduzir migrações versionadas do banco
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:46:57Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** introduzir migrações versionadas do banco,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
integrar Flyway (ou ferramenta definida) e substituir ddl-auto=update em produção por validate; organizar migrações incrementais.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Migrações reproduzíveis em banco novo
- [ ] Rollback documentado quando necessário
- [ ] Dados preservados em atualizações
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `config` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** database
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `database`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #5

- URL: https://github.com/arthurSchgg/loja-online/issues/5
- Título antes: [BACKEND] Estruturar módulos MVC, DTOs, mappers e tratamento global de erros
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:16:03Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** estruturar módulos mvc, dtos, mappers e tratamento global de erros,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
organizar pacotes por domínio (product, category, brand, store, auth, etc.) com controller/service/repository/entity/dto/mapper e exceções compartilhadas; definir ProblemDetail.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Exceções 400/404/409/500 coerentes
- [ ] Respostas não expõem stack trace
- [ ] Testes de controller e serviço de exemplo
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `bootstrap` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** foundation
- **Story Points:** 5
- **Responsável:** a definir
- **Labels propostas:** `backend`, `foundation`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #6

- URL: https://github.com/arthurSchgg/loja-online/issues/6
- Título antes: [BACKEND] Configurar OpenAPI/Swagger e contrato de API
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:16:05Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** configurar openapi/swagger e contrato de api,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
habilitar documentação Springdoc, schemas DTO, exemplos, códigos de erro, paginação e agrupamento rotas públicas/admin.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Swagger-ui local acessível
- [ ] Operações com parâmetros/respostas documentados
- [ ] Endpoints protegidos identificados
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `architecture` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** api
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `api`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #8

- URL: https://github.com/arthurSchgg/loja-online/issues/8
- Título antes: [BACKEND] Criar validações de DTO e sanitização de entradas
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:16:14Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pela Lojinha da Kátia ou integrante da equipe de desenvolvimento,
**Eu quero** criar validações de dto e sanitização de entradas,
**Para** oferecer uma vitrine digital confiável, gerenciável e integrada ao frontend.

## Contexto e escopo
aplicar Bean Validation nos requests (tamanhos, valores numéricos não-negativos, URLs/strings quando aplicável), regras cruzadas no serviço e limites de paginação.

## Requisitos técnicos
- **Stack:** Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Bean Validation e Maven.
- **Arquitetura:** módulos por domínio, camadas Controller/Service/Repository, DTO e mapper onde pertinente.
- **Contrato:** documentar rotas e estados com Springdoc OpenAPI quando aplicável.
- **Segurança:** toda gravação administrativa precisa estar protegida; não expor dados ou segredos.

## Critérios de aceitação
- [ ] Inputs inválidos geram 400 com mensagens seguras por campo
- [ ] Payloads inesperados não geram 500
- [ ] Testes negativos
- [ ] Documentação atualizada e testes pertinentes adicionados.
- [ ] Pull request com revisão e validação do build.

## Dependências e ordem sugerida
- `architecture` (issue correspondente do backlog)

## Informações adicionais
- **Prioridade:** Alta
- **Fase:** MVP
- **Área:** api
- **Story Points:** 3
- **Responsável:** a definir
- **Labels propostas:** `backend`, `api`, `MVP`, `feature`.

> Decisões comerciais e abordagens sugeridas dependem de validação; não assumir informações da loja sem confirmação.
~~~

---

## Issue #17

- URL: https://github.com/arthurSchgg/loja-online/issues/17
- Título antes: [BACKEND] Definir ADR de autenticação e autorização administrativa
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T13:16:08Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** definir adr de autenticação e autorização administrativa,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Documentar decisão Spring Security + sessão ou JWT com análise de CORS/CSRF, gerenciamento de credenciais e inicialização de conta admin.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Decisão aprovada antes do código
- [ ] requisitos de senha e acesso documentados
- [ ] nenhuma escrita exposta durante desenvolvimento.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #01

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `security`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #18

- URL: https://github.com/arthurSchgg/loja-online/issues/18
- Título antes: [BACKEND] Implementar autenticação administrativa segura com Spring Security
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T13:16:34Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar autenticação administrativa segura com spring security,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Implementar a opção aprovada na ADR; senha com hash adaptativo, login, expiração, autorização de admin; restringir /api/v1/admin/**.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Sem autenticação retorna 401
- [ ] sem papel 403
- [ ] credenciais inválidas sem enumeração
- [ ] CORS/CSRF corretos
- [ ] testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #17
- Issue #05

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `security`, `MVP`, `feature`
- **Esforço (Story Points):** 8
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #25

- URL: https://github.com/arthurSchgg/loja-online/issues/25
- Título antes: [BACKEND] Configurar CORS seguro e cabeçalhos para frontend separado
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:52Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** configurar cors seguro e cabeçalhos para frontend separado,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Configurar origens por ambiente; métodos, headers, credenciais e políticas para integração Fetch; habilitar cabeçalhos de segurança.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Origem não autorizada rejeitada
- [ ] preflight correto
- [ ] sem wildcard com credenciais
- [ ] testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `security`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #26

- URL: https://github.com/arthurSchgg/loja-online/issues/26
- Título antes: [BACKEND] Criar bateria de testes unitários das regras de negócio
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:54Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** criar bateria de testes unitários das regras de negócio,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Cobrir Service/Mapper e validações com JUnit 5/Mockito; produtos publicados, filtros, preços, vínculos e exceções.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Caminhos felizes e negativos documentados
- [ ] build passa
- [ ] testes determinísticos.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #21
- Issue #10

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `quality`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #27

- URL: https://github.com/arthurSchgg/loja-online/issues/27
- Título antes: [BACKEND] Criar testes de integração REST, banco PostgreSQL e segurança
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:56Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** criar testes de integração rest, banco postgresql e segurança,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Usar Testcontainers PostgreSQL ou alternativa aprovada, MockMvc, migrations e autenticação; cobrir falhas e fluxos críticos.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Banco real em teste
- [ ] 401/403/404/409
- [ ] publicação/CRUD/filtros integrados
- [ ] isolamento dos testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #04
- Issue #21

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `quality`, `MVP`, `feature`
- **Esforço (Story Points):** 8
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #28

- URL: https://github.com/arthurSchgg/loja-online/issues/28
- Título antes: [BACKEND] Configurar integração contínua GitHub Actions com Java 21
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:30Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** configurar integração contínua github actions com java 21,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar workflow Maven compile/test em PR e push; cache de dependências e publicação de resultados sem segredos.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Checks executam no PR
- [ ] falhas ficam visíveis
- [ ] Java 21 fixado
- [ ] reprodutibilidade documentada.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #02
- Issue #26

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `devops`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #32

- URL: https://github.com/arthurSchgg/loja-online/issues/32
- Título antes: [BACKEND] Configurar CORS seguro e cabeçalhos para frontend separado
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:01Z
- Corpo anterior:

~~~markdown
Duplicada de #25. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #33

- URL: https://github.com/arthurSchgg/loja-online/issues/33
- Título antes: [BACKEND] Criar bateria de testes unitários das regras de negócio
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:03Z
- Corpo anterior:

~~~markdown
Duplicada de #26. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #34

- URL: https://github.com/arthurSchgg/loja-online/issues/34
- Título antes: [BACKEND] Criar testes de integração REST, banco PostgreSQL e segurança
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:06Z
- Corpo anterior:

~~~markdown
Duplicada de #27. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #35

- URL: https://github.com/arthurSchgg/loja-online/issues/35
- Título antes: [BACKEND] Configurar integração contínua GitHub Actions com Java 21
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:08Z
- Corpo anterior:

~~~markdown
Duplicada de #28. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #36

- URL: https://github.com/arthurSchgg/loja-online/issues/36
- Título antes: [BACKEND] Configurar health check, logs e monitoramento essencial
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:48Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pelo projeto ou pela administração da Lojinha da Kátia,
**Eu quero** configurar health check, logs e monitoramento essencial,
**Para** garantir uma vitrine digital segura, completa e fácil de manter.

## Escopo técnico
Spring Boot Actuator health; logging seguro, correlação e diagnóstico.

## Implementação esperada
- Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, DTOs, validação e Mapper conforme contexto.
- Separar Controller, Service, Repository e regras de domínio; documentar endpoints no Springdoc.
- Proteger qualquer rota administrativa; não implementar checkout.

## Critérios de aceitação
- [ ] Health sem segredos
- [ ] erros rastreáveis
- [ ] testes.
- [ ] Atualizar documentação, implementar testes relevantes e validar PR/CI.

## Dependências
#05

## Informações adicionais
- **Prioridade:** Alta
- **Labels previstas:** `backend`, `devops`, `MVP`
- **Story Points:** 3
- **Responsável:** a definir
- **Fase:** MVP

> Decisões não confirmadas dependem de validação com a proprietária.
~~~

---

## Issue #37

- URL: https://github.com/arthurSchgg/loja-online/issues/37
- Título antes: [BACKEND] Preparar deploy do backend e PostgreSQL em produção
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:14Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pelo projeto ou pela administração da Lojinha da Kátia,
**Eu quero** preparar deploy do backend e postgresql em produção,
**Para** garantir uma vitrine digital segura, completa e fácil de manter.

## Escopo técnico
Planejar hospedagem, custo, variáveis, HTTPS, migração, banco persistente, CORS e política Swagger.

## Implementação esperada
- Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, DTOs, validação e Mapper conforme contexto.
- Separar Controller, Service, Repository e regras de domínio; documentar endpoints no Springdoc.
- Proteger qualquer rota administrativa; não implementar checkout.

## Critérios de aceitação
- [ ] Implantação somente com segurança aplicada
- [ ] checklist de release
- [ ] segredo não versionado.
- [ ] Atualizar documentação, implementar testes relevantes e validar PR/CI.

## Dependências
#18, #27, #28

## Informações adicionais
- **Prioridade:** Alta
- **Labels previstas:** `backend`, `devops`, `MVP`
- **Story Points:** 5
- **Responsável:** a definir
- **Fase:** MVP

> Decisões não confirmadas dependem de validação com a proprietária.
~~~

---

## Issue #38

- URL: https://github.com/arthurSchgg/loja-online/issues/38
- Título antes: [BACKEND] Escrever README do backend, guia local e documentação da API
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:51Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pelo projeto ou pela administração da Lojinha da Kátia,
**Eu quero** escrever readme do backend, guia local e documentação da api,
**Para** garantir uma vitrine digital segura, completa e fácil de manter.

## Escopo técnico
Documentar Maven, Java 21, DB, profiles, Swagger, variáveis, testes, deploy e Git Flow.

## Implementação esperada
- Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, DTOs, validação e Mapper conforme contexto.
- Separar Controller, Service, Repository e regras de domínio; documentar endpoints no Springdoc.
- Proteger qualquer rota administrativa; não implementar checkout.

## Critérios de aceitação
- [ ] Pessoa nova consegue subir ambiente pelo guia
- [ ] comandos verificados
- [ ] exemplos seguros.
- [ ] Atualizar documentação, implementar testes relevantes e validar PR/CI.

## Dependências
#06, #30

## Informações adicionais
- **Prioridade:** Alta
- **Labels previstas:** `backend`, `docs`, `MVP`
- **Story Points:** 3
- **Responsável:** a definir
- **Fase:** MVP

> Decisões não confirmadas dependem de validação com a proprietária.
~~~

---

## Issue #39

- URL: https://github.com/arthurSchgg/loja-online/issues/39
- Título antes: [BACKEND] Homologar MVP e contrato de integração com frontend
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:18Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pelo projeto ou pela administração da Lojinha da Kátia,
**Eu quero** homologar mvp e contrato de integração com frontend,
**Para** garantir uma vitrine digital segura, completa e fácil de manter.

## Escopo técnico
Validar catálogo, filtros, detalhes, WhatsApp, autenticação admin, publicação e conteúdo institucional.

## Implementação esperada
- Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, DTOs, validação e Mapper conforme contexto.
- Separar Controller, Service, Repository e regras de domínio; documentar endpoints no Springdoc.
- Proteger qualquer rota administrativa; não implementar checkout.

## Critérios de aceitação
- [ ] Checklist de aceite registrado
- [ ] DTOs estáveis
- [ ] nenhuma escrita pública sem auth
- [ ] sem checkout.
- [ ] Atualizar documentação, implementar testes relevantes e validar PR/CI.

## Dependências
#30, #23, #22

## Informações adicionais
- **Prioridade:** Alta
- **Labels previstas:** `backend`, `quality`, `MVP`
- **Story Points:** 5
- **Responsável:** a definir
- **Fase:** MVP

> Decisões não confirmadas dependem de validação com a proprietária.
~~~

---

## Issue #51

- URL: https://github.com/arthurSchgg/loja-online/issues/51
- Título antes: [BACKEND] Reforçar segurança contra abuso, brute force e vulnerabilidades
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:22:09Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** reforçar segurança contra abuso, brute force e vulnerabilidades,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Aplicar rate limit em endpoints sensíveis, revisar dependências, validação de URL, controle de tentativas e política de atualização.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Testes de abuso, nenhum endpoint admin aberto e revisão de segurança documentada.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Login administrativo (#18), implantação (#37)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `security`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

