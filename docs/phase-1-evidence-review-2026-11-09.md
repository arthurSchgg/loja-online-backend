# Revisão de evidências da Fase 1 — 2026-11-09

> **Status: revisão técnica documental para conferência — NÃO É HOMOLOGAÇÃO.**
> Base inspecionada: `develop` no merge commit
> `de93e2d14189548e57126e8a951a6d5b1086bcef` (PR #152).
> A base deve ser revalidada se `develop` mudar.
>
> Decisões formais ainda registradas: [AUTH-001 #80](https://github.com/arthurSchgg/loja-online/issues/80)
> **NÃO AUTORIZADA**; [HML-001 #112](https://github.com/arthurSchgg/loja-online/issues/112)
> **NÃO HOMOLOGADA**; [especificação #79](https://github.com/arthurSchgg/loja-online/issues/79)
> ainda sem aprovação final documentada. Este relatório **não** altera essas decisões.

## 1. Escopo da verificação

Inspeção estática do código/documentos do GitHub e evidências de GitHub Actions,
sem execução em infraestrutura real de homologação/produção. Os termos usados:

- **Implementado com CI:** implementação observada em `develop` e testes
  automatizados com execução aprovada; **não** equivale a aceitação comercial.
- **Parcial/operacional pendente:** há componentes testados localmente, mas
  faltam validações em staging externo, integrações reais ou aprovação.
- **Documental pendente:** a implementação pode existir, mas requisitos/IDs,
  rastreabilidade e aprovação técnica não estão fechados.

## 2. Matriz Fase 1 — estado do código e evidência, não aprovação

| ID | Requisito | Evidência atual | Estado técnico | Pendente para HML-001 |
|---|---|---|---|---|
| F1-01 | Java 21 / Spring Boot / MVC / JPA / Maven | `pom.xml`, Dockerfile e CI Maven | Implementado com CI | revisão de especificação e arquitetura |
| F1-02 | PostgreSQL e Flyway | `application.yml`, migrações incluindo V13–V15; testes `PostgreSqlFlywayV14IntegrationTest` e `PostgreSqlExistingDataUpgradeIntegrationTest` | Implementado com CI | backup e migração de cópia de banco autorizado em staging |
| F1-03 | Conta administrativa persistente e sessão HTTP com CSRF | `AdminCredentialService`, `SecurityConfiguration`, `AuthenticationController`; PRs #143 e #150 | Implementado com CI | navegador/frontend real e revisão de autorização |
| F1-04 | 30 min de inatividade, cookie seguro, logout e revogação de sessões | `application.yml`, `application-staging.yml`, `AdminSessionVersionFilter`; PRs #149, #150, #152 | Implementado com CI | teste operacional de timeout, múltiplas réplicas e armazenamento de sessões |
| F1-05 | Recuperação de senha, link de 15 min e uso único | `AdminPasswordRecoveryService`, `AdminPasswordRecoveryController`, testes E2E HTTPS/Mailpit do PR #151 | Implementado com CI | SMTP/TLS real e interface do frontend |
| F1-06 | Notificações administrativas e outbox criptografado | `AdminEmailOutboxService`, testes SMTP Mailpit do PR #147 e E2E #151 | Parcial/operacional pendente | provedor real, idempotência e gestão/rotação de chave |
| F1-07 | Proteção de autenticação e senha | `LoginAttemptLimiter`, `BreachedPasswordVerifier`, `AdminPasswordPolicy`; PRs #145, #146 | Parcial/operacional pendente | escalonamento distribuído, verificar API de senhas vazadas no staging real |
| F1-08 | DEV/STAGING/PROD isolados | `compose.yaml`, `compose.staging.yaml`, `application-staging.yml` | Parcial/operacional pendente | aprovar e implementar ambiente real PROD, IAM, segredos e rede |
| F1-09 | HTTPS em staging | Caddy `tls internal`, smoke HTTPS `scripts/smoke-staging-https.py` do PR #150 | Implementado em CI local | certificado/domínio real e staging externo |
| F1-10 | CI de build, testes e restauração | `.github/workflows/ci.yml`; jobs `verify` e `staging-https-smoke` | Implementado com CI | requisitos de branch protection, aprovação de PR e release |
| F1-11 | JaCoCo ≥80% de linhas | `pom.xml`, regra `COVEREDRATIO minimum 0.80`; job `mvn verify` aprovado | Gate implementado com CI | relatório de cobertura do último candidato de release e revisão de escopo |
| F1-12 | Auditoria administrativa | Código de auditoria persistente e migrações de auditoria; testes como `PersistentAuditIntegrationTest` | Implementado com CI | retenção, controles de acesso e revisão de registros reais |
| F1-13 | Backup e restauração | `scripts/backup-postgres.sh`, `scripts/verify-postgres-restore.sh`, job de restauração CI | Parcial/operacional pendente | rotina diária/semanal, cópia externa cifrada, retenção e restore periódico |
| F1-14 | Contrato REST, OpenAPI e erros | `OpenApiConfiguration`, `ApiExceptionHandler`, documentação e testes HTTP | Parcial/documental pendente | revisar contratos negativos e integrações frontend |
| F1-15 | Especificação, rastreabilidade, sete lotes e autorização | PR #78 **mesclado** (histórico), `docs/business-rules.md`, `docs/traceability.md`, `docs/github-standardization/` | Documental pendente | reconciliar IDs duplicados, aprovar especificação/tag, diagnóstico e lotes; decisão #80 |

**Atenção ao vocabulário:** "Implementado com CI" não significa "aprovado",
"Ready", "Done", "homologado" ou "pronto para produção".

## 3. Cadeia de evidências verificáveis

| Natureza | Referência | O que comprova | O que NÃO comprova |
|---|---|---|---|
| Consolidação inicial | [PR #78](https://github.com/arthurSchgg/loja-online/pull/78) — mesclado | existência da base documental | revisão integral das regras e aprovação formal |
| Integração de código | [PR #134](https://github.com/arthurSchgg/loja-online/pull/134) — mesclado | escolha integrada de módulos, testes e migrações | decisões de negócio e testes humanos |
| Segurança da administradora | PRs [#143](https://github.com/arthurSchgg/loja-online/pull/143) a [#146](https://github.com/arthurSchgg/loja-online/pull/146) | credenciais persistentes, e-mails, política e senhas comprometidas | entrega SMTP real e operação distribuída |
| SMTP local e migrations | [PR #147](https://github.com/arthurSchgg/loja-online/pull/147), [#148](https://github.com/arthurSchgg/loja-online/pull/148) | envio capturado e upgrade PostgreSQL com dados fictícios | entrega externa e upgrade de banco real |
| Sessão e HTTPS | [PR #149](https://github.com/arthurSchgg/loja-online/pull/149), [#150](https://github.com/arthurSchgg/loja-online/pull/150) | revogação e autenticação atrás de Caddy TLS local | segurança de navegador frontend real, produção |
| Recuperação/e-mail E2E | [PR #151](https://github.com/arthurSchgg/loja-online/pull/151) | tokens de uso único, recuperação e confirmação via Mailpit | provedor SMTP externo |
| Sessão legada | [PR #152](https://github.com/arthurSchgg/loja-online/pull/152) | rejeição de sessão administrativa sem versão válida no runtime real | homologação operacional e aprovação |

Última execução observada no desenvolvimento desta revisão:
[GitHub Actions #42726790910](https://github.com/arthurSchgg/loja-online/actions/runs/42726790910)
— **success** nos jobs `verify` e `staging-https-smoke` para o último commit
do PR #152. Um PR documental posterior precisa de seu próprio CI aprovado.
O teste técnico não substitui a inspeção humana de cada critério.

## 4. Bloqueios formais e encaminhamento

1. **Especificação:** [#79](https://github.com/arthurSchgg/loja-online/issues/79)
   requer reconciliação de RN duplicados, texto integral das fontes e decisão
   expressa do responsável técnico. Um PR mesclado **não** é essa decisão.
2. **Tag documental:** criar `spec-vX.Y.Z` **somente após** aprovação da
   especificação e registro do commit exato.
3. **Diagnóstico:** usar este relatório como revisão técnica posterior, sem
   sobrescrever o histórico inicial em `docs/phase-1-diagnostic.md`.
   Formalizar versão final somente após tag e revisão dos critérios definitivos.
4. **Sete lotes:** em `docs/github-standardization/batch-0*.md` continuam
   inventariados e não aprovados/aplicados. Exigir comparação antes/depois de
   cada issue, responsável técnico e checagem dos campos de Projects.
5. **AUTH-001:** registrar decisão técnica em [#80](https://github.com/arthurSchgg/loja-online/issues/80)
   após cumprimento das pré-condições; não alterar estado automaticamente.
6. **Homologações HML-001–HML-007:** executar cenários reais, registrar
   testes/defeitos/evidências e assinatura por fase, inclusive frontend.
7. **Produção:** reconciliar divergência `main`/`develop`, confirmar
   orçamento/conta/domínio/segredos e autorizar explicitamente AWS e release;
   nenhuma execução deste relatório realiza isso.

## 5. Riscos que CI verde não elimina

- SMTP Mailpit não valida autenticação, TLS do provedor, entregabilidade,
  SPF/DKIM/DMARC, nem consumo de tokens na interface do frontend.
- Outbox oferece envio *at least once*; crash depois da aceitação SMTP,
  antes da persistência do envio, pode duplicar notificações.
- Rate limiting e deduplicação de avisos em memória precisam de estratégia
  entre réplicas antes de escalar a aplicação horizontalmente.
- Sessão e cookie seguro devem ser testados no domínio/origem finais.
- Restore CI com banco descartável não prova retenção, agenda, cifragem
  externa nem recuperação operacional de um banco existente.
- Os documentos legados em maiúsculas (`docs/ARCHITECTURE.md`,
  `docs/SECURITY.md`, etc.) precisam de revisão de divergências com o
  contrato técnico real e decisões posteriores.

**Responsável técnico:** pendente de identificação/aceite formal.
**Decisão de homologação:** não registrada; manter NÃO HOMOLOGADA.
**Decisão de produção:** não autorizada por este documento.
