# Snapshot pré-padronização — lote 06-frontend

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08. Estados e descrições observados abaixo para comparação futura. Não atesta aprovação do lote e não representa alteração nas issues.

## Issue #16

- URL: https://github.com/arthurSchgg/loja-online/issues/16
- Título antes: [BACKEND] Implementar API de informações da loja e contato
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:12Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar api de informações da loja e contato,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar GET /api/v1/store com nome comercial, slogan, descrição, contatos e redes somente quando informados e aprovados; DTO público.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Nenhum dado da loja inventado
- [ ] campos opcionais seguros
- [ ] 200 com estrutura consistente
- [ ] testes e Swagger.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #07

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `institutional`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #23

- URL: https://github.com/arthurSchgg/loja-online/issues/23
- Título antes: [BACKEND] Implementar gerenciamento administrativo de conteúdo e contatos
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:27Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar gerenciamento administrativo de conteúdo e contatos,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Criar atualização protegida de conteúdos institucionais, WhatsApp, redes e dados públicos; respeitar campos opcionais.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Mudanças refletidas em GET /api/v1/store
- [ ] validação de conteúdo/link
- [ ] admin apenas
- [ ] testes.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #16

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `admin`, `MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #30

- URL: https://github.com/arthurSchgg/loja-online/issues/30
- Título antes: [BACKEND] Implementar gerenciamento administrativo de conteúdo e contatos
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:56Z
- Corpo anterior:

~~~markdown
Duplicada de #23. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #47

- URL: https://github.com/arthurSchgg/loja-online/issues/47
- Título antes: [BACKEND] Implementar campanhas, destaques e vitrines promocionais
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:48:17Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** implementar campanhas, destaques e vitrines promocionais,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Gerenciar banners, destaques e vitrines com ordenação/publicação; descontos só após validação comercial.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Admin protegido
- [ ] API pública só conteúdo aprovado
- [ ] não alterar preços sem regra
- [ ] testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
CRUD de produtos (#21)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `catalog`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #48

- URL: https://github.com/arthurSchgg/loja-online/issues/48
- Título antes: [BACKEND] Criar editor simples de seções e páginas institucionais
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:48:19Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** criar editor simples de seções e páginas institucionais,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Gerenciar blocos de textos/imagens e posição de seções institucionais com rascunho/publicação; evitar HTML inseguro.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Apenas conteúdo publicado acessível
- [ ] admin protegido
- [ ] validações e testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Gestão institucional

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `institutional`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

