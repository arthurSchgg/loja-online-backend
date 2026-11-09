# Snapshot pré-padronização — lote 04-experiences

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08 (horário da consulta). O conteúdo preserva estado e descrição anterior para comparação futura. Não atesta aprovação técnica do lote, não representa alteração nas issues e não deve ser usado para reverter comentários ou informações posteriores sem comparação.

## Issue #40

- URL: https://github.com/arthurSchgg/loja-online/issues/40
- Título antes: [BACKEND] Definir modelo e regras das experiências interativas e quizzes
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:20Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** responsável pelo projeto ou pela administração da Lojinha da Kátia,
**Eu quero** definir modelo e regras das experiências interativas e quizzes,
**Para** garantir uma vitrine digital segura, completa e fácil de manter.

## Escopo técnico
Definir perguntas, alternativas, pontuação, resultados e vínculos com catálogo, sem presumir regras comerciais.

## Implementação esperada
- Java 21, Spring Boot, Spring MVC, Spring Data JPA, PostgreSQL, DTOs, validação e Mapper conforme contexto.
- Separar Controller, Service, Repository e regras de domínio; documentar endpoints no Springdoc.
- Proteger qualquer rota administrativa; não implementar checkout.

## Critérios de aceitação
- [ ] Documento de decisão aprovado
- [ ] privacidade e versionamento definidos.
- [ ] Atualizar documentação, implementar testes relevantes e validar PR/CI.

## Dependências
#01

## Informações adicionais
- **Prioridade:** Média
- **Labels previstas:** `backend`, `experiences`, `post-MVP`
- **Story Points:** 5
- **Responsável:** a definir
- **Fase:** Post-MVP

> Decisões não confirmadas dependem de validação com a proprietária.
~~~

---

## Issue #41

- URL: https://github.com/arthurSchgg/loja-online/issues/41
- Título antes: [BACKEND] Implementar API pública de experiências e avaliação de respostas
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:47Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** implementar api pública de experiências e avaliação de respostas,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Consultar quizzes publicados e avaliar respostas via regras aprovadas, limitando payload e evitando armazenar dados pessoais sem necessidade.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Rascunhos invisíveis
- [ ] validação 400
- [ ] resultados determinísticos
- [ ] testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Definição das regras (#40), autenticação administrativa (#18)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `experiences`, `post-MVP`, `feature`
- **Esforço (Story Points):** 8
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #42

- URL: https://github.com/arthurSchgg/loja-online/issues/42
- Título antes: [BACKEND] Criar gerenciamento administrativo de quizzes e resultados
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:54Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** criar gerenciamento administrativo de quizzes e resultados,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
CRUD de perguntas, alternativas, fluxos de quiz e resultados, com publicação e preview.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Admin restrito
- [ ] rascunhos não aparecem
- [ ] quiz inconsistente não pode publicar
- [ ] testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
API de experiências, autenticação (#18)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `experiences`, `post-MVP`, `feature`
- **Esforço (Story Points):** 8
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #49

- URL: https://github.com/arthurSchgg/loja-online/issues/49
- Título antes: [BACKEND] Decidir necessidade de backend para cartões digitais
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:22:05Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** decidir necessidade de backend para cartões digitais,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Examinar se cartões personalizados podem ser feitos somente com JS no frontend ou requerem backend; evitar persistência de mensagens privadas.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] ADR de escopo com justificativa
- [ ] proteção de dados e contratos se aplicável.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Definição de experiências (#40)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `experiences`, `post-MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

