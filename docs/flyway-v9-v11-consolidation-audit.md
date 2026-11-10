# Auditoria de consolidação Flyway V9–V11 (CR-001)

> **Estado:** evidência técnica para revisão, **não** decisão formal de encerramento.
> Código de referência: `develop`, integração [PR #134](https://github.com/arthurSchgg/loja-online/pull/134),
> merge commit `830fa6cdb469e8fc337783f0f7487196d2bda40e`.
> Consultado em 2026-11-09.

## Contexto

A [CR-001 #133](https://github.com/arthurSchgg/loja-online/issues/133)
registrou alternativas concorrentes para migrações **V9** (recomendações),
**V10** (personalização de velas) e **V11** (auditoria administrativa).
Os PRs #123–#132 estão fechados e mesclados em **branches intermediárias**,
não diretamente em `develop`. A consolidação integrada foi
[PR #134](https://github.com/arthurSchgg/loja-online/pull/134), **mesclado em develop**.

## Arquivos selecionados no PR integrado

| Versão | Arquivo efetivamente integrado | Regras verificadas no SQL |
|---|---|---|
| V9 | `src/main/resources/db/migration/V9__create_experience_recommendations.sql` | `experience_recommendations` com referências a resultado/produto, posição 0–2 e unicidade de produto/posição por resultado |
| V10 | `src/main/resources/db/migration/V10__create_offering_options.sql` | `offering_options` com `kind` restrito a AROMA/COLOR/PACKAGING, posição, `active` e unicidade por oferta/tipo/rótulo |
| V11 | `src/main/resources/db/migration/V11__create_administrative_audit_events.sql` | `administrative_audit_events` com ator, método, rota, recurso opcional, status 100–599 e timestamp; índices por tempo/ator/rota |

Os arquivos acima constam no patch do PR #134 e foram conferidos em
`develop`; **não** há base para alterar suas versões aplicadas.
O CI existente aplica a cadeia versionada a um PostgreSQL limpo
(`PostgreSqlFlywayV14IntegrationTest`) e valida atualização com dados
fictícios existentes (`PostgreSqlExistingDataUpgradeIntegrationTest`).

## Rastreabilidade de branches históricas

| Tema | PRs de alternativas | Evidência da integração escolhida |
|---|---|---|
| Recomendações | #123, #124 | V9 e API de recomendações no #134; `QuizRecommendationsIntegrationTest` |
| Velas | #125, #126, #128 | V10 e `PUT /api/v1/admin/offerings/{id}/options`; `CandleOptionsIntegrationTest` |
| Auditoria | #127, #129, #130 | V11 e `GET /api/v1/admin/audit-events`; `PersistentAuditIntegrationTest` |
| Limitação de login e ofertas | #131, #132 | Componentes integrados no #134; `LoginThrottleIntegrationTest` e `OfferingAvailabilityIntegrationTest` |

**Importante:** a presença de uma migração em `develop` não comprova
que todas as decisões funcionais das alternativas foram examinadas e
aprovadas pelo responsável técnico. Evitar declarar um PR histórico
"descartado" apenas por ter sido mesclado em outra branch.

## Critérios ainda necessários para encerrar CR-001

- [x] Identificar no código atual as implementações efetivamente integradas
  para V9/V10/V11.
- [x] Vincular PR #134 às alternativas históricas e aos testes relevantes.
- [x] Confirmar que a cadeia Flyway tem testes automatizados em PostgreSQL.
- [ ] Confirmar os critérios definitivos da especificação e a regra de
  fallback de recomendações, opções de velas e auditoria.
- [ ] Validar upgrade de **banco existente autorizado**, incluindo dados
  históricos, sem alterar checksums de migrações já aplicadas.
- [ ] Revisar relações/estados das issues históricas e documentar decisões
  antes/depois dos lotes pertinentes.
- [ ] Obter a aprovação explícita do responsável técnico, com data e SHA,
  para encerrar [CR-001 #133](https://github.com/arthurSchgg/loja-online/issues/133).

**Decisão:** PENDENTE. Não alterar checksums, remover tabelas ou marcar
CR-001 como resolvida automaticamente. Nenhum deploy ou migração
em banco real foi efetuado nesta auditoria.
