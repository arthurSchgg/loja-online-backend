# Contrato REST — v1

## JSON e erros

Usa `application/json`, paginação Spring Data, campos em camelCase. Erros são `application/problem+json` com status e descrição; falhas de validação incluem `errors`.

## Produto

`ProductRequest` requer:
- `name`: texto não vazio, até 180 caracteres.
- `description`: até 4000 caracteres, opcional.
- `price`: decimal >= 0, com até duas casas.
- `brandId`: referência de marca ativa.
- `categoryIds`: conjunto de IDs de categorias ativas (pode ser vazio).
- `available`: disponibilidade visual.
- `published`: publicação.
- `images`: até 12 itens, cada um com `url` HTTPS, `position` >= 0 e `altText` opcional.

A URL de imagem é pública e não é carregada pelo servidor; não armazene dados secretos na URL.

## Busca, preço vigente e paginação

`GET /api/v1/products` aceita filtros opcionais `q` (nome/descrição), `brandId`, `categoryId`, `available`, `maxPrice` (decimal não negativo), `sort=recent|name|price-asc|price-desc`, `page` e `size` (1 a 50; padrão 12). O backend aplica filtros **antes de paginar**. A ordenação por preço utiliza `effectivePrice` com promoções ativas no instante da consulta, em vez do preço original; empates usam ID descendente para uma ordem estável. `recent` ordena pelo ID decrescente e `name` pelo nome, com desempate por ID. Parâmetros de sort inválidos e preço máximo negativo retornam 400.

Exemplo: `GET /api/v1/products?categoryId=2&q=vela&sort=price-asc&maxPrice=99.90&page=0&size=12`.

**Limite temporal:** promoções podem entrar/sair de vigência entre requisições de páginas diferentes, alterando a ordem; o frontend deve revalidar o preço no fluxo WhatsApp. O endpoint público não aceita campos JPA arbitrários como sort.

## Regras

- Leitura pública retorna somente `published=true`; detalhes ocultos retornam 404.
- Deletar produto publicado exige despublicação anterior.
- Inativação de marca/categoria impede o uso em novos cadastros; produtos existentes mantêm vínculo.
- A vitrine direciona a conversas via WhatsApp: **não cria pedidos ou transações financeiras**.
- A API não presume informações de contato não aprovadas.

## Autorização

Prefixo `/api/v1/admin/**` requer papel `ADMIN` e sessão HTTP autenticada. O login usa formulário `POST /api/v1/auth/login` com CSRF; veja `docs/SECURITY.md`. Chamadas anônimas retornam 401, e escritas sem CSRF válido retornam 403.

## Evolução

Os contratos podem receber versões e módulos adicionais (experiências, velas e campanhas), sem misturar regras de comércio eletrônico tradicional.
