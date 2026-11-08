# Auditoria administrativa persistente — implementação proposta

**Estado:** implementada em branch de revisão, ainda não integrada nem homologada. Ref: [PR #127](https://github.com/arthurSchgg/loja-online/pull/127).

## Registros capturados
- O interceptor HTTP observa escrita administrativa `POST`, `PUT`, `PATCH`, `DELETE` sob `/api/v1/admin/**`.
- Salva ator autenticado, método, rota mapeada, URI **sem query string**, status HTTP e momento UTC.
- Usa transação independente para conservar tentativas rejeitadas por regra de negócio, inclusive status 409.
- **Nunca armazena body, token, cookie, parâmetros de busca, senha ou e-mail de recuperação.**
- Falha de persistência fica no log operacional, mas não quebra a resposta original do endpoint; monitorar esse erro em operação.

## Banco
`V11__create_admin_audit_events.sql` cria `admin_audit_events` com índices por data e ator. **V10 está reservada às opções de velas do [PR #128](https://github.com/arthurSchgg/loja-online/pull/128)**. Integrar a migração V10 antes da V11 para não provocar execução Flyway fora de ordem.

## API administrativa
`GET /api/v1/admin/audit-events?page=0&size=20`

Filtros opcionais:
- `action`: `POST`, `PUT`, `PATCH`, `DELETE`;
- `actor`: nome do responsável;
- `resource`: prefixo da URI de recurso (coringas tratados literalmente);
- `from`, `to`: instante UTC ISO-8601, inclusivos.

Tamanho de página: 1 a 100; ordenação decrescente por timestamp e ID. Endpoints protegidos por sessão ADMIN e CSRF nas operações de escrita.

## Testes
`AdminAuditPersistenceIntegrationTest` cobre sucesso, conflito, filtragem, intervalo inválido, leitura segura, ausência de cópia de body, não auditoria de GET e acesso não autenticado.

## Escopo e limitações
- A auditoria HTTP **não equivale** a diff completo de antes/depois de cada campo de entidade. Se o requisito final exigir diffs, adicionar eventos de domínio em nível de Service.
- URI de rota pode conter identificador de recurso; revisar se algum futuro identificador for sensível antes de publicar.
- Não há interface de remoção de registros; retenção e acesso operacional dependem de aprovação/infraestrutura.
- Nenhuma homologação da Fase 7 foi declarada. Antes de integrar, revisar o fluxo de migrações empilhadas e registrar CI/PR approval.
