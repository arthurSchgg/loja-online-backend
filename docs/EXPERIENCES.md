# Experiências interativas

Quizzes são configurados pelo administrador por meio de um payload de perguntas, alternativas e resultados. A loja deve aprovar os conteúdos, recomendações e regras de cada experiência.

## Modelo simples

- Experiência pode estar em rascunho ou publicada.
- Uma experiência publicada precisa de ao menos uma pergunta e um resultado.
- Cada pergunta publicada exige ao menos duas alternativas.
- Cada alternativa tem uma `resultKey` que aponta para resultado conhecido.
- Uma resposta por pergunta, sem coleta de dados pessoais.
- A apuração soma um voto para a chave da alternativa selecionada em cada pergunta.
- Em empate, a chave alfabeticamente menor vence.

**Importante:** esse algoritmo é provisório e explícito, não uma recomendação personalizada baseada em evidências.

## Endpoints

- `GET /api/v1/experiences`: listar quizzes publicados.
- `GET /api/v1/experiences/{id}`: perguntas e alternativas públicas sem revelar o mapeamento das respostas.
- `POST /api/v1/experiences/{id}/evaluate`: receber `{"answers":[{"questionId":1,"optionId":2}]}`; calcular um resultado, sem persistir respostas.
- `GET /api/v1/admin/experiences`: listar todos os quizzes.
- `POST/PUT/DELETE /api/v1/admin/experiences`: publicar, editar e excluir (requer ADMIN).

Recomendações de produto não são geradas automaticamente; esse vínculo exige decisão comercial.
