# Handoff de revisão — Lojinha da Kátia

**Gerado em 2026-11-07/08.** Este documento é um **relatório do trabalho executado**, não assinatura de aprovação técnica ou comercial.

## Alterações concretas produzidas

| Entrega | Localização | Situação |
|---|---|---|
| Especificação modular, visão, casos de uso, modelo de dados, API, Design System, testes e operações | [PR #78](https://github.com/arthurSchgg/loja-online/pull/78) | Draft, sem merge |
| Regras, conflitos e rastreabilidade | `docs/business-rules.md`, `docs/consistency-audit.md`, `docs/traceability.md` | Revisão pendente de fonte integral RN |
| Diagnóstico preliminar | `docs/phase-1-diagnostic.md` | Draft, não aprovado |
| Sete relatórios de padronização | `docs/github-standardization/batch-0*.md` | Inventariados, não aplicados |
| Issue de controle | [#79](https://github.com/arthurSchgg/loja-online/issues/79) | Aberta |
| Autorização da Fase 1 | [AUTH-001 #80](https://github.com/arthurSchgg/loja-online/issues/80) | Expressamente não autorizada |
| Backlog | #81–#89 + #91–#110; duplicata #90 encerrada e ligada à #52 | Issues reais criadas e relacionadas |
| Homologações por fase | [#112](https://github.com/arthurSchgg/loja-online/issues/112)–[#118](https://github.com/arthurSchgg/loja-online/issues/118) | Não homologadas |
| Gate de cobertura e testes | [PR #111](https://github.com/arthurSchgg/loja-online/pull/111) | CI passou; pronto para revisão, sem merge |
| Frontend público protótipo | ZIP fornecido separadamente na conversa | Não está no GitHub e não foi homologado |

## Resultado de CI medido
- Antes dos testes adicionais: 439/579 linhas = **75,82%**, gate 80% falhou.
- Após testes adicionais: 491/579 linhas = **84,80%**, [run CI aprovado](https://github.com/arthurSchgg/loja-online/actions/runs/42440101382).
- Nenhuma exclusão artificial de classes foi adicionada ao JaCoCo.
- Resultado é da **branch de proposta**, não de `develop`/produção.

## Revisão indispensável (não automatizável por suposição)
- [ ] Conferir texto integral das decisões RN para reconciliar identificadores duplicados e registrar sucessoras.
- [ ] Comparar briefing original com atualizações aprovadas; validar escopo, critérios e detalhamento.
- [ ] Revisar PR #78, aprovar tecnicamente, fazer merge conforme Git Flow e registrar tag documental `spec-vX.Y.Z`.
- [ ] Homologar o diagnóstico formal da Fase 1 com código e testes da versão correta.
- [ ] Para **cada** lote: registrar valores anteriores, proposta, aprovação técnica, aplicação e verificação; nenhum dos sete está aprovado/aplicado.
- [ ] Preencher os campos do GitHub Projects v2, ver `docs/project-board-plan.md`.
- [ ] Aprovar/recusar formalmente AUTH-001 e registrar responsável, data, evidências.
- [ ] Revisar PR #111 e integrar somente dentro do fluxo autorizado; CI aprovado não substitui revisão.
- [ ] Incluir frontend em repositório independente, com segurança de mesma origem para painel; validar acessibilidade, identidade e conteúdo comercial.
- [ ] Homologar cada HML-### e autorizar release à parte; não publicar sem dados reais aprovados.

## Restrições e cautelas
- Ninguém foi nomeado artificialmente como responsável técnico/administradora.
- Não foram inventados preços, marcas ativas, número WhatsApp, endereço, imagens oficiais ou logotipo.
- Não houve merge em `develop`/`main`, alteração de produção, tag documental ou aprovação de homologação.
- O planejamento de sete fases não equivale à implementação de sete fases.

## Rastreabilidade do documento original
- O pacote inicial de documentação recuperado contém 17 RN legadas, 28 RF, 14 RNF e quatro histórias de usuário. O inventário encontra-se em [legacy-baseline-crosswalk.md](legacy-baseline-crosswalk.md).
- Os nomes `LEGACY-US001–US004` são separados das issues novas `US-001–US-004`, que possuem escopos parcialmente diferentes.
- Docs antigos `ARCHITECTURE.md`, `WHATSAPP.md`, `CAMPAIGNS.md` e `REVIEW-DECISIONS.md` receberam avisos preservando o conteúdo original, mas indicando substituições e diferenças com a especificação aprovada.

## Atualização de revisão — 2026-11-08
- PR #111 (JaCoCo e testes): **marcado pronto para revisão**, sem merge; GitHub Actions aprovado, cobertura 84,80%.
- PR #78 (especificação): **continua em rascunho**, porque conflitos de regras e aprovação técnica seguem pendentes.
- Nenhuma assinatura técnica ou comercial foi inventada; HML-001 a HML-007 e AUTH-001 permanecem não aprovadas.

## Snapshots auditáveis das issues legadas
- Foi feita consulta paginada (2 páginas) de todas as **53 issues antigas #1–#53**.
- Foram preservados títulos, estados, responsáveis, labels, milestones, descrição e timestamp de atualização em sete arquivos `docs/github-standardization/snapshot-*.md`.
- Esses snapshots permitem comparação antes/depois quando a padronização dos lotes for tecnicamente aprovada; **não substituem** a autorização nem o histórico do GitHub e não incluem campos externos do Project v2.

## Execução adicional de código e AWS — 2026-11-08

> Esta seção registra mudanças reais em **branches separadas**. Nenhuma foi integrada a `develop` ou `main`, publicada na AWS ou tecnicamente homologada.

| Área | PR | Implementação / evidências | Integração |
|---|---|---|---|
| Deploy AWS | [#119](https://github.com/arthurSchgg/loja-online/pull/119) | `application-prod.yml`, ECS Express Mode/ECR/RDS/Amplify e GitHub Actions manual para ECR; CI verificado | Não integrada |
| Estoque | [#120](https://github.com/arthurSchgg/loja-online/pull/120) | Flyway V7, ledger, API protegida, saldo/disponibilidade, testes. CI aprovado | Não integrada |
| Promoções | [#121](https://github.com/arthurSchgg/loja-online/pull/121) | Flyway V8, preço efetivo e períodos sem sobreposição, testes. CI aprovado | PR empilhado sobre #120 |
| WhatsApp | [#122](https://github.com/arthurSchgg/loja-online/pull/122) | Confirmação com revalidação de preço/estoque, CORS público restrito e testes; verificar o último CI | PR empilhado sobre #121 |

**Ordem recomendada de revisão/merge, após os gates formais:** #120 → #121 → #122. O PR #119 é independente, mas configurações de AWS não significam deploy.

**Frontend:** arquivo ZIP externo ao repositório com HTML/CSS/JS, catálogo, quizzes públicos, painel de administração inicial, integração com API, confirmação WhatsApp, `amplify.yml` e `config.js`. O repositório separado do frontend ainda precisa ser criado pelo titular da conta. O painel só deverá usar sessões quando hospedado na **mesma origem HTTPS** que a API administrativa.

**Bloqueios restantes:** auditoria final de RN e aprovação da especificação; tarefas não implementadas (recomendações de produto por perfil, personalização avançada de velas, recuperação de senha/outbox, auditoria persistente, dados reais e conteúdo oficial), testes E2E/a11y, homologações por fase, configuração de conta AWS e orçamento, provisionamento real ECS/RDS/Amplify, backups externos e aprovação comercial/técnica de produção.

**Nenhum dado comercial, credential, assinatura ou cobrança AWS foi presumido ou criado.**
