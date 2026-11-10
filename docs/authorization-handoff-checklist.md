# Checklist de transição — documentação, AUTH-001 e homologação

> **Documento de trabalho para revisão, não ata de aprovação.**
> Snapshot da base técnica: `develop` em
> `de93e2d14189548e57126e8a951a6d5b1086bcef` (PR #152).
> Criado em 2026-11-09. Não concede autorização para execução de
> produção, transferência para `main` ou provisionamento AWS.

## A. Consolidação da especificação — issue #79

- [ ] Confrontar fontes originais com `docs/business-rules.md`,
  `docs/requirements.md`, `docs/architecture.md` e
  `docs/consistency-audit.md`; registrar origem e prevalência de cada regra.
- [ ] Reconciliar RN-647–650, RN-1039–1041 e RN-1139–1144
  **sem inventar novos números**; criar tabela antiga → nova após consulta
  aos textos originais.
- [ ] Confirmar regras vigentes de estoque, promoções, mensagens de WhatsApp,
  experiência/quiz, velas, serviços e segurança com critérios testáveis.
- [ ] Reconciliar `docs/traceability.md` (RN → US → tarefas → testes →
  HML) com IDs, responsáveis, SP, prioridade, bloqueios e GitHub Projects.
- [ ] Identificar responsável técnico autorizado; registrar revisão,
  decisão expressa, data, commit documental aprovado e pendências.
- [ ] Somente depois de aprovação da especificação, criar a tag
  `spec-vX.Y.Z` apontando para o SHA aprovado.

**Referência:** [#79 — Aprovação da especificação](https://github.com/arthurSchgg/loja-online/issues/79).
**Estado:** revisão não concluída, assinatura não registrada.

## B. Diagnóstico técnico da Fase 1

- [x] Atualizar o diagnóstico à luz de `develop` após PRs #134 e #143–#152
  e linkar evidências dos pipelines.
- [x] Distinguir código implementado, testes automatizados, operação
  pendente e homologação formal.
- [ ] Congelar a referência do código na versão de especificação aprovada;
  reavaliar matriz F1-01–F1-15 no SHA final.
- [ ] Anexar evidências operacionais em ambiente autorizado: TLS, SMTP,
  CORS, cookies, CSRF, sessão, recuperação, backup e restauração.
- [ ] Registrar aprovação formal do diagnóstico por responsável técnico.

**Referências:**
[diagnóstico atualizado](phase-1-diagnostic.md) e
[matriz com evidências](phase-1-evidence-review-2026-11-09.md).
**Estado:** revisão técnica preparada, diagnóstico formal pendente.

## C. Sete lotes de padronização de issues

| Ordem | Lote e arquivo | Estado neste levantamento | Evidência para encerrar |
|---|---|---|---|
| 1 | [Fundação e segurança](github-standardization/batch-01-foundation.md) | Inventariado, não aprovado/aplicado | Tabela antes/depois, PR/issues, aprovação |
| 2 | [Catálogo e estoque](github-standardization/batch-02-catalog.md) | Inventariado, não aprovado/aplicado | IDs US/TASK, campos Projects e revisão |
| 3 | [Promoções e WhatsApp](github-standardization/batch-03-promotions.md) | Inventariado, não aprovado/aplicado | Migração de escopo, relações e revisão |
| 4 | [Experiências](github-standardization/batch-04-experiences.md) | Inventariado, não aprovado/aplicado | Critérios dos quizzes, dependências |
| 5 | [Velas e serviços](github-standardization/batch-05-offerings.md) | Inventariado, não aprovado/aplicado | Verificar contato só pelo WhatsApp |
| 6 | [Conteúdo e frontend](github-standardization/batch-06-frontend.md) | Inventariado, não aprovado/aplicado | Repositório frontend e rastreabilidade |
| 7 | [Administração e qualidade](github-standardization/batch-07-administration.md) | Inventariado, não aprovado/aplicado | Auditoria, backup, segurança, evidências |

Para **cada** lote:
- [ ] Conferir issue individual (descrição, comentários, histórico,
  assignee, labels, milestone, campos de Project e relações).
- [ ] Comparar valores **antes → depois**, não sobrescrever históricos;
  não fechar issues potenciais duplicatas só pelo título.
- [ ] Obter aprovação técnica pré-aplicação, executar somente mudanças
  aprovadas e registrar links e evidências de pós-verificação.
- [ ] Registrar decisão técnica final do lote.

## D. Autorização e homologação

- [ ] Após A, B e C, registrar a decisão **AUTORIZADA** ou **NÃO AUTORIZADA**
  em [AUTH-001 #80](https://github.com/arthurSchgg/loja-online/issues/80),
  com responsável técnico, data e links de aprovação.
- [ ] Executar e documentar HML-001–007 com dados e critérios aprovados
  (issues [#112](https://github.com/arthurSchgg/loja-online/issues/112)
  a [#118](https://github.com/arthurSchgg/loja-online/issues/118)).
- [ ] Testar SMTP real com TLS e autorização do provedor; não confundir
  Mailpit/CI com entrega externa.
- [ ] Restaurar backup de base autorizada e avaliar agendamento, cópia
  externa cifrada, retenção e rollback.
- [ ] Criar e integrar o repositório frontend separado, executar E2E,
  WCAG 2.2 AA e métricas Web Vitals em condições de homologação.
- [ ] Conciliar divergências `develop`/`main` antes de branch de release.
- [ ] Definir ambiente AWS, região, domínio, orçamento e alarmes de custo;
  obter **autorização específica para provisionar recursos**.
- [ ] Registrar plano de go/no-go e rollback assinado. Nenhuma homologação
  concede automaticamente autorização de produção.

## E. Registro de decisão para preenchimento pelo aprovador

| Campo | Valor a registrar |
|---|---|
| Responsável técnico autorizado | PENDENTE |
| SHA/tag da especificação aprovada | PENDENTE |
| Link de aprovação do diagnóstico | PENDENTE |
| Links dos sete lotes aprovados/aplicados | PENDENTE |
| Links das HML concluídas | PENDENTE |
| Decisão AUTH-001 e data | NÃO AUTORIZADA (ver issue #80) |
| Decisão de release | PENDENTE |
| Autorização específica de gastos AWS | NÃO REGISTRADA |

**Regra de integridade:** não preencher a tabela com aprovação presumida
a partir de mensagem genérica, aprovação de PR, CI verde ou existência de
código. Preservar autoria, momento e evidência de cada decisão.
