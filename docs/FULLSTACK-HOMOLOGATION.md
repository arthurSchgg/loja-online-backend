# Homologação full stack local — Lojinha da Kátia

> **Estado:** ambiente pronto para ser executado pelo responsável no PC. Esta documentação não confirma aprovação comercial, teste manual nem deploy. Não utilizar dados oficiais, e-mails reais ou senhas de produção neste ambiente.

## Objetivo e topologia

Executar **frontend público em modo live + painel administrativo + API Spring Boot + PostgreSQL + Mailpit**, todos com sessão administrativa HTTPS **na mesma origem**:

- Site de teste: `https://localhost:8443/`
- Painel: `https://localhost:8443/admin.html`
- Quizzes: `https://localhost:8443/admin-experiences.html`
- API: `https://localhost:8443/api/v1/...`
- Captura de e-mails de teste (Mailpit): `http://localhost:8025`

Caddy usa certificado de CA interna, roteia `/api/*` para o backend e entrega arquivos do `dist-hml/` montado somente para leitura. O PostgreSQL não é exposto na rede; a única porta HTTPS exposta é `127.0.0.1:8443`. O Mailpit fica restrito ao PC. **Não publica AWS nem custa serviços de nuvem.**

O Compose de HML usa o nome `lojinha-homologation`, separado de `lojinha-staging` e `lojinha-local`; o volume de banco é independente. Dados ficam persistentes entre reinícios (exceto se o volume for removido explicitamente). O servidor de staging mantém `SPRING_PROFILES_ACTIVE=staging`, Secure/HttpOnly/SameSite e CSRF; não usar perfil `local` para HTTPS.

## Requisitos

- Docker Desktop + Compose v2 em execução.
- Node.js 22+ instalado no PC.
- Python 3 para o smoke automatizado (opcional para visita manual).
- Navegador atualizado e **certificado local confiável**; não ignorar alertas HTTPS.
- Os dois repositórios clonados na mesma pasta-pai, nos diretórios abaixo:

```text
projetos/
  loja-online-backend/
  loja-online-frontend/
```

Se usar outro nome ou diretório, configure `HML_FRONTEND_DIST` como caminho absoluto até a pasta `dist-hml` do frontend. Execute os comandos com as branches `develop` atualizadas, e não com código de produção divergente.

## 1. Gerar o frontend live (PowerShell, pasta frontend)

```powershell
cd .\loja-online-frontend
git checkout develop
git pull --ff-only origin develop
npm run build:hml
```

Isso cria `dist-hml/` localmente: **vitrine live (sem dados fictícios injetados pela API), painel, páginas de recuperação** e os assets necessários. A pasta é ignorada pelo Git. Não publicar `dist-hml/` como site em CDN; os builds públicos e administrativos de produção permanecem separados.

## 2. Preparar backend isolado (PowerShell, pasta backend)

```powershell
cd ..\loja-online-backend
git checkout develop
git pull --ff-only origin develop
Copy-Item .env.staging.example .env.staging
notepad .env.staging
```

Definir senhas fortes **exclusivas deste staging** para `DB_PASSWORD` e `ADMIN_PASSWORD`, e revisar o `ADMIN_USERNAME`. Mantenha `ADMIN_EMAIL=admin-staging@example.invalid` e `MAIL_FROM=no-reply@staging.invalid` se não tiver infraestrutura de e-mail real. Não reutilize contas e senhas de produção. O `.env.staging` não deve ser enviado ao GitHub ou compartilhado em screenshots.

Opcionalmente preencher `EMAIL_OUTBOX_KEY` com uma chave aleatória Base64 de 32 bytes para testar recuperação no Mailpit. O valor é secreto, nunca comitado. Não desativar o verificador de senhas comprometidas fora do CI descartável.

## 3. Iniciar PostgreSQL, API e HTTPS

```powershell
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml config --quiet
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml up --build -d
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml ps
```

O primeiro start baixa imagens, compila o backend e aplica migrações Flyway. O PostgreSQL e as tabelas são criados **automaticamente**. O proxy não deve ficar de pé se `dist-hml/` estiver ausente; isso evita exibir painel incompleto.

## 4. Confiar na CA local para o navegador

Exportar somente a CA **gerada no seu próprio Docker**, não de uma fonte externa:

```powershell
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml cp proxy:/data/caddy/pki/authorities/local/root.crt .\staging-local-ca.crt
curl.exe --cacert .\staging-local-ca.crt https://localhost:8443/actuator/health
```

