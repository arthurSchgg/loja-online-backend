# Segurança — decisão aprovada

## Autenticação administrativa

A Lojinha da Kátia tem **uma única administradora**. Foi aprovado Spring Security com sessão HTTP, cookie `JSESSIONID` protegido e CSRF, **sem JWT, sem HTTP Basic e sem cadastro público**.

- Conta administrativa única configurada em `ADMIN_USERNAME` e `ADMIN_PASSWORD`. A senha precisa ter ao menos 12 caracteres e é comparada com hash BCrypt; ela **não fica em texto puro no banco**, nem é registrada nos logs.
- Sessões são criadas apenas quando necessárias, com alteração do ID após autenticação (proteção contra session fixation), expiração por **30 minutos de inatividade** e logout com invalidação.
- Cookie `HttpOnly=true`, `SameSite=Lax`, `Secure=true` por padrão. Para desenvolvimento **somente local HTTP**, defina `SESSION_COOKIE_SECURE=false`. Produção exige HTTPS e `Secure=true`.
- CSRF é armazenado na própria sessão, com retorno por corpo de uma chamada GET, não em cookie acessível ao JavaScript.
- Autorização exige papel ADMIN em `/api/v1/admin/**`, e qualquer rota não listada explicitamente é bloqueada.
- A avaliação pública `POST /api/v1/experiences/{id}/evaluate` é isenta de CSRF porque não autentica ninguém nem altera/persiste dados; deve ser limitada por gateway/rate limit na implantação.

## Contrato HTTP

| Método | Rota | Acesso | Descrição |
| --- | --- | --- | --- |
| GET | `/api/v1/auth/csrf` | Público | `{"headerName":"X-CSRF-TOKEN","token":"..."}` |
| POST | `/api/v1/auth/login` | Público, com CSRF | Formulário `username` e `password` (URL-encoded); 200 ou 401 |
| GET | `/api/v1/auth/me` | Logado | Confirma sessão e retorna nome da conta |
| POST | `/api/v1/auth/logout` | Sessão, com CSRF | 204 e invalidação da sessão |

**Após o login e após logout, obtenha um novo CSRF token**, pois o Spring Security limpa/rotaciona o anterior.

## Exemplo JavaScript (frontend na MESMA origem)

```javascript
async function getCsrf() {
  const response = await fetch("/api/v1/auth/csrf", { credentials: "same-origin" });
  if (!response.ok) throw new Error("Não foi possível iniciar a sessão");
  return response.json();
}

async function login(username, password) {
  const csrf = await getCsrf();
  const response = await fetch("/api/v1/auth/login", {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      [csrf.headerName]: csrf.token
    },
    body: new URLSearchParams({ username, password })
  });
  if (!response.ok) throw new Error("Credenciais inválidas");
  // Token anterior fica inválido quando a sessão muda.
  return getCsrf();
}

async function saveBrand(name, csrf) {
  return fetch("/api/v1/admin/brands", {
    method: "POST",
    credentials: "same-origin",
    headers: { "Content-Type": "application/json", [csrf.headerName]: csrf.token },
    body: JSON.stringify({ name })
  });
}

async function logout(csrf) {
  return fetch("/api/v1/auth/logout", {
    method: "POST",
    credentials: "same-origin",
    headers: { [csrf.headerName]: csrf.token }
  });
}
```

**Não armazene senhas, JSESSIONID ou token CSRF no localStorage.** Mantenha o token CSRF em memória; renove após login/logout. Um `fetch` 401 significa sessão não autenticada ou expirada. Um 403 pode indicar CSRF ausente/expirado ou permissão negada.

## Origens e implantação

**O painel e a API administrativa devem compartilhar a mesma origem**, por exemplo `https://loja.exemplo.com/admin` e `https://loja.exemplo.com/api/v1` com proxy reverso, mesmo que os projetos tenham repositórios diferentes.

O CORS configurado permite apenas requisições públicas de leitura de origens declaradas; **não permite requisições autenticadas com cookies entre origens diferentes**. Se o frontend for hospedado em domínio separado, crie um proxy de mesma origem antes de integrar o painel (não habilite `Access-Control-Allow-Credentials: true` indiscriminadamente).

Uma única instância backend pode manter a sessão em memória para o MVP. Se houver mais de uma instância ou reinicializações frequentes, considere armazenamento de sessão compartilhado e política de expiração/rotação definida.

## Pendências operacionais antes de produção

- Proteção efetiva contra força bruta (gateway/limitação de tentativas), monitoramento e alertas.
- Credenciais exclusivas com rotação; backup/restauração do banco; revisão de dependências.
- HTTPS real, cabeçalhos de proxy, hostname, restrição de CORS e backup da infraestrutura.
- Testes de segurança e integração com o frontend administrativo real.
- Não tratar o CI verde como auditoria profissional de segurança.

Referências:
- https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
- https://docs.spring.io/spring-boot/3.5/reference/web/servlet.html
