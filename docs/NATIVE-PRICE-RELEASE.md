# Revisão de integridade e release — 21/11/2026

## Estado da versão publicada constatado por leitura

- Render: serviço `lojinha-da-katia-api-rjvx`, plano Free, branch `feature/DEPLOY-001-render-neon`, autodeploy desligado.
- Deploy ativo: `b6963adb192fa3d6c740d3e4748fa1250140edcd` (17/11). A branch efetivamente implantada **já contém** `sort/maxPrice`, diferente da antiga `main`.
- A API pública e o health retornaram HTTP 200 após uma tentativa inicial que excedeu 25 segundos; Pages chegou a retornar 502 antes de se recuperar. Catálogo: 65 produtos publicados; 19 categorias públicas. A primeira página de `price-asc` tinha 12 preços técnicos zero, todos indisponíveis — confirmação do problema de modelagem #190.
- `/admin.html` retornou 200; `/api/v1/admin/products` sem autenticação retornou 401. Não foram usadas credenciais reais nem realizadas alterações no banco/site durante o diagnóstico.

O plano Free pode suspender instâncias ociosas e levar cerca de um minuto para acordar; não declarar essa latência eliminada apenas por melhorar o frontend. Referência: https://render.com/docs/free#spinning-down-on-idle.

## Alterações desta revisão

1. Preço pendente é nulo, com `priceStatus=ON_REQUEST` explícito. Produtos com valor desconhecido não geram link WhatsApp, não passam por teto de preço e ficam ao final da ordenação por preço. Preço confirmado precisa ser positivo.
2. Saldo físico, disponibilidade manual e disponibilidade efetiva deixam de ser confundidos. O admin preserva a flag manual mesmo com estoque zerado.
3. Uma consulta em lote resolve promoções da página usando o mesmo instante da ordenação; fetch em lote reduz consultas de associações. A edição de produto compartilha bloqueio de escrita com estoque/promoções; uma alteração de preço não pode invalidar uma promoção ativa ou futura.
4. Produtos relacionados são pesquisados no catálogo publicado completo. O índice V19 otimiza a relação categoria-produto.
5. Ofertas reaproveitam opções do mesmo conjunto já validado no WhatsApp; listas de ofertas carregam opções juntas. A mudança de tipo não deixa personalizações antigas em um serviço não-vela. Quizzes só substituem recomendações indisponíveis até o total configurado.
6. Recuperação/alteração de credenciais valida tamanhos e URLs; falha de uma mensagem cifrada não bloqueia toda a fila; timeouts SMTP e limites da recuperação são explícitos.
7. Bundle administrativo sincronizado em `develop`, com verificação de manifesto e fronteira de segurança; portas de execução compatíveis com o provedor, compressão de respostas, CI de Mockito/Java 21 sem attach dinâmico.

## Evidências executadas em ambiente descartável

| Verificação | Resultado |
|---|---|
| Java 21 / Maven 3.9.11 — `mvn clean verify` | **131 testes, 0 falhas, 0 erros, 0 ignorados** |
| JaCoCo | **90,73%** de linhas, 88,60% de instruções (gate ≥80% de linhas aprovado) |
| PostgreSQL real 16.16 | Migrações, paginação por categoria/preço, atualização V17→V19 e preservação dos dados fictícios aprovadas |
| SMTP local Mailpit v1.33.1 | Entrega real para conta fictícia e cifragem em repouso aprovadas |
| Pré-validação, scripts Python e bundle administrativo | 167 fontes Java, 8 testes Python, 22 arquivos estáticos com hashes verificados |

Os testes não enviaram e-mails reais nem acessaram o Neon de produção. Homologação HTTPS do proxy Docker/Caddy, restauração de backup de produção e navegação com credenciais reais permanecem gates separados.

## Plano de migração V18/V19 (issue #190)

- V18 remove NOT NULL de `products.price`, converte somente os zeros técnicos a NULL e adiciona restrição que aceita NULL ou preço positivo. Não altera estoque, publicação, nomes, contatos nem preços positivos.
- V19 adiciona índice de categoria. Migrations V1–V17 não são editadas.
- O contrato antigo não é compatível com produtos com preço nulo. Para rollback **não basta** trocar a imagem do aplicativo: antes do rollout, documente recuperação com backup conferido, restauração em banco separado e plano de reversão compatível. Não execute correções manuais de SQL na base real.
- Confirmar backup e migração em staging com cópia autorizada/anonimizada ou dados fictícios equivalentes. Validar o admin atualizado no mesmo host, DTO, ordenação e confirmação WhatsApp.
- Não alterar a branch configurada ou disparar o deploy Render antes dos gates da issue #189. A conta de e-mail e o transporte precisam ser homologados separadamente.

**A revisão finaliza o código no ramo de integração. A release de produção permanece uma etapa comercial/operacional controlada, não uma migração automática.**
