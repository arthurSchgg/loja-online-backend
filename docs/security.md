# Segurança — implementação em develop x homologação

> **Estado: referência técnica consolidada, NÃO É APROVAÇÃO DE SEGURANÇA.**
> Snapshot: `develop` em
> `de93e2d14189548e57126e8a951a6d5b1086bcef`.
> Ver [evidências da Fase 1](phase-1-evidence-review-2026-11-09.md).

## Já implementado no backend com testes automatizados

- Administradora única com credencial persistente em PostgreSQL a partir
  de Flyway V13; sem cadastro público, JWT ou HTTP Basic.
- Spring Security usando sessão HTTP com CSRF; cookie HttpOnly/Secure,
  SameSite=Lax em staging HTTPS e timeout de 30 minutos de inatividade.
- Login/logout e versionamento das credenciais. Sessões administrativas sem
  versão válida, antigas ou incompatíveis são rejeitadas (PRs #149 e #152).
- Alteração/recuperação de senha com política de 12–128 caracteres,
  verificação local de senhas comuns e consulta de senhas comprometidas
  via k-anonimato (prefixo de SHA-1, sem enviar senha completa).
- Recuperação por link de 15 minutos de uso único; mudança de e-mail
  após confirmação do novo endereço; notificações e outbox SQL cifrado.
- Limitação de tentativas de login e avisos de atividade suspeita.
- CI Maven/JaCoCo/PostgreSQL, Mailpit local e smoke HTTPS com Caddy,
  incluindo e-mails de recuperação e confirmação capturados (PRs #143–#152).

O perfil `test` simplifica algumas verificações externas e suporta
usuários sintéticos do MockMvc. O staging HTTPS exercitado pelo CI aplica
as verificações de sessão de produção. **Não** replicar exceções do perfil
de testes nos ambientes reais.

## Riscos operacionais ainda abertos

- SMTP local Mailpit não comprova TLS/autenticação/entregabilidade com
  provedor real. Validar links, segredos, rotação de chave e falhas.
- Limites de login/deduplicação em memória e sessão local requerem
  avaliação antes de múltiplas réplicas.
- Backup/restauração sob demanda e CI não comprovam agenda, retenção,
  cópia externa cifrada ou restauração em ambiente operacional.
- Revisar política de privacidade, logs (sem segredos), auditoria,
  retenção, segurança de rede, IAM e configuração de CORS/domínio final.
- Testes E2E e acessibilidade com o frontend real estão pendentes.
- Aprovação técnica da especificação, diagnóstico, sete lotes,
  [AUTH-001](https://github.com/arthurSchgg/loja-online/issues/80) e
  homologação HML-001–007 permanecem pendentes.

**Fontes técnicas:** `docs/PERSISTENT-ADMIN-CREDENTIALS.md`,
`docs/ADMIN-EMAIL-CHANGE.md`, `docs/ADMIN-PASSWORD-POLICY.md`,
`docs/ADMIN-BREACHED-PASSWORD-CHECK.md`,
`docs/STAGING-HTTPS-AUTH-GATE.md` e
`docs/STAGING-AUTH-EMAIL-E2E.md`.

Uma execução CI verde prova os cenários automatizados daquela revisão;
**não** substitui avaliação independente, aceite comercial nem liberação
para produção.
