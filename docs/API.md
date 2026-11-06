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

## Regras

- Leitura pública retorna somente `published=true`; detalhes ocultos retornam 404.
- Deletar produto publicado exige despublicação anterior.
- Inativação de marca/categoria impede o uso em novos cadastros; produtos existentes mantêm vínculo.
- A vitrine direciona a conversas via WhatsApp: **não cria pedidos ou transações financeiras**.
- A API não presume informações de contato não aprovadas.

## Autorização

Prefixo `/api/v1/admin/**` requer papel `ADMIN` e credenciais HTTP Basic. Chamadas anônimas retornam 401.

## Evolução

Os contratos podem receber versões e módulos adicionais (experiências, velas e campanhas), sem misturar regras de comércio eletrônico tradicional.
