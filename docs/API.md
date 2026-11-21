# Contrato REST — v1

## JSON e erros

Usa `application/json`, paginação Spring Data, campos em camelCase. Erros são `application/problem+json` com status e descrição; falhas de validação incluem `errors`.

## Produto

`ProductRequest` requer:
- `name`: texto não vazio, até 180 caracteres.
- `description`: até 4000 caracteres, opcional.
- `price`: decimal **positivo**, com até duas casas. Para valor ainda não definido, envie explicitamente `priceStatus: "ON_REQUEST"` e `price: null`. Clientes antigos podem omitir o status somente ao enviar preço positivo. Zero não representa um produto gratuito, é rejeitado em novos cadastros.
- `brandId`: referência de marca ativa.
- `categoryIds`: conjunto de IDs de categorias ativas (pode ser vazio).
- `available`: disponibilidade **manual** configurada pela administradora no cadastro.
- `published`: publicação.
- `images`: rascunhos podem ter galeria vazia; **produtos publicados exigem 1 a 12 imagens**. Cada item inclui `url` HTTPS, `position` >= 0 e `altText` opcional. O backend responde `409 Conflict` se a administradora tentar criar ou publicar um produto sem imagem.

A URL de imagem é pública e não é carregada pelo servidor; não armazene dados secretos na URL.

### Resposta e disponibilidade

- `priceStatus` é `CONFIRMED` ou `ON_REQUEST`, derivado do preço base persistido.
- `price` e `effectivePrice` são nulos quando o valor está sob consulta. Não habilitam solicitação de produto.
- `available` na resposta é a disponibilidade efetiva: flag manual, estoque positivo e preço confirmado.
- `stockQuantity` na vitrine informa a quantidade disponível para consulta (zero quando bloqueada); no admin informa o saldo físico.
- `manualAvailable` é retornado **apenas no admin**, permitindo editar um produto sem confundir estoque zerado com bloqueio manual.
- As listas administrativas de marcas/categorias retornam `active`; as listas públicas continuam retornando somente as opções ativas.

## Busca, preço vigente e paginação

`GET /api/v1/products` aceita filtros opcionais `q` (nome/descrição), `brandId`, `categoryId`, `available`, `maxPrice` (decimal não negativo), `sort=recent|name|price-asc|price-desc`, `page` e `size` (1 a 50; padrão 12). O backend aplica filtros **antes de paginar**. A ordenação por preço utiliza `effectivePrice` com promoções ativas no instante da consulta, em vez do preço original; empates usam ID descendente para uma ordem estável. `recent` ordena pelo ID decrescente e `name` pelo nome, com desempate por ID. Parâmetros de sort inválidos e preço máximo negativo retornam 400.

Valores sob consulta ocupam o final das duas ordenações por preço e não entram em filtros com `maxPrice`. O filtro `available=true` exclui esses produtos mesmo que haja estoque. A resolução de promoções da página utiliza consulta em lote e o mesmo instante de referência da ordenação.

`GET /api/v1/products/{id}/related?limit=4` pesquisa o catálogo publicado inteiro por marca ou categorias em comum, priorizando a mesma marca (limite 1–8). Não inclui o próprio produto nem rascunhos.

Exemplo: `GET /api/v1/products?categoryId=2&q=vela&sort=price-asc&maxPrice=99.90&page=0&size=12`.

**Limite temporal:** promoções podem entrar/sair de vigência entre requisições de páginas diferentes, alterando a ordem; o frontend deve revalidar o preço no fluxo WhatsApp. O endpoint público não aceita campos JPA arbitrários como sort.

## Regras

- Leitura pública retorna somente `published=true`; detalhes ocultos retornam 404.
- Deletar produto publicado exige despublicação anterior.
- Edição de produto/estoque/promoção usa o mesmo bloqueio de escrita. Não é permitido reduzir o preço base abaixo de promoção vigente/programada, nem torná-lo pendente enquanto houver uma promoção incompatível.
- Inativação de marca/categoria impede o uso em novos cadastros; produtos existentes mantêm vínculo.
- A vitrine direciona a conversas via WhatsApp: **não cria pedidos ou transações financeiras**.
- A API não presume informações de contato não aprovadas.

## Autorização

Prefixo `/api/v1/admin/**` requer papel `ADMIN` e sessão HTTP autenticada. O login usa formulário `POST /api/v1/auth/login` com CSRF; veja `docs/SECURITY.md`. Chamadas anônimas retornam 401, e escritas sem CSRF válido retornam 403.

## Evolução

Os contratos podem receber versões e módulos adicionais (experiências, velas e campanhas), sem misturar regras de comércio eletrônico tradicional.
