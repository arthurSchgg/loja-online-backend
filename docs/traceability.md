# Matriz de rastreabilidade — backlog preparado para revisão

> **NÃO HOMOLOGADA.** Os números de issues e seus vínculos abaixo **são reais**, mas os IDs de regras RN ainda precisam ser reconciliados e os campos Projects, responsáveis e homologações precisam ser validados. As histórias e tarefas foram criadas durante preparação autorizada pelo usuário para revisão posterior; nenhuma aprovação técnica foi registrada.

| Grupo de requisito | Fase | História/issue principal | Tarefas técnicas vinculadas por referências recíprocas | Histórico existente | Homologação | Situação |
|---|---:|---|---|---|---|---|
| Estoque e ledger | 2 | [#81](https://github.com/arthurSchgg/loja-online/issues/81) | [#91](https://github.com/arthurSchgg/loja-online/issues/91), [#92](https://github.com/arthurSchgg/loja-online/issues/92) | #7, #21 | HML-002 planejada | Backlog; não homologado |
| Promoções de preço | 3 | [#82](https://github.com/arthurSchgg/loja-online/issues/82) | [#93](https://github.com/arthurSchgg/loja-online/issues/93), [#94](https://github.com/arthurSchgg/loja-online/issues/94) | #47 (banners; escopo distinto) | HML-003 planejada | Backlog; não homologado |
| WhatsApp com confirmação | 3 | [#83](https://github.com/arthurSchgg/loja-online/issues/83) | [#95](https://github.com/arthurSchgg/loja-online/issues/95), [#96](https://github.com/arthurSchgg/loja-online/issues/96), [#97](https://github.com/arthurSchgg/loja-online/issues/97) | #15, PR #64 | HML-003 planejada | Backlog; não homologado |
| Quiz e recomendações | 4 | [#84](https://github.com/arthurSchgg/loja-online/issues/84) | [#98](https://github.com/arthurSchgg/loja-online/issues/98), [#99](https://github.com/arthurSchgg/loja-online/issues/99) | #40, #41, #42 | HML-004 planejada | Backlog; não homologado |
| Personalização de velas | 5 | [#85](https://github.com/arthurSchgg/loja-online/issues/85) | [#100](https://github.com/arthurSchgg/loja-online/issues/100), [#101](https://github.com/arthurSchgg/loja-online/issues/101) | #43, #44, #45 | HML-005 planejada | Backlog; não homologado |
| Frontend público acessível | 6 | [#86](https://github.com/arthurSchgg/loja-online/issues/86) | [#102](https://github.com/arthurSchgg/loja-online/issues/102), [#103](https://github.com/arthurSchgg/loja-online/issues/103), [#104](https://github.com/arthurSchgg/loja-online/issues/104) | frontend separado | HML-006 planejada | Backlog; não homologado |
| Auditoria persistente | 7 | [#87](https://github.com/arthurSchgg/loja-online/issues/87) | [#105](https://github.com/arthurSchgg/loja-online/issues/105), [#106](https://github.com/arthurSchgg/loja-online/issues/106) | #24, #31 duplicata histórica | HML-007 planejada | Backlog; não homologado |
| Recuperação administrativa | 1 | [#88](https://github.com/arthurSchgg/loja-online/issues/88) | [#107](https://github.com/arthurSchgg/loja-online/issues/107), [#108](https://github.com/arthurSchgg/loja-online/issues/108) | #18 | HML-001 planejada | Backlog; não homologado |
| Gate JaCoCo 80% | 1 | [#89](https://github.com/arthurSchgg/loja-online/issues/89) | [#109](https://github.com/arthurSchgg/loja-online/issues/109), [#110](https://github.com/arthurSchgg/loja-online/issues/110) | #26, #27, #28 | HML-001 planejada | Backlog; não homologado |
| Backup/restauração automática | 7 | [#52](https://github.com/arthurSchgg/loja-online/issues/52) | Reutilizar #52; tarefas existentes a inventariar | #90 encerrada como duplicata | HML-007 planejada | Backlog; não homologado |

## Regras e pendências de validação
- [ ] Conferir RN originais e completar RN → US por regra única; duplicações RN históricas documentadas em `consistency-audit.md`.
- [ ] Confirmar histórias já representadas nas issues antigas e **não duplicar** escopo com novas US; quando houver sobreposição, registrar o motivo da divisão ou encerrar a duplicata.
- [ ] Confirmar os responsáveis, prioridades e Story Points diretamente nos campos do GitHub Projects (não apenas no corpo das issues).
- [ ] Confirmar relações impeditivas e ausência de ciclos, inclusive entre fases.
- [ ] Revisar se cada história e suas tarefas têm o **mesmo responsável**.
- [ ] Associar testes, PRs efetivamente aprovados, evidências e issue HML da fase; nenhuma HML foi homologada.
- [ ] Formalizar aprovação técnica da especificação, diagnóstico, sete lotes e autorização AUTH-001 antes de implementar a Fase 1.

## Evidências já verificadas
- Repositório backend `develop`: Java 21 / Spring Boot 3.5.7, controllers/services/JPA, Flyway V1–V6, segurança de sessão e CSRF, Docker staging local e CI.
- `pom.xml` e `.github/workflows/ci.yml`: gate JaCoCo 80% não identificado na inspeção inicial.
- `catalog/entity/Product.java`: ausência de campos de estoque quantitativo e histórico na entidade observada.
- `docs/WHATSAPP.md`: contrato GET de link contextualizado não documenta confirmação obrigatória.
- [Execução do CI documental](https://github.com/arthurSchgg/loja-online/actions/runs/42438620814): sucesso em verificação do PR de documentação; isso **não comprova cobertura funcional**.

## Fluxo obrigatório
Especificação aprovada + tag → diagnóstico técnico formal → sete lotes verificados/aprovados → [AUTH-001](https://github.com/arthurSchgg/loja-online/issues/80) autorizada → implementação Ready → testes + homologação formal por fase → autorização de release.
