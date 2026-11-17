# Catálogo WhatsApp — captura parcial para revisão (17/11/2026)

**Status: levantamento parcial, NÃO importado.** O link original de catálogo WhatsApp não retornou fichas públicas legíveis; os dados abaixo foram transcritos exclusivamente de **duas capturas fornecidas pelo proprietário**. Não usar este documento como autorização de publicação.

| # | Nome visível no catálogo | Preço visível | Preço riscado | Descrição legível |
|---|---|---:|---:|---|
| 1 | Maquiagem Corporal Bronzeado Instantâneo | R$ 99,90 | — | Truncada: “O Bronzeado Instantâneo é uma ma...” |
| 2 | Kit Miniaturas Sellers Set | R$ 99,90 | — | 3 fragrâncias de 15ml cada |
| 3 | Kit miniaturas Exótic Set | R$ 99,90 | — | Truncada: “Seus destinos favoritos em 15ml. O...” |
| 4 | Kit Miniaturas Trends Set | R$ 99,90 | — | 3 fragrâncias de 15ml cada |
| 5 | Mousse Autobronzeador Dark | R$ 130,00 | — | Truncada |
| 6 | Luva aplicadora de autobronzeador | R$ 35,00 | — | Truncada |
| 7 | Autobronzeador Bifásico | R$ 104,00 | R$ 119,90 | Truncada |
| 8 | Maquiagem Corporal Iluminador | R$ 99,90 | — | “Iluminador Corporal Skelt” |

## Dados que ainda faltam para importar via API

- **Marcas confirmadas** por produto: as miniaturas sugerem Skelt em alguns itens, mas não permitir inferência automática sem conferência.
- **Imagens originais**, com permissão de uso, e seu endereço HTTPS estável. Não reutilizar miniaturas da captura ou links temporários do WhatsApp como fotos finais.
- **Descrições completas**: algumas estão truncadas. Não completar por suposição.
- **Categorias validadas**, **quantidade em estoque** e estado disponível/indisponível.
- **Regras promocionais** do Autobronzeador Bifásico: há preço antigo riscado, mas não há datas de validade. Não criar promoção programada sem vigência confirmada.
- Outros produtos do catálogo, caso haja páginas além das duas capturas.

## Próxima ação técnica

Quando as informações restantes estiverem confirmadas, preencher um manifesto baseado em `docs/catalog-import-template.json` e validar com `python scripts/import_catalog.py catalogo-local.json`. Depois de revisar, executar `--apply` **apenas no banco local**, criando rascunhos não publicados. O frontend live já consome o endpoint público, então não é necessário editar produtos individuais no HTML.

**Nada deste documento deve ser interpretado como migração de dados já realizada.**
