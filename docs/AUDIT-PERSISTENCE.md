# Auditoria administrativa persistente — PR para revisão

A migration V11 introduz `administrative_audit_events` e índices de data, ator e rota.
O interceptor grava operações de escrita que chegam ao MVC em uma transação independente:
ator da sessão, método HTTP, **padrão da rota** em vez de URL livre, identificador numérico quando presente,
status de resposta e instante. Não grava corpo, senha, cookie, token ou parâmetros de consulta.

Consulta por conta administrativa: `GET /api/v1/admin/audit-events`, com filtros exatos
`actor`, `route`, `from` (inclusive) e `to` (exclusive); ordenação decrescente e páginas
de até 20 registros. Endpoint autenticado pelo `/api/v1/admin/**` existente.

## Limites e riscos
- A gravação após a resposta não é atômica com a mutação. Falha de banco é registrada como erro operacional, mas não
  transforma a resposta já enviada em falha. Implementação de outbox/evento transacional seria necessária para garantia rígida.
- Respostas bloqueadas pelo Spring Security antes do MVC não são coletadas por este interceptor;
  logs de segurança do filtro devem ser analisados separadamente.
- Ausência de remoção automática segue decisão de retenção de auditoria; política de acesso/arquivamento precisa ser homologada.
- Não representa autenticação completa, notificações de segurança ou auditoria de diferenças de valores (before/after).
- Revisar limites de armazenamento PostgreSQL e conteúdo dos logs antes de produção.

Esta feature não foi integrada à develop, homologada nem publicada.
