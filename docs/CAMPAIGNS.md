# Campanhas e banners

O módulo gerencia banners e chamadas de destaque no site, sem regra de desconto, cupom ou alteração automática de preços.

`GET /api/v1/campaigns` retorna somente banners publicados dentro da janela temporal configurada. `startsAt` e `endsAt` são opcionais e usam instantes UTC. Fim da campanha é exclusivo.

`POST/PUT/DELETE /api/v1/admin/campaigns` exige autorização ADMIN; uma campanha publicada precisa ser despublicada antes da exclusão.

Os links de destino devem ser HTTPS públicos; as URLs precisam de revisão editorial antes da publicação. Os títulos, imagens, datas e alegações promocionais dependem da proprietária.
