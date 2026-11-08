# Plano de preenchimento do GitHub Projects — revisão de campos

> **Estado:** PROPOSTO, NÃO APLICADO. O conector utilizado nesta execução consegue criar/editar issues, mas não expôs operações para manipular os campos personalizados do **GitHub Projects v2**. Os valores desta tabela não foram registrados no Projects. Não substituir assignee sem designação técnica.

## Histórias criadas para lacunas identificadas

| Issue | Identificador | Fase | Prioridade proposta | Story Points propostos | Status | Responsável |
|---|---|---:|---|---:|---|---|
| [#81](https://github.com/arthurSchgg/loja-online/issues/81) | US-001 | 2 | Alta | 5 | Backlog | A definir |
| [#82](https://github.com/arthurSchgg/loja-online/issues/82) | US-002 | 3 | Alta | 5 | Backlog | A definir |
| [#83](https://github.com/arthurSchgg/loja-online/issues/83) | US-003 | 3 | Alta | 8 | Backlog | A definir |
| [#84](https://github.com/arthurSchgg/loja-online/issues/84) | US-004 | 4 | Alta | 5 | Backlog | A definir |
| [#85](https://github.com/arthurSchgg/loja-online/issues/85) | US-005 | 5 | Alta | 5 | Backlog | A definir |
| [#86](https://github.com/arthurSchgg/loja-online/issues/86) | US-006 | 6 | Alta | 8 | Backlog | A definir |
| [#87](https://github.com/arthurSchgg/loja-online/issues/87) | US-007 | 7 | Alta | 5 | Backlog | A definir |
| [#88](https://github.com/arthurSchgg/loja-online/issues/88) | US-008 | 1 | Alta | 8 | Backlog | A definir |
| [#89](https://github.com/arthurSchgg/loja-online/issues/89) | US-009 | 1 | Alta | 3 | Backlog | A definir |
| [#52](https://github.com/arthurSchgg/loja-online/issues/52) | ID US pendente de padronização | 7 | Alta | Revisar 5 | Backlog | A definir |

**US-010/#90:** foi encerrada como duplicata da #52. O identificador US-010 não deve ser reutilizado sem uma decisão formal de renumeração (histórico preservado).

## Tarefas técnicas vinculadas
| História | Backend | Frontend | Testes |
|---|---|---|---|
| #81 | #91 | — | #92 |
| #82 | #93 | — | #94 |
| #83 | #95 | #96 | #97 |
| #84 | #98 | — | #99 |
| #85 | #100 | — | #101 |
| #86 | — | #102, #103 | #104 |
| #87 | #105 | — | #106 |
| #88 | #107 | — | #108 |
| #89 | #109 | — | #110 |
| #52 | Reutilizar tarefas antigas compatíveis | — | A confirmar |

Nenhuma TASK recebe Story Points. O responsável de cada TASK deve ser o mesmo responsável da história.

## Pendências antes do primeiro Ready
1. Aprovação formal da especificação, diagnóstico e sete lotes.
2. Resolver sobreposição #87 com #24 e backlog legado #22/#29, #23/#30, #24/#31, #25/#32, #26/#33, #27/#34 e #28/#35.
3. Definir formalmente responsáveis técnicos e refletir os mesmos nas US e tarefas.
4. Preencher no **Project v2 real** os campos `Phase`, `Priority`, `Story Points`, `Status`; confirmar grafia dos campos e IDs.
5. Verificar relações impeditivas e ciclos.
6. Comprovar aprovação AUTH-001 e somente então liberar histórias de Fase 1 aptas.

## Padrão de estimativa
1, 2, 3, 5, 8 ou 13 pontos, aplicados exclusivamente a US selecionadas para versão. Manter pós-MVP sem estimativa enquanto não selecionado.
