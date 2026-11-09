# Diagnóstico da Fase 1 — RASCUNHO PRELIMINAR (não aprovado)

> **Status:** EM PREPARAÇÃO, NÃO FORMAL. A versão aprovada da especificação e a tag documental ainda não existem. Este registro **não é** a autorização para implementar a Fase 1; sua validade deverá ser reavaliada após a aprovação técnica da especificação. O commit de referência para inspeção inicial é o HEAD anterior de `develop` (`a0959eb5488ab7e9023d66d4b3938c621fd5b7e7`) e arquivos consultados em 2026-11-07/08.

## Metodologia
- Inspeção de `pom.xml`, `.github/workflows/ci.yml`, `application.yml`, classes de `security`, modelos, `compose*.yaml`, migrações Flyway V1–V6 e relatórios do CI.
- Referências técnicas contrastadas com `docs/business-rules.md`, `docs/security.md`, `docs/operations.md`, `docs/requirements.md` (todos provisórios).
- Classificações **provisórias**: `Atendido` só por evidência direta suficiente do requisito delimitado; `Parcial` quando solução existe, mas faltam comportamentos ou validação; `Pendente` quando não há evidência de implementação suficiente.
- **Limites:** nenhuma revisão independente de segurança, nenhuma homologação em staging externa e nenhum teste de frontend foram realizados nesta auditoria.

## Matriz preliminar

| ID preliminar | Requisito da Fase 1 | Evidência observada | Estado indicativo | Lacuna / ação |
|---|---|---|---|---|
| F1-01 | Java 21, Spring Boot, Web MVC, JPA, Maven | `pom.xml` | Atendido (configuração) | Revalidar no commit de especificação aprovado |
| F1-02 | PostgreSQL + Flyway migrações versionadas | `pom.xml`, `application.yml`, migrações V1–V6, CI PostgreSQL | Parcial | Revisar histórico de migrações e critérios por ambiente |
| F1-03 | Uma administradora e sessão HTTP com CSRF | `security/SecurityConfiguration.java`, `AuthenticationController.java`, testes de autenticação | Parcial | Validar todos os fluxos e recuperação/atualização de conta |
| F1-04 | Sessão expira após 30 min; cookie seguro | `application.yml` timeout 30m e cookie Secure/HttpOnly/Lax | Parcial | Verificar runtime de staging/produção, proxy e logout |
| F1-05 | Recuperação por link de uso único, token 15 min | Conta `InMemoryUserDetailsManager`; nenhum fluxo recuperado nos arquivos vistoriados | Pendente | [US-008 #88](https://github.com/arthurSchgg/loja-online/issues/88) |
| F1-06 | E-mail/outbox persistente para segurança | Não identificado no inventário de classes Java/Flyway | Pendente | [TASK-BE-007 #107](https://github.com/arthurSchgg/loja-online/issues/107) |
| F1-07 | Proteção contra abuso de autenticação | `docs/SECURITY.md` declara limites pendentes | Pendente | Reutilizar [#51](https://github.com/arthurSchgg/loja-online/issues/51); avaliar controles atuais |
| F1-08 | DEV/STAGING/PROD isolados | Docker Compose local e staging isolado, volumes distintos | Parcial | Definir/homologar PROD, segredos, TLS, origens |
| F1-09 | HTTPS em staging | `compose.staging.yaml` e Caddy local no loopback | Parcial | Validar ambiente real de homologação |
| F1-10 | Pipeline CI com testes Maven e PostgreSQL | `.github/workflows/ci.yml`, histórico do GitHub Actions | Parcial | Completar gate de cobertura e verificar evidências completas |
| F1-11 | Cobertura de linhas JaCoCo >=80% | [PR #111](https://github.com/arthurSchgg/loja-online/pull/111) e [CI aprovado](https://github.com/arthurSchgg/loja-online/actions/runs/42440101382): 491/579 linhas = 84,80% | Parcial | Gate e testes em PR draft, ainda não integrados em develop nem homologados |
| F1-12 | Auditoria administrativa e logs | `AdminAuditInterceptor`, `AdminAuditConfiguration` | Parcial | Persistência, ator/recurso, retenção e proteção do dado |
| F1-13 | Backup e restauração | `scripts/backup-postgres.sh`, `scripts/verify-postgres-restore.sh` | Parcial | Agendamento, retenção, cifragem externa e exercício periódico, [#52](https://github.com/arthurSchgg/loja-online/issues/52) |
| F1-14 | OpenAPI e erros HTTP | `pom.xml`, `OpenApiConfiguration`, `ApiExceptionHandler` | Parcial | Verificar documentação/erros negativos de todos os contratos |
| F1-15 | Documentação e rastreabilidade da fase | [PR #78](https://github.com/arthurSchgg/loja-online/pull/78), `docs/traceability.md` | Parcial | IDs RN únicos, aprovação técnica, rastreabilidade definitiva |

## Evidência JaCoCo reproduzível
- 62 classes analisadas na primeira execução do PR #111.
- `LINE covered=439; missed=140; total=579; ratio=75,82%`.
- Principal cobertura faltante no relatório inicial: `CatalogService` (34 linhas), `OfferingService` (14), `ContentSectionService` (14), `CampaignService` (14) e `AdminCatalogController` (12).
- **Nova execução validada no PR #111:** 491 linhas cobertas / 579 totais = **84,80%**; check de 80% e pipeline aprovados no commit `a85f726a58528732ce66843a0fce2e22834ce542`.
- O CI do branch de proposta passou, mas o gate **não foi integrado a develop**; não equiparar resultado no PR com homologação ou entrega definitiva.

## Condições de aprovação deste diagnóstico
- [ ] Aprovação técnica da especificação, tag documental e commit exato de referência.
- [ ] Auditoria de toda a Fase 1 contra os RN definitivos (não apenas itens acima).
- [ ] Evidências por requisito (código, testes/CI, execução em staging).
- [ ] Atualizar classificações e lacunas após testes reais.
- [ ] Conciliar issues antigas e novas; não duplicar trabalho.
- [ ] Aprovação técnica formal do diagnóstico antes da padronização dos sete lotes.

**Decisão de início:** bloqueada em [AUTH-001](https://github.com/arthurSchgg/loja-online/issues/80). Este documento **não** a substitui.
