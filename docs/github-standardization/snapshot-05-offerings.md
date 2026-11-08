# Snapshot pré-padronização — lote 05-offerings

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08 (horário da consulta). O conteúdo preserva estado e descrição anterior para comparação futura. Não atesta aprovação técnica do lote, não representa alteração nas issues e não deve ser usado para reverter comentários ou informações posteriores sem comparação.

## Issue #43

- URL: https://github.com/arthurSchgg/loja-online/issues/43
- Título antes: [BACKEND] Definir regras de negócio para velas, oficinas e locações
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:51Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** definir regras de negócio para velas, oficinas e locações,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Validar ofertas reais de velas, oficinas e locações, informações de preço, disponibilidade e comunicação WhatsApp; não presumir agenda.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] ADR aprovada pela loja
- [ ] dados e fronteiras registrados.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Escopo do projeto (#1)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `candles`, `post-MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #44

- URL: https://github.com/arthurSchgg/loja-online/issues/44
- Título antes: [BACKEND] Implementar consulta pública de ofertas de velas e serviços
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:47:56Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** implementar consulta pública de ofertas de velas e serviços,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Modelar ofertas de velas/serviços ou reutilizar Product conforme decisão; expor apenas conteúdo publicado.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Valores apenas confirmados
- [ ] visibilidade aplicada
- [ ] paginação e testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
ADR sobre velas e oficinas

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `candles`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #45

- URL: https://github.com/arthurSchgg/loja-online/issues/45
- Título antes: [BACKEND] Gerenciar serviços e ofertas de velas no administrativo
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T12:48:14Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** gerenciar serviços e ofertas de velas no administrativo,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
CRUD de velas, oficinas e locações com textos, fotos e publicação; contatos apenas se confirmados.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Acesso admin
- [ ] dados obrigatórios validados
- [ ] testes
- [ ] atualização refletida na API pública.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Consulta pública de velas, Spring Security (#18)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `candles`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #46

- URL: https://github.com/arthurSchgg/loja-online/issues/46
- Título antes: [BACKEND] Avaliar e implementar formulário de interesse com LGPD
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:21:58Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** avaliar e implementar formulário de interesse com lgpd,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Somente após aprovação: formulário para consulta de oficina/locação com coleta mínima, proteção anti-spam, políticas de retenção e consentimento quando aplicável.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Nenhum dado sensível desnecessário
- [ ] finalidade explicada
- [ ] testes e limitação de requisições.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
ADR sobre velas e oficinas

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `integration`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

