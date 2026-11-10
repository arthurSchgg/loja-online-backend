# Homologação e release — Lojinha da Kátia

## Evidências técnicas

O estado técnico foi atualizado em 2026-11-09, na `develop`
(merge [PR #152](https://github.com/arthurSchgg/loja-online/pull/152),
SHA `de93e2d14189548e57126e8a951a6d5b1086bcef`).
O [CI #42726790910](https://github.com/arthurSchgg/loja-online/actions/runs/42726790910)
teve sucesso nos jobs `verify` (Maven/JaCoCo, PostgreSQL, Mailpit,
backup/restore) e `staging-https-smoke` (Caddy TLS local, login,
CSRF e links de recuperação/confirmação).

**Limitação:** essas evidências se referem a ambiente descartável de CI.
Não representam SMTP/TLS externo, staging corporativo, frontend E2E,
aprovação da especificação ou autorização de release. O histórico anterior
V1–V6 não descreve mais todo o banco. Ver
[revisão de evidências da Fase 1](phase-1-evidence-review-2026-11-09.md).

## Critérios para liberar `main`

- [ ] CI da branch `release/*` aprovado no último commit.
- [ ] Todas as rotas administrativas autenticadas e sem escritas públicas indevidas.
- [x] Arquitetura de autenticação por sessão e CSRF aprovada e implementada no backend.
- [ ] Proteção contra força bruta, HTTPS real, origens CORS e integração com frontend testadas.
- [ ] Dados oficiais (telefone, preços, marcas, categorias e imagens) confirmados pela proprietária.
- [ ] Quizzes, recomendações, textos, serviços e campanhas aprovados pela proprietária.
- [ ] Política de privacidade, armazenamento e retenção analisados para o cenário real.
- [ ] Banco PostgreSQL persistente, segredos, HTTPS e backups com restauração testados.
- [ ] Testes negativos, de atualização, exclusão, concorrência e segurança revisados em todos os módulos.
- [ ] Revisão do contrato REST com o frontend (códigos HTTP, DTOs, paginação, imagens e WhatsApp).
- [ ] Homologação com a proprietária e revisão de acessibilidade do frontend, separadamente.
- [ ] Release PR revisado; nenhuma publicação automática sem aprovação.

## Procedimento Git Flow

1. Integrar features testadas em `develop`.
2. Abrir `release/0.1.0-rc1` a partir de `develop`.
3. Rodar CI e corrigir regressões na branch de release, retornando ajustes a `develop`.
4. Registrar pendências comerciais e técnicas.
5. **Somente após concluir o checklist**, aprovar PR `release/* -> main` e criar uma tag de versão.

A preparação de uma release candidate **não significa** disponibilidade para produção.

## Verificações específicas do frontend — 2026-11-10

- [x] Build público e administrativo separados no repositório frontend; CI da implementação validado.
- [x] Novo comando de release `API_BASE_URL=/api/v1 npm run build:release` rejeita fallback demonstrativo (PR frontend #41).
- [x] Busca/categoria, preço promocional global e contatos oficiais implementados em `develop` (backend #160; frontend #31, #33, #35, #39).
- [x] Script público read-only de contrato disponível: `npm run smoke:public -- https://staging.exemplo.com` (frontend #37).
- [ ] Executar smoke de contrato **contra staging autorizado** com certificado TLS validado e dados fictícios representativos; anexar evidências à HML-FE-001.
- [ ] Validar E2E no navegador, sessões/CSRF, redirecionamento WhatsApp, conteúdo comercial, WCAG 2.2 AA e CWV p75.
- [ ] Verificar regras reais de branch protection GitHub (consulta pelo conector retorna 403) e reconciliar divergência do histórico de `main`/ `develop` antes de montar PR de release.
- [ ] Registrar custos, conta, domínio, IAM, segredos e backups AWS aprovados antes de qualquer provisionamento.

**A marcação das entregas de código não representa homologação comercial/técnica nem autorização de produção.**
