# Diagnóstico técnico da Fase 1 — atualização para revisão (NÃO APROVADO)

> **Referência do código auditado:** `develop` em
> `de93e2d14189548e57126e8a951a6d5b1086bcef` (merge PR #152,
> 2026-11-09). **Status: DIAGNÓSTICO TÉCNICO EM REVISÃO, NÃO FORMAL.**
> A [especificação #79](https://github.com/arthurSchgg/loja-online/issues/79)
> ainda não está formalmente aprovada nem etiquetada na versão documental.
> A [AUTH-001 #80](https://github.com/arthurSchgg/loja-online/issues/80)
> permanece NÃO AUTORIZADA; a [HML-001 #112](https://github.com/arthurSchgg/loja-online/issues/112)
> permanece NÃO HOMOLOGADA.

A revisão anterior deste documento foi produzida em 2026-11-07/08, com base
em `develop` anterior a PRs de integração e segurança. Naquele momento
reportou migrações V1–V6, autenticação em memória e ausência de outbox;
**esses itens são evidência histórica, não o estado corrente do código.**
O histórico detalhado, critérios, links, limites e bloqueios estão no
[registro de evidências de 2026-11-09](phase-1-evidence-review-2026-11-09.md).

## Metodologia e classificação

- Inspeção do `pom.xml`, aplicação Spring/segurança, workflows, Compose,
  testes de integração e documentações de migrações/sessões, comparados
  aos critérios da Fase 1 em `docs/development-phases.md`.
- Conferência dos PRs efetivamente **mesclados**: #78, #134 e #143–#152;
  evidência de testes `verify` e `staging-https-smoke` com sucesso em
  [CI #42726790910](https://github.com/arthurSchgg/loja-online/actions/runs/42726790910).
- **Implementado/CI:** o contrato técnico possui implementação e evidência
  automatizada. **Parcial:** faltam obrigações de infraestrutura, operação,
  interface ou ambiente. **Pendente:** falta prova de atendimento documental
  ou técnico suficiente. Nenhuma classificação é a aprovação da fase.
- **Não executado nesta revisão:** deploy externo, auditoria independente,
  SMTP real, acesso AWS, frontend completo ou restauro de banco autorizado.

## Matriz de diagnóstico atualizada

| ID | Critério da Fase 1 | Estado técnico provisório | Evidências principais | Lacunas de aceite |
|---|---|---|---|---|
| F1-01 | Plataforma Java/Spring/JPA | Implementado/CI | `pom.xml`, Dockerfile, workflow | Revisão final de arquitetura |
| F1-02 | PostgreSQL + Flyway | Implementado/CI | Flyway V13–V15 e testes reais PostgreSQL, PR #148 | Migração de cópia de banco real autorizada |
| F1-03 | Sessão administrativa com CSRF | Implementado/CI | `SecurityConfiguration`, PRs #143 e #150 | Integração UI e revisão de fluxos |
| F1-04 | 30 min, cookie, logout, revogação | Implementado/CI | `application*.yml`, PRs #149/#150/#152 | Testar timeout e múltiplas réplicas |
| F1-05 | Reset com token 15 min de uso único | Implementado/CI | PRs #143 e #151; controller/service | SMTP TLS externo e frontend |
| F1-06 | Outbox cifrado e avisos de segurança | Parcial | PRs #143/#147/#151; Mailpit | TLS real, falhas, idempotência e chaves |
| F1-07 | Proteção de login e senhas | Parcial | Limitador; PRs #145/#146 | Validação distribuída e serviço externo |
| F1-08 | Ambientes isolados | Parcial | Docker Compose local/staging; profiles | Produção, segredos e operação |
| F1-09 | Staging HTTPS | Parcial | Caddy local; PR #150 e smoke CI | Infra/domínio de staging aprovado |
| F1-10 | CI de Maven, PostgreSQL e backup | Implementado/CI | `.github/workflows/ci.yml`; CI #42726790910 | Gates de release/branch protection |
| F1-11 | Cobertura de linhas JaCoCo ≥80% | Implementado/CI | `pom.xml` regra mínima 0.80; `mvn verify` passa | Cobertura do candidato de release |
| F1-12 | Auditoria persistente | Parcial | PR #134, integração de auditoria e testes | Política de retenção/acesso |
| F1-13 | Backup e restauração | Parcial | Scripts e job PostgreSQL CI | Agenda, retenção, cópia externa cifrada, teste real |
| F1-14 | Swagger/DTO/HTTP errors | Parcial | `OpenApiConfiguration`, `ApiExceptionHandler` | Matriz de contrato completo com frontend |
| F1-15 | Documentos, RN, US, lotes, AUTH | Pendente | PR #78 mesclado; documentos em draft | RN duplicadas; tag; diagnóstico formal; lotes e assinatura |

## Cobertura e histórico

- A auditoria inicial de 2026-11-07 identificou 439/579 linhas (75,82%),
  posteriormente 491/579 (84,80%) no PR #111; **essas são medições antigas**.
- No código atual, a regra no `pom.xml` exige `LINE COVEREDRATIO >=0.80`
  no `mvn verify`. O CI #42726790910 passou com esse gate; obter o relatório
  do candidato final antes de registrar uma percentagem corrente.
- O CI de staging usa dados fictícios, TLS local e captura SMTP Mailpit;
  **não** é homologação humana ou de provedor SMTP externo.

## Pendências para converter o diagnóstico em documento formal

- [ ] Obter a aprovação técnica explícita e tag da especificação, com SHA.
- [ ] Conferir toda a lista de RN definitiva e as RN duplicadas
      (inclusive RN-647–650, RN-1039–1041, RN-1139–1144).
- [ ] Reconciliar as issues e a rastreabilidade RN → US → tarefas → testes.
- [ ] Revalidar cada classificação acima no commit congelado para homologação.
- [ ] Documentar testes em staging externo, SMTP/TLS, backups e
      integração frontend, com evidências sem segredos.
- [ ] Registrar responsável técnico identificado, data, decisão e justificativa.
- [ ] Revisar/aprovar/aplicar **cada** lote de padronização antes da
      autorização formal AUTH-001.

**Decisão atual:** não há evidência para declarar a Fase 1 AUTORIZADA nem
HOMOLOGADA. Nenhuma tag, merge em `main`, deploy AWS ou aprovação é
originada automaticamente por este arquivo.
