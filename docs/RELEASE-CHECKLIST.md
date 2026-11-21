# Checklist de entrega técnica e liberação — Lojinha da Kátia

> **Atualização 21/11/2026:** ver [NATIVE-PRICE-RELEASE.md](NATIVE-PRICE-RELEASE.md) para a revisão de produção, as novas migrations V18/V19, os 131 testes locais aprovados e o plano de release. Este checklist preserva os gates históricos de homologação.

> **Status: NO-GO para produção** (revisão em 10/11/2026). A implementação pode estar pronta para homologação sem estar autorizada a receber visitantes reais. Nenhuma conta AWS, domínio, SMTP comercial ou aprovação da proprietária pode ser presumida.

## Fontes oficiais

- **Backend:** [arthurSchgg/loja-online-backend](https://github.com/arthurSchgg/loja-online-backend), branch de integração `develop`.
- **Frontend:** [arthurSchgg/loja-online-frontend](https://github.com/arthurSchgg/loja-online-frontend), branch de integração `develop`.
- **Release / decisões / pendências:** [RELEASE-001 #135](https://github.com/arthurSchgg/loja-online-backend/issues/135).
- **Homologação do navegador:** [HML-FE-001 #22](https://github.com/arthurSchgg/loja-online-frontend/issues/22).

## Evidência automatizada verificada

- [x] Backend no commit `6b9e56b`: [CI #42857044691](https://github.com/arthurSchgg/loja-online-backend/actions/runs/42857044691) com Maven/JaCoCo, PostgreSQL, backup e restauração, proxy TLS local, login/CSRF e e-mail em Mailpit **concluído com sucesso**.
- [x] Frontend no commit `b9b8bc1`: [CI #42860887281](https://github.com/arthurSchgg/loja-online-frontend/actions/runs/42860887281) com testes Node, builds demo/live/release/admin e inspeção de separação de arquivos **concluído com sucesso**.
- [x] Catálogo público: regras de busca, filtro, ordenação por preço promocional e exigência de imagem para publicação implementadas; rascunhos podem ser incompletos.
- [x] Separação `dist/` (vitrine pública) e `dist-admin/` (administração); release pública proíbe fallback para catálogo fictício.
- [x] Autenticação administrativa com sessão, CSRF, cookies seguros no perfil de produção; endpoints públicos não concedem permissões administrativas.
- [x] **Contrato público para integração:** [PR backend #169](https://github.com/arthurSchgg/loja-online-backend/pull/169) mesclado (`65aca5e`), CI do head [#42868708282](https://github.com/arthurSchgg/loja-online-backend/actions/runs/42868708282) aprovado. Readiness administrativa só fica UP após bootstrap; smoke público HTTPS confere paginação, preço efetivo e redaction de quizzes. A integração de navegador ainda é pendente. O frontend é privado para o CI do backend; por segurança, o backend executa um **espelho do contrato HTTP**, não faz checkout cruzado nem usa token pessoal.

Essas evidências são de ambientes isolados **sem identidade visual comercial aprovada, sem DNS final e sem dados reais**.

## Homologação obrigatória antes do GO

- [ ] [HML-001](https://github.com/arthurSchgg/loja-online-backend/issues/112): segurança e fundação, autenticação, recuperação, bloqueios, CSRF, sessão e HTTPS real.
- [ ] [HML-002](https://github.com/arthurSchgg/loja-online-backend/issues/113): catálogo/estoque (incluindo ajustes, imagens, arquivamento e filtros) com PostgreSQL persistente.
- [ ] [HML-003](https://github.com/arthurSchgg/loja-online-backend/issues/114): promoções, preços vigentes e abertura de WhatsApp com confirmação de preço/estoque.
- [ ] [HML-004](https://github.com/arthurSchgg/loja-online-backend/issues/115): quizzes, perfis, recomendações e sigilo das chaves administrativas.
- [ ] [HML-005](https://github.com/arthurSchgg/loja-online-backend/issues/116): velas, oficinas e locações, inclusive personalizações.
- [ ] [HML-006](https://github.com/arthurSchgg/loja-online-backend/issues/117): conteúdo, banners, navegação e compatibilidade frontend/API.
- [ ] [HML-007](https://github.com/arthurSchgg/loja-online-backend/issues/118): administração, auditoria, acessos, recuperação, erros e qualidade.
- [ ] [HML-FE-001](https://github.com/arthurSchgg/loja-online-frontend/issues/22): navegador real no celular/desktop, teclado, leitor de tela, `prefers-reduced-motion`, responsividade, rede lenta, diálogos, login/logout e formulários.
- [ ] Medições **em dispositivos reais ou representativos**: LCP ≤ 2,5 s, INP ≤ 200 ms e CLS ≤ 0,1 (p75); WCAG 2.2 AA. Testes Node/CI não medem isso.
- [ ] Investigar a intermitência de login 401 [#165](https://github.com/arthurSchgg/loja-online-backend/issues/165) com evidência conclusiva ou aceitação documentada de risco técnico, sem suprimir falhas de autenticação.

## Critérios operacionais e comerciais

- [ ] Aprovar conteúdo, marca, logo, fotos/licenças, preços, catálogo real, WhatsApp, e-mail, política de privacidade e horários com a responsável pela loja.
- [ ] Validar em staging representativo o banco PostgreSQL, Flyway atualizado, backups externos criptografados e **teste real de restauração**.
- [ ] Confirmar acesso à conta de hospedagem, domínio/DNS, orçamento máximo e responsáveis por cobrança/IAM; **não provisionar AWS automaticamente**.
- [ ] Gerenciar credenciais, chave de e-mail, acesso ao banco e serviços SMTP via gerenciador de segredos; nunca no repositório.
- [ ] Fornecer HTTPS válido, origem única para painel/API, cabeçalhos HTTP de produção (CSP com `frame-ancestors`, HSTS após validação, `nosniff`, `Referrer-Policy`), cache correto e proteção do painel; CSP em `meta` não substitui cabeçalhos.
- [ ] Confirmar fluxo de recuperação/notificações SMTP real sem envio repetitivo, limites e retentiva de logs.
- [ ] Documentar rollback, janela de implantação, monitoração, alertas e operador responsável.
- [ ] Registrar evidências, aprovação formal de cada HML, decisão GO/NO-GO e responsável técnico.

## Histórico de branches — reconciliação concluída

Na revisão inicial de 10/11/2026, o backend estava divergente: `develop` com 183 commits à frente e 6 atrás de `main`. **Os seis eram commits de merges históricos, com zero arquivos diferentes no sentido `develop...main`.** O [PR #171](https://github.com/arthurSchgg/loja-online-backend/pull/171) passou no CI [#42868967169](https://github.com/arthurSchgg/loja-online-backend/actions/runs/42868967169) e foi **mesclado pelo método `merge`**, preservando ancestrais sem modificar arquivos de código ou a `main`. Depois, a comparação `main...develop` indicou **194 commits à frente e zero atrás**. A publicação em `main` não ocorreu.

O frontend permaneceu com `develop` à frente e zero commits atrás de `main`. Ambos exigem release candidate, CI da revisão final, homologação e autorização de publicação.
- A configuração de branch protection/rulesets não pôde ser auditada com a permissão atual do GitHub App (HTTP 403). Um responsável com acesso administrativo deve conferir permissões e regras de aprovação antes da release.

## Git Flow seguro para a release

1. Concluir os PRs de código na `develop`, verificar **o último SHA em CI** e registrar as evidências nas issues.
2. **Concluído**: reconciliação dos seis merges históricos sem diferenças de código pelo [PR #171](https://github.com/arthurSchgg/loja-online-backend/pull/171). Conferir novamente `main...develop` na data da release; não utilizar `push --force`.
3. Criar `release/0.1.0-rc1` com escopo congelado, testar migrações/ambientes e executar homologação completa com dados fictícios.
4. Finalizar o checklist e obter aprovação técnica/comercial e orçamento da hospedagem.
5. Só então revisar PR `release/* -> main`, aprovar merge, criar tag SemVer e implantar em janela definida, com rollback e monitoração.

**Criar uma branch de release não significa aprovação para produção.** `main` e AWS devem permanecer inalteradas até a decisão registrada.
