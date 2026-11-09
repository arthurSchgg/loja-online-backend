> **DISTINÇÃO DE ESCOPO:** este documento descreve campanhas de **banners** do código existente. Promoções de preço programadas são uma regra diferente, planejada na [US-002](https://github.com/arthurSchgg/loja-online/issues/82). Não inferir um mecanismo de desconto a partir do módulo de campanhas. Confira a decisão vigente em [business-rules.md](business-rules.md).

# Campanhas e banners

O módulo gerencia banners e chamadas de destaque no site, sem regra de desconto, cupom ou alteração automática de preços.

`GET /api/v1/campaigns` retorna somente banners publicados dentro da janela temporal configurada. `startsAt` e `endsAt` são opcionais e usam instantes UTC. Fim da campanha é exclusivo.

`POST/PUT/DELETE /api/v1/admin/campaigns` exige autorização ADMIN; uma campanha publicada precisa ser despublicada antes da exclusão.

Os links de destino devem ser HTTPS públicos; as URLs precisam de revisão editorial antes da publicação. Os títulos, imagens, datas e alegações promocionais dependem da proprietária.
