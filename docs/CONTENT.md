# Seções institucionais editáveis

O backend expõe `GET /api/v1/content-sections` para títulos, textos e imagens de seções publicadas. A administração usa `POST/PUT/DELETE /api/v1/admin/content-sections`.

Cada bloco possui `slug` estável, título, `body` de **texto puro**, URL HTTPS de imagem, posição e status de publicação.

**O backend não armazena HTML arbitrário**. O frontend deve renderizar o `body` como texto, evitando `innerHTML`.

Nenhuma seção da loja é preenchida sem conteúdo fornecido e aprovado pela proprietária; é responsabilidade do painel administrativo definir cópia, tom e imagens reais.
