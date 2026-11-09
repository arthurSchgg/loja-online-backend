# Lote 2 — Catálogo e estoque

**Estado:** INVENTARIADO / NÃO APROVADO / NÃO APLICADO. **Fonte:** consulta ao GitHub em 2026-11-07, repo `arthurSchgg/loja-online`.

## Pré-requisitos
- [ ] Especificação consolidada, revisada em PR e formalmente aprovada.
- [ ] Tag documental registrada.
- [ ] Diagnóstico técnico da Fase 1 concluído.
- [ ] Aprovação técnica prévia deste lote com lista de mudanças e valores anteriores.

## Inventário de issues (estado anterior; sem alteração)

| Issue | Título atual | Estado atual | Ação proposta |
|---|---|---|---|
| [#7](https://github.com/arthurSchgg/loja-online/issues/7) | [BACKEND] Modelar entidades e relacionamentos de catálogo | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#9](https://github.com/arthurSchgg/loja-online/issues/9) | [BACKEND] Implementar listagem pública de produtos publicados | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#10](https://github.com/arthurSchgg/loja-online/issues/10) | [BACKEND] Implementar busca textual e filtros combináveis de produtos | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#11](https://github.com/arthurSchgg/loja-online/issues/11) | [BACKEND] Implementar consulta pública de detalhes de produto | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#12](https://github.com/arthurSchgg/loja-online/issues/12) | [BACKEND] Implementar listagem pública de marcas | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#13](https://github.com/arthurSchgg/loja-online/issues/13) | [BACKEND] Implementar listagem pública de categorias | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#14](https://github.com/arthurSchgg/loja-online/issues/14) | [BACKEND] Definir armazenamento e validação das imagens de produtos | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#19](https://github.com/arthurSchgg/loja-online/issues/19) | [BACKEND] Implementar CRUD administrativo de marcas | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#20](https://github.com/arthurSchgg/loja-online/issues/20) | [BACKEND] Implementar CRUD administrativo de categorias | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#21](https://github.com/arthurSchgg/loja-online/issues/21) | [BACKEND] Implementar CRUD administrativo de produtos e publicação | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#22](https://github.com/arthurSchgg/loja-online/issues/22) | [BACKEND] Gerenciar imagens de produtos na área administrativa | open | Revisar regras, critérios, vínculos, labels e ID antes de editar |
| [#29](https://github.com/arthurSchgg/loja-online/issues/29) | [BACKEND] Gerenciar imagens de produtos na área administrativa | closed | Revisar regras, critérios, vínculos, labels e ID antes de editar |

## Achados específicos
Imagens #22/#29 possivelmente sobrepostas, mas estados diferentes. Estoque transacional/histórico e promoções não devem ser presumidos implementados pelo catálogo atual. Preservar histórico das issues fechadas.

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
| [#81](https://github.com/arthurSchgg/loja-online/issues/81) | História proposta | Pendente de padronização aprovada |
| [#91](https://github.com/arthurSchgg/loja-online/issues/91) | Tarefa técnica vinculada | Pendente de padronização aprovada |
| [#92](https://github.com/arthurSchgg/loja-online/issues/92) | Tarefa técnica vinculada | Pendente de padronização aprovada |

**Referências de comparação:** #7, #21, #22 (imagens).

### Validação antes da aplicação
- [ ] Verificar que nenhuma issue nova reproduz por inteiro trabalho já concluído ou aberto.
- [ ] Registrar snapshots antes/depois de labels, títulos, descrições, responsáveis e vínculos.
- [ ] Validar IDs de `US-###` e `TASK-###` em todas as issues, inclusive fechadas.
- [ ] Atualizar campos reais do GitHub Projects (fase, status, prioridade, SP e assignee).
- [ ] Registrar aprovação técnica explícita e resultado por lote.
