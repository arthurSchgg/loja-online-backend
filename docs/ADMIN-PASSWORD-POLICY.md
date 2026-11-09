# Política administrativa de senhas — verificação local

A política centralizada em `AdminPasswordPolicy` é aplicada ao
bootstrap da primeira conta, à troca autenticada de senha e à recuperação.

- Comprimento permitido: 12 a 128 caracteres.
- Senhas comuns conhecidas presentes na lista interna são rejeitadas,
  incluindo variantes de maiúsculas e separadores.
- Sequências com apenas um caractere distinto são rejeitadas; variantes comuns de padrões repetidos são filtradas pela lista local.
- Frases-senha longas são permitidas sem obrigatoriedade arbitrária
  de símbolos ou composição mista.
- As senhas continuam protegidas com o encoder configurado no Spring
  Security e não são registradas no log.

**Limite importante:** a lista local é deliberadamente pequena e não
comprova que uma senha esteja ausente de vazamentos de credenciais.
Para atender integralmente ao requisito de rejeição de senhas
comprometidas, integrar fonte confiável de hashes comprometidos com
proteção de privacidade e comportamento definido em caso de indisponibilidade
do provedor; homologar antes de produção.

Os valores de `ADMIN_PASSWORD` em ambiente devem ser tratados como
segredos e alterados regularmente apenas se houver comprometimento ou
necessidade operacional, não por troca periódica forçada.

Nenhum dos testes deste PR substitui a homologação com banco PostgreSQL
existente, SMTP real, front-end administrativo e AUTH-001.
