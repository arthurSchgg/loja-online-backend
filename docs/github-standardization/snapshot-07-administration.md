# Snapshot pré-padronização — lote 07-administration

> **Evidência somente leitura.** Extraído das issues existentes do GitHub em 2026-11-08 (horário da consulta). O conteúdo preserva estado e descrição anterior para comparação futura. Não atesta aprovação técnica do lote, não representa alteração nas issues e não deve ser usado para reverter comentários ou informações posteriores sem comparação.

## Issue #24

- URL: https://github.com/arthurSchgg/loja-online/issues/24
- Título antes: [BACKEND] Implementar auditoria mínima de alterações administrativas
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:18:49Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** visitante da loja, administradora ou integrante da equipe responsável,
**Eu quero** implementar auditoria mínima de alterações administrativas,
**Para** manter a Lojinha da Kátia funcional, segura e administrável.

## Contexto e objetivo
Auditar ator, tipo de operação, entidade, identificador e data de alterações críticas, evitando senhas, tokens e dados sensíveis.

## Diretrizes de implementação
- **Backend:** Java 21, Spring Boot, MVC, Spring Data JPA/PostgreSQL e Bean Validation.
- **Padrão:** modularização por domínio, Repository, Service, Controller, DTOs e Mapper quando pertinente.
- **Contrato HTTP:** usar códigos de status adequados, erros padronizados e Swagger quando houver endpoint.
- **Segurança:** operações administrativas requerem autenticação/autorização; **não publicar endpoints de escrita abertos**.
- **Escopo:** não há carrinho, checkout nem processamento de pagamentos nesta fase.

## Critérios de aceitação
- [ ] Operações rastreáveis
- [ ] logs protegidos
- [ ] testes sem gravação de segredos.
- [ ] Documentação técnica atualizada.
- [ ] Testes automatizados relevantes implementados ou explicitamente justificados.
- [ ] Pull request revisado, sem credenciais e com CI verde.

## Dependências
- Issue #18
- Issue #21

## Informações adicionais
- **Prioridade:** Alta
- **Labels propostas:** `backend`, `security`, `MVP`, `feature`
- **Esforço (Story Points):** 3
- **Responsável (Assignee):** a definir
- **Fase:** MVP

> Para detalhes comerciais não aprovados (preços, WhatsApp, serviços, horários e regras de quizzes), registrar ADR e validar antes de codificar.
~~~

---

## Issue #31

- URL: https://github.com/arthurSchgg/loja-online/issues/31
- Título antes: [BACKEND] Implementar auditoria mínima de alterações administrativas
- Estado antes: closed
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:20:59Z
- Corpo anterior:

~~~markdown
Duplicada de #24. Acompanhar a issue original para evitar trabalho repetido.
~~~

---

## Issue #50

- URL: https://github.com/arthurSchgg/loja-online/issues/50
- Título antes: [BACKEND] Implementar métricas agregadas de interação preservando privacidade
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:22:06Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** implementar métricas agregadas de interação preservando privacidade,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Métricas opcionais agregadas de catálogo/experiências apenas com finalidade e retenção aprovadas; sem rastreamento pessoal por padrão.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Não coleta dados desnecessários
- [ ] relatórios agregados
- [ ] testes.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Homologação de MVP (#39)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `quality`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

## Issue #52

- URL: https://github.com/arthurSchgg/loja-online/issues/52
- Título antes: [BACKEND] Implementar e testar backups com recuperação do PostgreSQL
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-08T02:38:08Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** implementar e testar backups com recuperação do postgresql,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Configurar backups, retenção e criptografia conforme hospedagem; testar restauração em staging, registrar responsáveis.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Restauração demonstrada
- [ ] documentação e acesso restrito.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Deploy (#37)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `devops`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.

---
## Complemento do planejamento consolidado — revisão para MVP
> Alteração de **critérios propostos**; não declara implementação concluída, nem muda o status da issue.

- [ ] Automatizar cópias diárias com retenção mínima de **7 diários e 4 semanais**.
- [ ] Manter **4 cópias semanais externas criptografadas** e segredos fora do repositório.
- [ ] Documentar evidências de **restauração isolada mensal**, com responsável e resultado.
- [ ] Validar integridade, acesso restrito, falhas, alertas operacionais cabíveis e revisão de recuperação.
- [ ] Respeitar aprovação técnica da especificação, diagnóstico e lote de padronização antes da execução.

**Rastreabilidade:** proposta duplicada [#90](https://github.com/arthurSchgg/loja-online/issues/90) encerrada em favor desta issue principal. Metadados de prioridade/SP devem ser reconciliados no GitHub Projects, não em labels.
~~~

---

## Issue #53

- URL: https://github.com/arthurSchgg/loja-online/issues/53
- Título antes: [BACKEND] Otimizar desempenho de consultas, paginação e cache público
- Estado antes: open
- Assignees antes: (nenhum)
- Labels antes: (nenhuma)
- Milestone antes: (nenhuma)
- Data de atualização observada: 2026-11-06T11:22:13Z
- Corpo anterior:

~~~markdown
## História de usuário
**Como** pessoa responsável pelo sistema da Lojinha da Kátia,
**Eu quero** otimizar desempenho de consultas, paginação e cache público,
**Para** ampliar os serviços digitais com qualidade, segurança e controle administrativo.

## Escopo funcional e técnico
Medir JPA, consultas N+1, índices e cache público; otimizar só com baseline e preservar invalidação na publicação.

## Diretrizes técnicas
- Java 21, Spring Boot, Spring Data JPA/PostgreSQL, Maven, Spring MVC e DTOs.
- Criar Controller/Service/Repository/Mapper e documentação Swagger quando houver endpoints.
- Respeitar segurança administrativa, validação e LGPD; sem checkout ou pagamentos.
- Solicitar validação das regras comerciais antes de implementação.

## Critérios de aceitação
- [ ] Métricas antes/depois
- [ ] testes sem regressão
- [ ] cache atualizado após alterações.
- [ ] Cobertura automatizada compatível com o escopo.
- [ ] README/Swagger atualizados quando necessário.
- [ ] Pull request validado no CI.

## Dependências
Busca (#10), deploy (#37)

## Informações adicionais
- **Prioridade:** Média / evolução pós-MVP
- **Labels previstas:** `backend`, `quality`, `post-MVP`, `feature`
- **Esforço (Story Points):** 5
- **Responsável:** a definir

> Decisões de negócio precisam da aprovação da proprietária.
~~~

---

