# Snapshot pré-padronização — lote 02-catalog

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08 (horário da consulta). O conteúdo preserva estado e descrição anterior para comparação futura. Não atesta aprovação técnica do lote, não representa alteração nas issues e não deve ser usado para reverter comentários ou informações posteriores sem comparação.

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

