# Homologação automática de recuperação e troca de e-mail no staging HTTPS

Este gate complementa `scripts/smoke-staging-https.py` com um **fluxo real de
API e SMTP capturado** por `scripts/smoke-staging-security-email.py`. Os dois
scripts rodam no job `staging-https-smoke` do GitHub Actions, que sobe Caddy
com TLS local, PostgreSQL, Spring Boot e Mailpit inteiramente descartáveis.

## Critérios automatizados

1. Solicitação de recuperação para e-mail inexistente e para a administradora
   fictícia: ambas retornam `202 Accepted`, sem enviar mensagem para a
   identidade inexistente.
2. E-mail de recuperação é capturado pelo Mailpit e contém token temporário.
   Nenhum token, conteúdo da mensagem ou senha é impresso no CI.
3. Confirmação da recuperação sem CSRF não autentica a operação; com CSRF,
   retorna `204`; uma segunda utilização do token retorna `400`.
4. A senha antiga não autentica mais; a senha fictícia recém-definida permite
   novo login e acesso a `GET /api/v1/auth/me`.
5. Solicitação autenticada de mudança de e-mail com senha atual e CSRF
   mantém a sessão ativa até a confirmação e emite avisos para o endereço
   anterior e para o endereço novo.
6. A confirmação do novo e-mail, usando o token do Mailpit, retorna `204`;
   a sessão anterior passa a receber `401` e o token não pode ser reutilizado.
7. Um novo pedido de recuperação é entregue ao novo e-mail confirmado.

Todos os endereços usam `example.invalid`, e a única API do Mailpit consultada
usa loopback HTTP `127.0.0.1:18025`. O tráfego administrativo real do teste
passa exclusivamente pelo proxy HTTPS e valida a CA interna, sem desabilitar
a validação TLS.

O teste é propositalmente separado da suite unitária: depende de um container
de e-mail e do worker de outbox (intervalo padrão 30 segundos). As verificações
de senha comprometida externas ficam desligadas **somente** no ambiente
descartável de CI, para evitar dependência da rede externa.

## Limites de homologação

Este resultado não valida SMTP/TLS de provedor real, registros SPF/DKIM/DMARC,
recebimento externo, entregabilidade, AWS, UX do frontend nem os fluxos
administrativos a partir de um navegador humano. Também não aprova alterações
na especificação, AUTH-001 ou HML-001. Para publicação, registrar evidências
dessas etapas separadamente e obter a decisão do responsável técnico.

O CI deve passar nos jobs `verify` e `staging-https-smoke` no **último commit**
antes do merge. Nunca publicar credenciais do runner nem o corpo dos e-mails
capturados como artefato ou log.
