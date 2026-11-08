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
