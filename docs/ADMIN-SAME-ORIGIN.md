# Painel administrativo na mesma origem do backend

**Ambiente de revisão:** `https://lojinha-da-katia-api-rjvx.onrender.com/admin.html`

## Estrutura e fonte

Os dois repositórios GitHub são **privados**. O Render acessa o repositório backend, mas não pode importar automaticamente os arquivos administrativos privados do frontend durante um build Docker sem outro token. Para evitar credenciais cross-repo no Render e não expor o painel no Cloudflare Pages, incluímos um **snapshot versionado do bundle administrativo** em `src/main/resources/static/`, copiado da branch `feature/DEPLOY-001-cloudflare-pages` do repositório `arthurSchgg/loja-online-frontend` (commit `b82d2bb54311a86773452c1c94e2ea025d85802b`).

Arquivos copiados: `admin.html`, `admin-experiences.html`, `reset-password.html`, `confirm-email.html`, toda a pasta `admin/`, CSS público compartilhado em `assets/css/{tokens,base,components}.css` e marca `assets/images/brand/logo-katia.svg`.

**Atenção à sincronização:** qualquer alteração nos arquivos acima no frontend exige novo snapshot **por PR no backend** e revisão. A cópia aqui é um artefato estático, **não** é um segundo projeto frontend independente, nem altera a API.

## Segurança

- Spring Security permite por GET apenas os caminhos explícitos dos recursos estáticos; `/api/v1/admin/**` continua com `hasRole("ADMIN")`.
- Login usa `POST /api/v1/auth/login` + CSRF sob o mesmo host HTTPS, com cookie `Secure`, `HttpOnly` e `SameSite=Lax`.
- Os arquivos HTML/CSS/JS do login são públicos (como qualquer frontend), mas não incluem dados administrativos protegidos. O backend autoriza cada leitura/gravação, não o JavaScript.
- Nenhum arquivo administrativo será copiado para `dist/` do Cloudflare Pages; o bundle `dist-admin/` continua excluído no build público.
- Nem o código nem os recursos estáticos contêm senhas ou strings do banco. Segredos ficam exclusivamente no Render.
- Não desativar CSRF, trocar CORS por `*` ou expor credenciais para corrigir sessão cross-origin.
- Recuperação de senha e troca de e-mail dependem de SMTP configurado e testado. **Ainda não divulgar o painel para uso comercial antes dessa validação.**

## Verificações antes de fechar a issue #180

1. Passar testes do backend: `mvn clean verify`, incluindo `AdminSameOriginStaticBundleTest`.
2. Confirmar build Docker e `GET /actuator/health` em Render com HTTP 200.
3. Confirmar `GET /admin.html`, `GET /admin/api.js`, `GET /reset-password.html` em HTTPS com status 200.
4. Confirmar `GET /api/v1/admin/products` sem autenticação com status 401.
5. Teste manual HTTPS com administradora: login, listagem dos oito rascunhos Skelt, alterar cadastro de teste, CSRF, logout, expiração de sessão e reautenticação.
6. Configurar SMTP, backup do Neon e validar recuperação de senha.
7. Integrar somente depois de CI confiável, revisão de código e homologação comercial.

**Nenhuma atualização comercial pode ser publicada automaticamente.** O banco Neon contém oito produtos Skelt em rascunho e indisponíveis. Os WebP são prévias de baixa resolução e ainda precisam de imagens autorizadas para uso final.

## Limitações

O link "Voltar à vitrine" no painel originalmente aponta para `index.html`, que **não** está empacotado no backend. Após configurar a URL real do Cloudflare Pages, ajustar esse link em ambos os repositórios e sincronizar o bundle.
