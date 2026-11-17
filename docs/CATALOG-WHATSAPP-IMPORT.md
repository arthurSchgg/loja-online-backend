# Importação de catálogo comercial para a Lojinha da Kátia

**Status:** importador técnico preparado. **Nenhum produto foi extraído ou importado do WhatsApp até receber uma lista/fotos verificáveis da proprietária.** O link `wa.me/c/...` abre um catálogo interativo, não fornece um arquivo de produtos e fotos para esta ferramenta. Não inventar nomes, preços ou imagens.

## Dados necessários

Envie capturas ou uma exportação autorizada com **nome, preço, marca, categoria, descrição (quando houver), disponibilidade** e arquivos originais das imagens. Imagens privadas do WhatsApp não são URLs permanentes; **não colocar URLs temporárias do WhatsApp no banco**. A publicação exigirá imagens hospedadas em HTTPS estável e com direito de uso.

O importador cria **rascunhos não publicados por padrão**, com URLs HTTPS ou arquivos WebP locais estritamente validados, e **não altera estoque**. A opção explícita `--preview-local` permite mostrar os registros novos no navegador **apenas na API localhost:8080**, sempre como indisponíveis.

### Manifesto JSON

Copie `docs/catalog-import-template.json` para uma pasta não versionada (ex.: `catalogo-local.json`) e preencha:

```json
{
  "products": [
    {
      "name": "NOME-EXATO-DO-PRODUTO",
      "brand": "MARCA-EXATA",
      "categories": ["CATEGORIA-APROVADA"],
      "description": "DESCRIÇÃO-APROVADA",
      "price": "R$ 89,90",
      "available": false,
      "images": [
        {"url": "https://seu-dominio-de-imagens.example/foto.webp", "altText": "Foto principal do produto"}
      ]
    }
  ]
}
```

O exemplo acima é apenas esquema: não usar a URL `.example` para um produto real. Até 12 imagens por produto; a primeira fica na posição 0. O script rejeita HTTP, URLs privadas e produtos duplicados no mesmo arquivo. Também aceita **somente** imagens relativas no formato `assets/images/catalogo/<marca>/<arquivo>.webp`, desde que os arquivos estejam realmente presentes no frontend servido. Os dados podem ser importados antes de decidir domínio, mas as imagens precisam de URLs HTTPS duráveis, ou deve-se aguardar sua hospedagem.

## Conferir sem modificar o banco

No diretório do backend, com Python 3.10+ instalado:

```powershell
python .\scripts\import_catalog.py .\catalogo-local.json
```

O comando padrão **não acessa a API e não modifica o banco**; apenas valida o arquivo e informa quantos itens estão prontos.

## Aplicar ao PostgreSQL local por meio da API Spring Boot

1. Inicie **somente o PostgreSQL local** com `docker compose --env-file .env -f compose.yaml up -d postgres`.
2. Execute `LojaOnlineApplication` pelo IntelliJ com perfil `local`, banco `lojinha_da_katia` e porta HTTP 8080.
3. Verifique `http://localhost:8080/actuator/health`.
4. Faça backup do banco local se já houver registros importantes.
5. Após revisar e aprovar todas as fichas do manifesto:

```powershell
python .\scripts\import_catalog.py .\catalogo-local.json --apply
```

A ferramenta solicitará **nome e senha da conta administrativa local**, sem mostrar a senha; autentica via sessão/CSRF, busca marcas/categorias existentes, cria as que faltam e cadastra os produtos como `published=false`. Por padrão, **produtos existentes com mesma marca + nome são ignorados**; repetir a importação não cria duplicatas desses produtos.

Para **atualizar fichas já cadastradas** (substitui também a galeria de imagens), execute somente depois de revisar as URLs e dados:

```powershell
python .\scripts\import_catalog.py .\catalogo-local.json --apply --update-existing
```

A atualização é por **nome normalizado + marca**; nomes iguais de produtos diferentes devem ser revisados manualmente antes, e renomeações devem ser tratadas de forma explícita. O script não remove produtos ausentes do catálogo, não altera estoque e não publica automaticamente. Uma falha parcial não reverte automaticamente cadastros já concluídos; confira o log e execute novamente, sem remover volumes.

## Testar na vitrine

- API administrativa: `GET /api/v1/admin/products` (requer sessão).
- Catálogo público: `GET /api/v1/products` **mostra somente `published=true`**.
- Depois de revisar fotos, preços e disponibilidade, publique os itens no painel administrativo e cadastre o estoque pela rota de movimentações (nada de SQL direto).
- O frontend em modo `live` usa a API: **não é necessário inserir produtos manualmente em HTML**, desde que a versão live esteja configurada para essa mesma API.

## Limitações

- Não lê catálogos do WhatsApp por login, captura de tela automatizada ou APIs não oficiais.
- Não faz upload de arquivos de imagem: recebe **URLs HTTPS estáveis**; armazenamento e distribuição das fotos devem ser definidos separadamente.
- Não é uma operação de produção, nem instala Docker, nem acessa seu PostgreSQL local a partir do ChatGPT.
- Nenhuma senha deve ser colocada no manifesto ou no GitHub.

## Lote real Skelt — 8 produtos da gravação

O arquivo revisável `docs/catalogo-skelt-2026-11-17.json` contém **oito produtos** transcritos da gravação WhatsApp com preço, descrição resumida e imagem local. O frontend da PR **arthurSchgg/loja-online-frontend#80** contém as oito miniaturas provisórias já versionadas em `assets/images/catalogo/skelt/`.

Estas imagens são de **256 × 256 px**, para testes visuais; fotos oficiais deverão ser substituídas antes da produção. As categorias são sugestões e precisam de revisão comercial. O preço anterior riscado de R$ 119,90 do Autobronzeador Bifásico não gerou promoção agendada.

Para conferir sem gravar:

```powershell
python .\scripts\import_catalog.py .\docs\catalogo-skelt-2026-11-17.json
```

Para cadastrar os oito produtos **na API local do IntelliJ**, em rascunho:

```powershell
python .\scripts\import_catalog.py .\docs\catalogo-skelt-2026-11-17.json --apply
```

Para criar **prévia pública apenas no localhost**, com `published=true`, `available=false`, estoque 0:

```powershell
python .\scripts\import_catalog.py .\docs\catalogo-skelt-2026-11-17.json --apply --preview-local
```

**Atenção:** o importador pula produtos já existentes por padrão. Para atualizar dados de registros de teste existentes, conferir o banco e usar `--update-existing` intencionalmente; a atualização substitui a galeria. Essa funcionalidade exige a integração de PR backend #183, e a CI ainda precisa ser aprovada antes do merge. Para teste imediato do computador, há também o pacote local fornecido no chat; ele é separado do pipeline de produção.

Não confiar no acesso remoto à API com HTTPS de outro host; o comando restringe o destino ao localhost. Não compartilhar credenciais ou `.env` no GitHub.
