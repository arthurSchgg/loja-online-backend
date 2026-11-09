> **CONTRATO IMPLEMENTADO NO BACKEND ATUAL, NÃO A REGRA-ALVO COMPLETA.** O fluxo aprovado posteriormente exige confirmação obrigatória, revalidação de preço/estoque/disponibilidade e mensagens estruturadas pelo servidor; ver [business-rules.md](business-rules.md), [api-contract-proposal.md](api-contract-proposal.md) e [US-003](https://github.com/arthurSchgg/loja-online/issues/83). O GET abaixo é histórico e não comprova cumprimento desses requisitos.

# Integração de atendimento via WhatsApp

O sistema não cria vendas nem processa pagamentos. Ele pode preparar links de conversas com o WhatsApp oficial que foi informado pela loja.

## Rotas públicas

- `GET /api/v1/products/{id}/whatsapp`: retorna mensagem sobre o produto publicado.
- `GET /api/v1/offerings/{id}/whatsapp`: retorna mensagem sobre a oferta publicada.

As respostas possuem `url` (formato `https://wa.me/<telefone>?text=<mensagem codificada>`) e `message`.

**Condições:** o produto ou serviço precisa estar publicado, e `whatsapp` precisa ter sido cadastrado no endpoint administrativo de contato. Sem contato, a API retorna conflito (409), explicitando configuração ausente. Objetos ocultos ou inexistentes retornam 404.

A API apenas fornece o link; o navegador/cliente decide quando abri-lo. O backend não armazena mensagens e não confirma se alguma conversa ou venda ocorreu.
