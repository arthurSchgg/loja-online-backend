> **DOCUMENTO LEGADO — NÃO NORMATIVO PARA O ESCOPO ATUAL.** A seção abaixo sobre Basic Auth/JWT como escolha pendente foi substituída pela decisão posterior de **sessão HTTP com CSRF**. Consulte [architecture.md](architecture.md), [security.md](security.md) e [ADR-001-ADMIN-SESSION.md](ADR-001-ADMIN-SESSION.md). O texto histórico foi preservado para rastreabilidade.

# Arquitetura

Monólito modular por domínio, Spring MVC, Spring Data JPA e PostgreSQL.

- `catalog`: produtos, marcas, categorias e imagens.
- `store`: dados institucionais e WhatsApp.
- `security`: proteção de operações administrativas.
- `shared`: configuração e erros HTTP.

## Convenções

Entidades JPA não são expostas diretamente. DTOs validam limites de entrada. Services concentram transações e regras de negócio; Controllers orquestram HTTP. Banco gerenciado via Flyway. Todas as rotas de escrita ficam sob `/api/v1/admin` e exigem ADMIN.

## Decisões ainda pendentes

Logotipo, dados comerciais, canais oficiais, estratégia de hospedagem de fotos e autenticação definitiva (JWT ou sessão) precisam ser aprovados. A proteção provisória com Basic Auth exige HTTPS em ambientes publicados. Nenhuma credencial padrão é disponibilizada.
