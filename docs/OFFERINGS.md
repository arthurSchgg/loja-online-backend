# Espaço das velas e serviços

O backend contém um catálogo separado para **velas artesanais**, **oficinas** e **locação**. Os tipos têm os códigos `CANDLE`, `WORKSHOP` e `RENTAL`.

A proprietária define quais ofertas existem, descrições, valores e imagens. O preço é opcional; ausência de valor significa **consultar a loja**, jamais valor zero implícito.

## Rotas

- `GET /api/v1/offerings`: ofertas publicadas.
- `GET /api/v1/offerings?type=CANDLE`: filtro opcional por tipo.
- `GET /api/v1/offerings/{id}`: detalhes publicados.
- `GET /api/v1/admin/offerings`: consulta administrativa, incluindo rascunhos.
- `POST /api/v1/admin/offerings`: cadastrar.
- `PUT /api/v1/admin/offerings/{id}`: atualizar.
- `DELETE /api/v1/admin/offerings/{id}`: excluir, somente se despublicada.

Todos os endpoints administrativos exigem papel `ADMIN`.

**Não há agenda, agendamento, carrinho, reserva automática, locação efetiva, pagamento ou formulário de dados pessoais.** O frontend pode direcionar dúvidas ao WhatsApp oficial, quando cadastrado no módulo institucional.
