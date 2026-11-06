# ADR-001 — Autenticação por sessão HTTP e proteção CSRF

**Status:** Aprovado pela pessoa responsável pelo projeto na conversa de revisão.

## Contexto

O painel administrativo será utilizado exclusivamente pela proprietária da Lojinha da Kátia. O backend é uma aplicação Spring MVC com interface HTML/JavaScript separada em repositório próprio, mas que pode ser servida sob a mesma origem da API.

## Decisão

Adotar `Spring Security` com `formLogin` em `POST /api/v1/auth/login`, sessão HTTP `JSESSIONID` protegida por cookie `HttpOnly`, `Secure` em HTTPS e `SameSite=Lax`, controle de CSRF via `HttpSessionCsrfTokenRepository`, recuperação de token por `GET /api/v1/auth/csrf`, consulta da conta por `GET /api/v1/auth/me` e logout por `POST /api/v1/auth/logout`.

Credenciais de administrador continuam em variáveis do ambiente e são comparadas usando BCrypt. Não haverá endpoint público de cadastro e nem emissão de JWT. O frontend administrativo deverá consumir a API sob a mesma origem. Restringir CORS a leituras públicas sem cookies.

## Consequências

**Benefícios:** revogação de sessão no logout, sem token de autenticação armazenado no JavaScript, baixo custo de manutenção para uma única administradora.

**Custos:** requer gerenciamento de sessão, proteção CSRF obrigatória em escritas, revalidação de token após login/logout, configuração HTTPS/proxy e consideração de persistência de sessão em ambientes distribuídos.

## Itens futuros separados da decisão

Limitação de tentativas, recuperação segura de acesso, provedores de hospedagem, RPO/RTO, integração do frontend e homologação comercial. Essas escolhas não foram presumidas como aprovadas.
