# Recomendações de produtos por perfil — implementação em revisão

## Escopo
- Flyway V9 cria `experience_result_recommendations` com ordem 0–2 e vínculos a perfil e produto.
- Administradora configura até 3 IDs por perfil com sessão+CSRF:
  - `GET /api/v1/admin/experiences/{id}/results/{key}/recommendations`
  - `PUT /api/v1/admin/experiences/{id}/results/{key}/recommendations` com `{"productIds":[1,2,3]}`
- Avaliação pública `POST /api/v1/experiences/{id}/evaluate` continua devolvendo perfil calculado e acrescenta `recommendations: []` com produtos completos elegíveis.
- Publicação, disponibilidade manual e saldo positivo são obrigatórios para recomendar.
- Produtos configurados mas indisponíveis não são exibidos; fallback só busca itens publicados/disponíveis de mesma marca ou categorias dos produtos configurados, sem repetições, até completar 3.
- Se não houver candidato, o resultado do quiz permanece visível sem produto sugerido.

## Compatibilidade
- Migrações V1–V8 permanecem imutáveis; nova tabela em V9.
- Contrato público só acrescenta a lista `recommendations` ao objeto `EvaluationResponse`.
- Se um resultado for removido, seus vínculos são removidos por cascade; produtos com vínculos ainda precisam de política de arquivamento/exclusão no catálogo.
- O método de fallback percorre produtos para um MVP pequeno. Antes de escalar, avaliar query específica e índices por categoria/marca.

## Homologação ainda pendente
- [ ] CI da branch aprovado.
- [ ] Revisão independente e testes com dados aprovados.
- [ ] Verificar transições de estoque e alterações de marca/categorias entre avaliação e consulta.
- [ ] Conferir autorização da Fase 4, especificação e HML-004.

**Rastreabilidade:** US-004 #84; TASK-BE-004 #98; TASK-TEST-004 #99. Branch empilhada sobre feature/whatsapp-confirmation (PR #122), que depende dos PRs #121 e #120. Não publicar nem fazer merge diretamente na develop fora da sequência aprovada.
