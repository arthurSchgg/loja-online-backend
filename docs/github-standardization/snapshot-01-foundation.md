# Snapshot pré-padronização — lote 01-foundation

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08 (horário da consulta). O conteúdo preserva estado e descrição anterior para comparação futura. Não atesta aprovação técnica do lote, não representa alteração nas issues e não deve ser usado para reverter comentários ou informações posteriores sem comparação.

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

