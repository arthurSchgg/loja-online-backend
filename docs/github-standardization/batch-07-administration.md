# Lote 7 — Administração, operações e qualidade

**Estado:** INVENTARIADO / NÃO APROVADO / NÃO APLICADO. **Fonte:** consulta ao GitHub em 2026-11-07, repo `arthurSchgg/loja-online`.

## Pré-requisitos
- [ ] Especificação consolidada, revisada em PR e formalmente aprovada.
- [ ] Tag documental registrada.
- [ ] Diagnóstico técnico da Fase 1 concluído.
- [ ] Aprovação técnica prévia deste lote com lista de mudanças e valores anteriores.

## Inventário de issues (estado anterior; sem alteração)

| Issue | Título atual | Estado atual | Ação proposta |
|---|---|---|---|
| [#24](https://github.com/arthurSchgg/loja-online/issues/24) | [BACKEND] Implementar auditoria mínima de alterações administrativas | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#31](https://github.com/arthurSchgg/loja-online/issues/31) | [BACKEND] Implementar auditoria mínima de alterações administrativas | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#50](https://github.com/arthurSchgg/loja-online/issues/50) | [BACKEND] Implementar métricas agregadas de interação preservando privacidade | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#52](https://github.com/arthurSchgg/loja-online/issues/52) | [BACKEND] Implementar e testar backups com recuperação do PostgreSQL | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#53](https://github.com/arthurSchgg/loja-online/issues/53) | [BACKEND] Otimizar desempenho de consultas, paginação e cache público | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |

## Achados específicos
#24/#31 potencialmente duplicadas; #50 propõe analytics pós-MVP fora do escopo corrente; #52 scripts de backup versus rotina automática, retenção e cópia externa; #53 avaliação de cache seletivo e desempenho. Analisar também #51 (referenciada no lote 1) como transversal.

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


## Backlog adicional preparado para revisão
As issues abaixo já existem no GitHub. Sua **criação não constitui aprovação técnica, execução do lote ou atribuição de campos do Projects**.

| Issue real | Natureza | Situação |
|---|---|---|
| [#87](https://github.com/arthurSchgg/loja-online/issues/87) | US complementar (avaliar sobreposição com #24) | Pendente de padronização aprovada |
| [#105](https://github.com/arthurSchgg/loja-online/issues/105) | Tarefa técnica vinculada | Pendente de padronização aprovada |
| [#106](https://github.com/arthurSchgg/loja-online/issues/106) | Tarefa técnica vinculada | Pendente de padronização aprovada |
| [#52](https://github.com/arthurSchgg/loja-online/issues/52) | Issue principal reaproveitada | Pendente de padronização aprovada |

**Referências de comparação:** #24 (auditoria antiga), #31 (duplicata histórica), #52 (backup principal), #90 (duplicata encerrada).

### Validação antes da aplicação
- [ ] Verificar que nenhuma issue nova reproduz por inteiro trabalho já concluído ou aberto.
- [ ] Registrar snapshots antes/depois de labels, títulos, descrições, responsáveis e vínculos.
- [ ] Validar IDs de `US-###` e `TASK-###` em todas as issues, inclusive fechadas.
- [ ] Atualizar campos reais do GitHub Projects (fase, status, prioridade, SP e assignee).
- [ ] Registrar aprovação técnica explícita e resultado por lote.
