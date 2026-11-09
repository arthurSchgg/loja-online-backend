# Segurança — consolidação documental

**Referência técnica do código existente:** [SECURITY.md](SECURITY.md) e [ADR-001-ADMIN-SESSION.md](ADR-001-ADMIN-SESSION.md). Este documento apresenta o **alvo aprovado**, não certifica o estado da implementação.

- Uma única administradora. Sem registro público, HTTP Basic ou JWT.
- Spring Security com sessão HTTP, CSRF e logout por invalidação; 30 min de inatividade; cookies seguros; frontend e API administrativa na mesma origem.
- Senhas de pelo menos 12 caracteres; políticas de passphrase, alteração segura, revisão de sessões e rejeição de credenciais comprometidas no alvo aprovado.
- Limitação progressiva de tentativas de login; recuperação por link temporário único de 15 min, com verificação de e-mail e autorização de manutenção de emergência quando aplicável.
- E-mail verificado para troca de endereço; notificações de eventos significativos; fila/outbox persistente com tentativas limitadas para envio.
- Logs administrativos auditáveis (ator, data, ação, mudança), separados dos logs técnicos, sem segredos.
- Dados de segurança/administrativos não devem ser armazenados em cache público.
- HTTPS em staging e produção; credenciais e chaves fora do repositório; ambientes isolados.

## Pendências documentais conhecidas
- [ARCHITECTURE.md](ARCHITECTURE.md) é antigo e ainda fala em escolha entre Basic/JWT/sessão; não tratar essa seção como válida.
- O backend atual usa conta configurada em memória por propriedades de ambiente. Não assumir que mudança de senha, recuperação por e-mail, rate limiting e outbox já estão implementados.
- Nenhuma revisão formal de segurança nem homologação foi realizada por este documento.