Para abrir a homologação no navegador Windows, **confira a origem e a impressão digital** da CA e adicione-a temporariamente às autoridades confiáveis **do usuário atual** (não da máquina toda):

```powershell
$cert = Get-PfxCertificate .\staging-local-ca.crt
$cert.Thumbprint
certutil -user -addstore Root .\staging-local-ca.crt
```

Instale somente se tiver certeza de que a CA veio do seu contêiner de teste. Feche e reabra o navegador depois. **Ao terminar os testes**, remova essa CA da loja de confiança do usuário:

```powershell
certutil -user -delstore Root $($cert.Thumbprint)
```

Não clique para ignorar avisos de certificado e não exponha o proxy para sua rede ou internet. Não compartilhe a chave privada da CA.

## 5. Testar a API e o PostgreSQL reais

Ainda na pasta backend, com a CA já exportada:

```powershell
python .\scripts\smoke-homologation.py
```

O script usa apenas `https://localhost:8443`, verifica arquivos públicos/admin da mesma origem, configuração live, API pública, negação anônima, sessão administrativa e token CSRF, cria uma **marca fictícia** no PostgreSQL e a **desativa novamente**, confirma logout e negação após logout. Nenhuma senha/token é exibida em texto.

**O registro sintético desativado e eventual auditoria permanecem no banco de homologação**, como evidência de teste. Não execute o script em banco com dados comerciais reais. A execução do script não valida acessibilidade, comportamento em smartphone ou entregabilidade de e-mails externos.

## 6. Sua homologação manual no navegador

Use somente nomes como `HML - Perfume teste`, `HML - Categoria teste` e URLs de imagem de teste. Os registros do staging são fictícios.

- [ ] **Páginas:** home, Conheça a Loja, catálogo, detalhe, experiências, Espaço das Velas, contato, privacidade, erro/estado vazio
- [ ] **Login:** sessão real, senha inválida rejeitada, logout, sessão expirada, recuperação por Mailpit (sem e-mail real)
- [ ] **Catálogo:** marca, categoria, produto em rascunho, imagem HTTPS, publicação, busca e filtros, edição, indisponibilidade e arquivamento
- [ ] **Estoque:** entradas/saídas/ajustes, justificativa, quantidade exibida, histórico
- [ ] **Promoções:** futura, ativa, preço efetivo, encerramento com motivo, preservação do histórico
- [ ] **Quizzes:** criar perfis/perguntas, publicar, responder no site público, recomendar até três produtos elegíveis, editar sem apagar recomendações por engano
- [ ] **Serviços:** velas, aromas, cores, embalagens, oficinas, locações, publicação e consulta via WhatsApp fictício
- [ ] **Contato:** horários, endereço e região opcionais; publicação e remoção dos valores
- [ ] **Segurança:** visitantes não podem acessar API admin; CSRF em escritas, acesso negado após logout, nenhuma senha em armazenamento do navegador
- [ ] **UX:** celular/desktop, teclado, foco, `prefers-reduced-motion`, estados de erro, animações sem cortes ou rolagem horizontal
- [ ] **Persistência:** reinicie sem `-v` e confira que os registros de teste continuam no PostgreSQL

Registre evidências (sem nomes pessoais, senhas ou tokens) nas issues de homologação: backend **#112–#118**, frontend **#22** e **#60**. **Somente o responsável humano pode aceitar os resultados manuais.**

## 7. Inspecionar logs e encerrar sem apagar o banco

```powershell
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml logs --tail 100 backend proxy
docker compose --env-file .env.staging -f compose.staging.yaml -f compose.homologation.yaml down
```

Não use `down -v` para parar os contêineres, pois remove os volumes persistentes do projeto de homologação. A CA temporária instalada no Windows deve ser removida quando os testes terminarem.

## Limites desta homologação

- **CI automatizada** verifica Java/Flyway com PostgreSQL, staging HTTPS, segurança/SMTP simulado, bundles JS/Playwright; **não** executa dois checkouts privados juntos em um runner GitHub sem autorização adicional. O smoke integrado acima é executado por quem tiver os dois repositórios locais.
- A execução local não substitui testes externos de DNS, AWS, custo, RDS, backup externo, SMTP real, carga, restauração e testes WCAG/Core Web Vitals representativos.
- Sem conteúdo oficial, fotos licenciadas e aprovação comercial, a decisão de produção permanece **NO-GO**. A branch `main` e a AWS não são alteradas por esse processo.
