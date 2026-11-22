# Painel administrativo sob a mesma origem da API

O serviço Render da Lojinha usa um bundle estático do painel no mesmo host HTTPS da API. Em 21/11/2026, o deploy ativo `b6963adb192fa3d6c740d3e4748fa1250140edcd` servia o painel na branch `feature/DEPLOY-001-render-neon`, com autodeploy desativado. A leitura pública de `/admin.html` respondeu 200 após o serviço aquecer; a leitura anônima de `/api/v1/admin/products` respondeu 401. Isso comprova a entrega dos arquivos e a fronteira anônima, **não** valida login, SMTP ou o uso administrativo real.

Esta revisão leva o bundle para a branch de integração, evitando que a promoção futura de `develop` remova o painel que já existe no serviço. O repositório frontend continua sendo a fonte canônica dos arquivos; não edite duas versões manualmente.

## Como sincronizar uma nova versão

1. No frontend, finalize e teste a revisão correspondente.
2. Gere o artefato administrativo com `PUBLIC_STOREFRONT_URL=https://main.loja-online-frontend.pages.dev/ npm run build:admin` (no PowerShell, defina a variável de ambiente antes do comando). O domínio público é configuração não secreta.
3. Inclua por PR apenas os arquivos de `dist-admin/` em `src/main/resources/static/`. Não copie `dist/`, `dist-hml/`, o catálogo, arquivos de ambiente nem credenciais.
4. Atualize `docs/ADMIN-BUNDLE-MANIFEST.sha256` com os hashes SHA-256 de todos os caminhos relativos ao diretório estático; revise a diferença com a fonte frontend.
5. Execute `python3 scripts/verify_admin_bundle.py` e `mvn -B verify`, incluindo `AdminSameOriginStaticBundleTest`.
6. Registre o commit frontend usado no PR de backend. Só promova a versão depois de homologação e revisão de rollback.

O formato de snapshot evita usar um token cross-repo em cada build de produção. O manifesto verifica o conjunto exato de arquivos e os hashes, não substitui revisão de origem.

## Segurança

Os endpoints administrativos permanecem restritos a sessão HTTP autenticada com papel ADMIN. As páginas de login e recuperação e seus CSS/JS podem ser entregues sem autenticação, mas não contêm registros administrativos. O frontend público Cloudflare exclui todo o painel e não encaminha rotas de autenticação. Não desative CSRF ou autenticação para corrigir problemas de origem.

O painel envia recuperações por um transporte configurado explicitamente. SMTP é o padrão para instalações próprias e homologação; no Render Free, configure o transporte HTTPS Resend conforme [ADMIN-EMAIL-HTTPS.md](ADMIN-EMAIL-HTTPS.md). Sem remetente e credencial válidos, o recurso permanece indisponível; não desative a proteção da conta.
