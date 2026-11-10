# Matriz de rastreabilidade — backlog preparado para revisão

> **NÃO HOMOLOGADA.** A tabela combina backlog real com evidência técnica
> atualizada em 2026-11-09. Os IDs RN, campos Projects, responsáveis,
> dependências e decisões de aprovação ainda requerem conferência; código
> implementado e CI aprovado **não equivalem** a aceite de histórias.

| Grupo de requisito | Fase | História/issue principal | Tarefas técnicas vinculadas por referências recíprocas | Histórico existente | Homologação | Situação |
|---|---:|---|---|---|---|---|
| Estoque e ledger | 2 | [#81](https://github.com/arthurSchgg/loja-online/issues/81) | [#91](https://github.com/arthurSchgg/loja-online/issues/91), [#92](https://github.com/arthurSchgg/loja-online/issues/92) | #7, #21 | [HML-002 #113](https://github.com/arthurSchgg/loja-online/issues/113) | Código/CI presentes; HML pendente |
| Promoções de preço | 3 | [#82](https://github.com/arthurSchgg/loja-online/issues/82) | [#93](https://github.com/arthurSchgg/loja-online/issues/93), [#94](https://github.com/arthurSchgg/loja-online/issues/94) | #47 (banners; escopo distinto) | [HML-003 #114](https://github.com/arthurSchgg/loja-online/issues/114) | Código/CI presentes; HML pendente |
| WhatsApp com confirmação | 3 | [#83](https://github.com/arthurSchgg/loja-online/issues/83) | [#95](https://github.com/arthurSchgg/loja-online/issues/95), [#96](https://github.com/arthurSchgg/loja-online/issues/96), [#97](https://github.com/arthurSchgg/loja-online/issues/97) | #15, PR #64 | HML-003 planejada | Backlog; não homologado |
| Quiz e recomendações | 4 | [#84](https://github.com/arthurSchgg/loja-online/issues/84) | [#98](https://github.com/arthurSchgg/loja-online/issues/98), [#99](https://github.com/arthurSchgg/loja-online/issues/99) | #40, #41, #42 | [HML-004 #115](https://github.com/arthurSchgg/loja-online/issues/115) | Código/CI presentes; HML pendente |
| Personalização de velas | 5 | [#85](https://github.com/arthurSchgg/loja-online/issues/85) | [#100](https://github.com/arthurSchgg/loja-online/issues/100), [#101](https://github.com/arthurSchgg/loja-online/issues/101) | #43, #44, #45 | [HML-005 #116](https://github.com/arthurSchgg/loja-online/issues/116) | Código/CI presentes; HML pendente |
| Frontend público acessível | 6 | [#86](https://github.com/arthurSchgg/loja-online/issues/86) | [#102](https://github.com/arthurSchgg/loja-online/issues/102), [#103](https://github.com/arthurSchgg/loja-online/issues/103), [#104](https://github.com/arthurSchgg/loja-online/issues/104) | frontend separado | [HML-006 #117](https://github.com/arthurSchgg/loja-online/issues/117) | Backend presente; frontend/HML pendentes |
| Auditoria persistente | 7 | [#87](https://github.com/arthurSchgg/loja-online/issues/87) | [#105](https://github.com/arthurSchgg/loja-online/issues/105), [#106](https://github.com/arthurSchgg/loja-online/issues/106) | #24, #31 duplicata histórica | [HML-007 #118](https://github.com/arthurSchgg/loja-online/issues/118) | Auditoria implementada; operação/HML pendentes |
| Recuperação administrativa | 1 | [#88](https://github.com/arthurSchgg/loja-online/issues/88) | [#107](https://github.com/arthurSchgg/loja-online/issues/107), [#108](https://github.com/arthurSchgg/loja-online/issues/108) | #18 | [HML-001 #112](https://github.com/arthurSchgg/loja-online/issues/112) | Recuperação implementada; SMTP/HML pendentes |
| Gate JaCoCo 80% | 1 | [#89](https://github.com/arthurSchgg/loja-online/issues/89) | [#109](https://github.com/arthurSchgg/loja-online/issues/109), [#110](https://github.com/arthurSchgg/loja-online/issues/110) | #26, #27, #28 | [HML-001 #112](https://github.com/arthurSchgg/loja-online/issues/112) | Gate JaCoCo ativo; HML pendente |
| Backup/restauração automática | 7 | [#52](https://github.com/arthurSchgg/loja-online/issues/52) | Reutilizar #52; tarefas existentes a inventariar | #90 encerrada como duplicata | [HML-007 #118](https://github.com/arthurSchgg/loja-online/issues/118) | Backup/restore CI; rotina externa pendente |

## Regras e pendências de validação
- [ ] Conferir RN originais e completar RN → US por regra única; duplicações RN históricas documentadas em `consistency-audit.md`.
- [ ] Confirmar histórias já representadas nas issues antigas e **não duplicar** escopo com novas US; quando houver sobreposição, registrar o motivo da divisão ou encerrar a duplicata.
- [ ] Confirmar os responsáveis, prioridades e Story Points diretamente nos campos do GitHub Projects (não apenas no corpo das issues).
- [ ] Confirmar relações impeditivas e ausência de ciclos, inclusive entre fases.
- [ ] Revisar se cada história e suas tarefas têm o **mesmo responsável**.
- [ ] Associar testes, PRs efetivamente aprovados, evidências e issue HML da fase; nenhuma HML foi homologada.
- [ ] Formalizar aprovação técnica da especificação, diagnóstico, sete lotes e autorização AUTH-001 antes de implementar a Fase 1.

## Evidências técnicas atualizadas (não são aceites de US)

**Referência de código:** `develop` após [PR #152](https://github.com/arthurSchgg/loja-online/pull/152),
SHA `de93e2d14189548e57126e8a951a6d5b1086bcef`. Ver
[diagnóstico atualizado](phase-1-diagnostic.md) e
[matriz de evidências](phase-1-evidence-review-2026-11-09.md).

| Área / grupo | PRs / testes observados | Situação técnica versus governança |
|---|---|---|
| Fundação e segurança | #143–#152; `verify`, `staging-https-smoke` | Implementado/testado localmente; HML-001 e AUTH-001 abertas |
| Estoque | Integração #134; testes `StockIntegrationTest` | Implementado com testes; aceite/HML-002 pendente |
| Promoções | Integração #134; `ProductPromotionIntegrationTest` | Implementado com testes; aceite/HML-003 pendente |
| WhatsApp | Integração #134; `WhatsAppConfirmationTest` | Implementado com testes; frontend/HML-003 pendentes |
| Experiências | Integração #134; `QuizRecommendationsIntegrationTest` | Implementado com testes; homologação HML-004 pendente |
| Serviços | Integração #134; `CandleOptionsIntegrationTest` | Implementado com testes; homologação HML-005 pendente |
| Conteúdo e frontend | APIs em `develop`; frontend separado ainda pendente no #135 | Backend parcial; HML-006 pendente |
| Auditoria, CI e backup | Integração #134, `PersistentAuditIntegrationTest`, scripts PostgreSQL e CI | Testes locais; retenção/restore operacional/HML-007 pendentes |

A última execução confirmada na revisão foi
[CI #42726790910](https://github.com/arthurSchgg/loja-online/actions/runs/42726790910)
(**success** nos jobs Maven/JaCoCo/PostgreSQL e staging HTTPS). O
CI não atesta Story Points, responsável, aprovação de US, padrão
WCAG, dados comerciais corretos nem aceite das sete HML.

**Atenção:** o status das issues na tabela preserva o histórico
existente. "Código presente" não significa que a issue possa ser movida
automaticamente para `Done`; exige revisão individual de critérios,
evidências, dependências e aprovação do responsável técnico.

## Fluxo obrigatório
Especificação aprovada + tag → diagnóstico técnico formal → sete lotes verificados/aprovados → [AUTH-001](https://github.com/arthurSchgg/loja-online/issues/80) autorizada → implementação Ready → testes + homologação formal por fase → autorização de release.
