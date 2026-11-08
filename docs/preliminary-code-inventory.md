# Inventário técnico preliminar do código — NÃO É O DIAGNÓSTICO FORMAL DA FASE 1

**Origem:** inspeção estática de arquivos do branch `develop` do repositório `arthurSchgg/loja-online`, em 2026-11-07.
**Limitação:** a especificação ainda não foi aprovada e nenhum teste foi executado nesta inspeção. Portanto, este inventário **não** atribui status finais `Atendido/Parcial/Pendente`. O diagnóstico formal será emitido em `docs/phase-1-diagnostic.md` **somente após** a aprovação da especificação.

## Evidências diretamente observadas
| Área | Evidência localizada | Observação, sem homologação |
|---|---|---|
| Stack | `pom.xml` | Java 21, Spring Boot 3.5.7, Spring MVC, JPA, Security, PostgreSQL, Flyway, springdoc |
| Sessões | `src/main/resources/application.yml` | Timeout configurado para 30m; cookies HttpOnly, Secure parametrizado e SameSite=Lax |
| Autenticação | `security/SecurityConfiguration.java`, `security/AuthenticationController.java` | Sessão + CSRF, login e consulta da conta; requer testes de comportamento |
| Administradora | `security/SecurityConfiguration.java` | Usuário em memória alimentado por variáveis de ambiente, não fluxo completo de recuperação/alteração de senha |
| Migrações | `src/main/resources/db/migration/V1..V6` | Existem seis migrações; sem conclusão sobre mudanças adicionais aprovadas |
| Ambiente | `compose.yaml`, `compose.staging.yaml`, `deploy/Caddyfile.staging` | Docker/PostgreSQL e HTTPS local isolado para staging; não há evidência de produção externa |
| CI | `.github/workflows/ci.yml` | Maven verify, PostgreSQL real no CI, scripts de backup/restauração no CI |
| Cobertura | `pom.xml`, `.github/workflows/ci.yml` | **Não aparece plugin/gate JaCoCo >=80%** nos arquivos inspecionados |
| Estoque | `catalog/entity/Product.java` | Produto contém `available`, mas não apresenta `stockQuantity` nem ledger; escopo atual exige controle quantitativo e histórico |
| Promoções | `docs/CAMPAIGNS.md`, entidade Product | Banners têm vigência; não confundir com promoções de preço de produto |
| WhatsApp | `docs/WHATSAPP.md` | Documenta GETs que devolvem link/mensagem; confirmação e revalidação do novo fluxo ainda não demonstradas |
| Testes | `src/test/java/br/com/lojinhadakatia/` | Há classes de testes de segurança, staging, catálogo, migrações e demais domínios; nenhuma execução feita nesta consulta |
| Backups | `scripts/backup-postgres.sh`, `scripts/verify-postgres-restore.sh`, `docs/BACKUP-RECOVERY.md` | Backup/restauração sob demanda; não comprova agendamento de produção e cópia externa |
| Frontend | README do backend | O frontend é separado; não foi inspecionado e nenhum requisito WCAG/CWV pode ser validado aqui |

## Lacunas a verificar no diagnóstico formal
1. Validar sessões, CSRF, autenticação, limitação de tentativas, recuperação/alteração de credenciais e notificações contra os critérios vigentes.
2. Confirmar segregação de ambientes e execução segura de Flyway em staging.
3. Executar testes e medir cobertura JaCoCo com pipeline aprovada.
4. Confirmar rastreabilidade, configuração de segurança e tratamento de falhas.
5. Não classificar como Atendido nenhum requisito somente porque há classe Java ou arquivo de configuração.

## Sequência
`docs/consistency-audit.md` → aprovação formal da especificação + tag → **novo diagnóstico formal** `docs/phase-1-diagnostic.md` → lotes 1–7 → AUTH-001.

## Estado
Pré-inventário documental concluído, mas diagnóstico formal **não iniciado** e implementação **não autorizada**.
