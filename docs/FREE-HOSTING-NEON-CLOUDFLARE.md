# Hospedagem gratuita escolhida: Cloudflare Pages + Render + Neon

> **Decisão de arquitetura (16/11/2026):** abandonar a opção AWS. Esta configuração prepara uma **prévia técnica**, não cria contas, infraestrutura ou deploy, e não libera produção. Não compartilhar credenciais ou imagens não autorizadas.

## Divisão de responsabilidades

- **Cloudflare Pages**: hospeda somente o frontend público estático, com `npm run build:cloudflare`, `SITE_MODE=live` e API externa HTTPS. Não hospeda o painel administrativo público.
- **Render Free**: executa o Spring Boot 3.5.7 / Java 21 via `Dockerfile` e oferece URL HTTPS `*.onrender.com`. Há `render.yaml` opcional para criar um Blueprint sem auto-deploy.
- **Neon Free**: hospeda PostgreSQL persistente, incluindo as tabelas criadas/atualizadas pelo Flyway. Usar contas e senhas fictícias na prévia.

Cloudflare Pages não se conecta diretamente ao Neon: **somente o backend** pode acessar `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. A vitrine usa a API pública em HTTPS com CORS restrito. O painel exige **mesma origem HTTPS da API** para sessão/cookie Secure + HttpOnly e CSRF; **não** publicar `dist-admin/` em Cloudflare Pages com origem separada.

## 1. Criar banco Neon Free (sem dados reais)

No console [Neon](https://console.neon.tech/), crie projeto e branch de **homologação** (região próxima ao backend, quando possível). Gere uma role exclusiva. Não copie a URL com senha para arquivos versionados ou para a conversa.

Use as credenciais do painel Neon para configurar as **variáveis secretas do serviço Render**, não variáveis públicas do Cloudflare.

Exemplo **fictício** de formato JDBC (substitua o host e o banco pelos reais; não comite a configuração):

```text
DB_URL=jdbc:postgresql://ep-exemplo.us-east-1.aws.neon.tech:5432/neondb?sslmode=require
DB_USERNAME=usuario_neon
DB_PASSWORD=<senha_apenas_no_painel_render>
```

A conexão PostgreSQL exibida pelo Neon normalmente começa com `postgresql://`; o Spring JDBC precisa de `jdbc:postgresql://` e usuário/senha separados. O modo `sslmode=require` exige TLS. Verificar suporte/validação de certificado antes de qualquer uso comercial. Na primeira integração, prefira a conexão **direta** do Neon para migrações Flyway; não assumir que PgBouncer/pooler é adequado para toda migração. Nunca persistir URL contendo senha no repositório.

## 2. Criar serviço Render Free

1. Conectar o repositório do backend no [Render](https://dashboard.render.com/) como Web Service Docker, ou usar o [render.yaml](../render.yaml) em `New > Blueprint`.
2. Escolher plano `Free`, branch `develop` **para prévia**, sem auto-deploy. Usar o Dockerfile existente e `/actuator/health` como health check.
3. Definir no painel Render as variáveis abaixo, sem escrever credenciais em `.env` versionado. O Blueprint já reserva as principais:
   - `SPRING_PROFILES_ACTIVE=prod,preview` (perfil com cookies Secure, Swagger desativado e Flyway habilitado)
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (Neon)
   - `ADMIN_USERNAME` e `ADMIN_PASSWORD` (conta inicial somente de **prévia**; senha forte e exclusiva de 12–128 caracteres)
   - `ADMIN_EMAIL` (e-mail de teste que não é conta pessoal)
   - `CORS_ALLOWED_ORIGINS=https://NOME-DO-SITE.pages.dev` (origem exata sem barra final)
4. Após o deploy, verificar `GET https://NOME-DO-BACKEND.onrender.com/actuator/health` → status `UP`, sem detalhes de infraestrutura. Verificar logs de Flyway. **Sem URL real informada, não é possível concluir esta etapa agora.**

O Dockerfile já expõe 8080 internamente; `render.yaml` define `SERVER_PORT=10000`, como configuração explícita de porta para o serviço. O Render encerra TLS na borda e repassa as requisições, então o perfil `prod` usa `server.forward-headers-strategy=framework`. Não usar `SESSION_COOKIE_SECURE=false`.

### Limites relevantes da prévia

- Render Free suspende o serviço após cerca de 15 minutos sem tráfego; o primeiro acesso pode demorar aproximadamente um minuto, e o filesystem é efêmero. Portanto, **imagens não podem ser salvas no disco local do serviço**.
- Neon Free tem limites de compute/storage, não substitui backups externos.
- Sem SMTP/transporte de e-mail validado, recuperação de senha e notificações não estão aprovadas. O Render Free bloqueia portas SMTP tradicionais para saída. Não publicar painel para uso real antes de resolver o envio de e-mails e as regras administrativas.
- Não utilizar monitoração artificial para contornar suspensão ou limites gratuitos.

## 3. Configurar Cloudflare Pages (frontend separado)

No painel [Cloudflare Workers & Pages](https://dash.cloudflare.com/), conectar o repositório `arthurSchgg/loja-online-frontend`, depois de integrar o PR de build à branch escolhida. Configurar:

| Campo | Valor |
| --- | --- |
| Framework | None |
| Production branch | `main` (apenas após aprovação da release) |
| Build command | `npm run build:cloudflare` |
| Build output directory | `dist` |
| Root directory | Raiz do repositório |
| `NODE_VERSION` | `22` |
| `API_BASE_URL` | `https://NOME-DO-BACKEND.onrender.com/api/v1` |

A URL precisa ser **HTTPS e pública**. Não colocar `DB_*`, `ADMIN_*` ou senhas na Cloudflare. O build da Pages deve validar exclusão da administração e gerar cabeçalhos estáticos de segurança.

Antes de homologar, desabilite deploy automático de produção/preview no painel e não autorize conteúdo comercial real.

## 4. Fluxo de teste

1. Testar localmente o ambiente full stack completo com [FULLSTACK-HOMOLOGATION.md](FULLSTACK-HOMOLOGATION.md) (PostgreSQL local independente e dados fictícios).
2. Depois da homologação local, criar banco Neon e serviço Render de **prévia**; verificar health, API pública e falhas seguras.
3. Montar a vitrine live na Cloudflare Pages, verificar catálogo e filtros em dados de **homologação**; erro de API não deve mostrar preços inventados.
4. Executar revisão humana do painel **na mesma origem da API**, em ambiente local integrado. O deploy remoto do painel ainda precisa de empacotamento/roteamento same-origin específico: **não está resolvido por este Blueprint**.
5. Só após aprovação formal, dados e imagens oficiais, SMTP, auditoria e backup, criar a configuração de produção.

## Critérios para NÃO publicar como loja definitiva

- [ ] Cookies administrativos e CSRF homologados em domínio HTTPS same-origin.
- [ ] Backend e Neon sobrevivem a restart e migrações com dados persistentes.
- [ ] Política de backup/restauração e proteção contra perda de dados testadas.
- [ ] Domínios, autenticação, recuperação de senha, CORS, logs e limites revisados.
- [ ] Preços, estoque, WhatsApp, imagens com direitos de uso e conteúdos revisados.
- [ ] Experiência de celular, acessibilidade e performance validadas.
- [ ] Responsável comercial aprovou explicitamente a publicação.

**Sem esses itens: NO-GO comercial.** O objetivo da hospedagem Free é a demonstração e os testes.
