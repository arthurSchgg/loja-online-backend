# Lote 1 — Fundação e segurança

**Estado:** INVENTARIADO / NÃO APROVADO / NÃO APLICADO. **Fonte:** consulta ao GitHub em 2026-11-07, repo `arthurSchgg/loja-online`.

## Pré-requisitos
- [ ] Especificação consolidada, revisada em PR e formalmente aprovada.
- [ ] Tag documental registrada.
- [ ] Diagnóstico técnico da Fase 1 concluído.
- [ ] Aprovação técnica prévia deste lote com lista de mudanças e valores anteriores.

## Inventário de issues (estado anterior; sem alteração)

| Issue | Título atual | Estado atual | Ação proposta |
|---|---|---|---|
| [#1](https://github.com/arthurSchgg/loja-online/issues/1) | [BACKEND] Definir escopo do backend, regras de negócio e matriz de rastreabilidade | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#2](https://github.com/arthurSchgg/loja-online/issues/2) | [BACKEND] Inicializar aplicação Java 21 / Spring Boot / Maven | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#3](https://github.com/arthurSchgg/loja-online/issues/3) | [BACKEND] Configurar PostgreSQL, perfis e variáveis de ambiente | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#4](https://github.com/arthurSchgg/loja-online/issues/4) | [BACKEND] Introduzir migrações versionadas do banco | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#5](https://github.com/arthurSchgg/loja-online/issues/5) | [BACKEND] Estruturar módulos MVC, DTOs, mappers e tratamento global de erros | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#6](https://github.com/arthurSchgg/loja-online/issues/6) | [BACKEND] Configurar OpenAPI/Swagger e contrato de API | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#8](https://github.com/arthurSchgg/loja-online/issues/8) | [BACKEND] Criar validações de DTO e sanitização de entradas | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#17](https://github.com/arthurSchgg/loja-online/issues/17) | [BACKEND] Definir ADR de autenticação e autorização administrativa | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#18](https://github.com/arthurSchgg/loja-online/issues/18) | [BACKEND] Implementar autenticação administrativa segura com Spring Security | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#25](https://github.com/arthurSchgg/loja-online/issues/25) | [BACKEND] Configurar CORS seguro e cabeçalhos para frontend separado | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#26](https://github.com/arthurSchgg/loja-online/issues/26) | [BACKEND] Criar bateria de testes unitários das regras de negócio | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#27](https://github.com/arthurSchgg/loja-online/issues/27) | [BACKEND] Criar testes de integração REST, banco PostgreSQL e segurança | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#28](https://github.com/arthurSchgg/loja-online/issues/28) | [BACKEND] Configurar integração contínua GitHub Actions com Java 21 | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#32](https://github.com/arthurSchgg/loja-online/issues/32) | [BACKEND] Configurar CORS seguro e cabeçalhos para frontend separado | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#33](https://github.com/arthurSchgg/loja-online/issues/33) | [BACKEND] Criar bateria de testes unitários das regras de negócio | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#34](https://github.com/arthurSchgg/loja-online/issues/34) | [BACKEND] Criar testes de integração REST, banco PostgreSQL e segurança | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#35](https://github.com/arthurSchgg/loja-online/issues/35) | [BACKEND] Configurar integração contínua GitHub Actions com Java 21 | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#36](https://github.com/arthurSchgg/loja-online/issues/36) | [BACKEND] Configurar health check, logs e monitoramento essencial | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#37](https://github.com/arthurSchgg/loja-online/issues/37) | [BACKEND] Preparar deploy do backend e PostgreSQL em produção | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#38](https://github.com/arthurSchgg/loja-online/issues/38) | [BACKEND] Escrever README do backend, guia local e documentação da API | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#39](https://github.com/arthurSchgg/loja-online/issues/39) | [BACKEND] Homologar MVP e contrato de integração com frontend | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#51](https://github.com/arthurSchgg/loja-online/issues/51) | [BACKEND] Reforçar segurança contra abuso, brute force e vulnerabilidades | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |

## Achados específicos
CI e testes: #26/#33, #27/#34, #28/#35; CORS #25/#32. Segurança de sessão existente via PR #74; revisar lacunas do login, proteção contra abuso e JaCoCo. Issue #36 de monitoramento deve ser conciliada com decisão de não exigir monitoramento dedicado no MVP.

## Plano de alterações — ainda não autorizado
- Inventariar descrição completa, comentários, PRs vinculados, assignees, labels e milestone em cada issue.
- Preparar tabela **antes → depois** para título, ID, corpo, labels, fase, prioridade, assignee, SP (somente US), relações e estado.
- Reutilizar issue compatível e preservar a fechada; não encerrar suposta duplicação sem avaliar seus vínculos.
- Verificar inexistência de ID `US-###` ou `TASK-###` repetido antes da atribuição.
- Atualizar `docs/traceability.md` somente com identificadores reais.

## Aprovação pré-aplicação
**Responsável técnico:** não identificado formalmente.
**Decisão:** PENDENTE.
**Data:** não registrada.

## Resultado da aplicação
**Alterações aplicadas em issues:** nenhuma.
**Verificações pós-aplicação:** não executadas.
**Divergências e correções:** a apurar após autorização.

## Critério de encerramento
- [ ] Alterações individuais aprovadas.
- [ ] Campos e vínculos conferidos após edição.
- [ ] Histórico preservado; inconsistências corrigidas.
- [ ] Relatório validado e aprovação final explícita.
