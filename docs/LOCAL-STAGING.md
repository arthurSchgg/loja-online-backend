# Execução local e staging isolado — backend da Lojinha da Kátia

**Objetivo:** testar Spring Boot, Spring Security com sessão/CSRF, PostgreSQL e Flyway **sem criar serviços na nuvem**. Os dois ambientes usam projetos e volumes Docker diferentes; não coloque informações reais da loja no staging.

## Requisitos

- Docker Engine com Compose v2 ou Docker Desktop atualizado.
- Portas locais disponíveis: **8080** (API local), **5432** (PostgreSQL local, opcional), **8443** (staging HTTPS).
- Não precisa de Java/Maven no host para executar containers: o Dockerfile compila o JAR em estágio Maven.
- Repositório `loja-online` atualizado a partir de `develop` ou da branch de feature.

## 1. Local — HTTP limitado a 127.0.0.1

Copie `.env.example` para `.env`; substitua as duas senhas de exemplo (a senha do administrador exige >=12 caracteres). A cópia `.env` é ignorada pelo Git.

```bash
docker compose --env-file .env -f compose.yaml config
docker compose --env-file .env -f compose.yaml up --build -d
docker compose --env-file .env -f compose.yaml ps
```

A API responde em `http://localhost:8080/actuator/health` e o Swagger em `http://localhost:8080/swagger-ui.html`. Só a stack local utiliza `SPRING_PROFILES_ACTIVE=local`, que desliga `Secure` no cookie para testes exclusivamente em `localhost`. **Não exponha a porta em LAN, roteador ou internet.**

Para acompanhar o início:

```bash
docker compose --env-file .env -f compose.yaml logs -f backend
```

Para parar sem apagar dados: `docker compose --env-file .env -f compose.yaml down`. **Nunca use `down -v` se quiser preservar o volume do PostgreSQL.**

## 2. Staging — HTTPS em localhost, isolado do ambiente local

Copie `.env.staging.example` para `.env.staging`, com credenciais **diferentes** das utilizadas no local. Não coloque dados pessoais nem imagens reais nesse banco.

```bash
docker compose --env-file .env.staging -f compose.staging.yaml config
docker compose --env-file .env.staging -f compose.staging.yaml up --build -d
docker compose --env-file .env.staging -f compose.staging.yaml ps
```

Acesse `https://localhost:8443/actuator/health`. O proxy Caddy emite um certificado por uma autoridade **local** (`tls internal`). O navegador não reconhecerá a CA até que ela seja explicitamente instalada como confiável. **Não ignore avisos de certificado**; configure a confiança apenas em dispositivo de desenvolvimento, ou valide o endpoint com `curl` e o certificado local:

```bash
docker compose --env-file .env.staging -f compose.staging.yaml cp proxy:/data/caddy/pki/authorities/local/root.crt ./staging-local-ca.crt
curl --cacert ./staging-local-ca.crt https://localhost:8443/actuator/health
```

No PowerShell, use `curl.exe` em vez do alias `curl`, se necessário. O arquivo `staging-local-ca.crt` é ignorado pelo Git.

O staging expõe **apenas** `127.0.0.1:8443`, não publica PostgreSQL nem porta do backend. O cookie de sessão mantém `Secure=true`, e a API é acessada pela mesma origem. Swagger é desativado em staging.

Para parar sem perder o banco: `docker compose --env-file .env.staging -f compose.staging.yaml down`. Cuidado com `down -v`: apaga todos os volumes daquele projeto.

**Observação:** não há frontend neste repositório. O proxy atende a API e o health check; a interface HTML/JS será integrada depois, sob a mesma origem HTTPS, sem assumir que já exista uma página `/admin`.

## 3. Segurança e dados

- Nunca comite `.env`, `.env.staging`, dumps ou certificados locais.
- As credenciais não são valores reais nos arquivos `.example`.
- Não use o perfil `local` no staging: o cookie seria menos seguro.
- As operações admin exigem sessão autenticada e token CSRF; consulte `docs/SECURITY.md` para o fluxo completo.
- O administrador é único e está definido por variáveis de ambiente.
- Os scripts de backup em `scripts/` precisam de agendamento/retensão antes da produção; os volumes são independentes.
- O staging HTTPS aqui **não é uma publicação na internet** e não substitui a revisão de segurança, proteção contra força bruta ou homologação do frontend.

## 4. Testes automatizados

`mvn verify` executa os testes H2 locais. O GitHub Actions executa os testes com PostgreSQL real, Flyway e verificação de backup/restauração. Também verifica a sintaxe dos arquivos de implantação.

Quando houver Docker disponível na máquina, confira a renderização dos dois Compose:

```bash
docker compose --env-file .env -f compose.yaml config -q
docker compose --env-file .env.staging -f compose.staging.yaml config -q
```

Para alterar portas, use `LOCAL_HTTP_PORT`, `LOCAL_DB_PORT` ou `STAGING_HTTPS_PORT` nos arquivos .env correspondentes.

## 5. Preparação para hosting futuro

O Dockerfile é reutilizável para Render ou outra plataforma, mas **não cria nenhum serviço externo**. Antes do deploy público serão necessários domínio, TLS confiável, proteção anti-abuso, credenciais em gerenciador de segredos, backups externos automatizados e a integração do frontend.
