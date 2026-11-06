# Gestão de marcas e categorias

O painel administrativo recebe agora listagem completa (incluindo inativos), consulta por ID e reativação, além de cadastro, edição e inativação já existentes.

| Operação | Marca | Categoria |
| --- | --- | --- |
| Listar todos | GET /api/v1/admin/brands | GET /api/v1/admin/categories |
| Consultar um | GET /api/v1/admin/brands/{id} | GET /api/v1/admin/categories/{id} |
| Cadastrar | POST /api/v1/admin/brands | POST /api/v1/admin/categories |
| Renomear | PUT /api/v1/admin/brands/{id} | PUT /api/v1/admin/categories/{id} |
| Inativar | DELETE /api/v1/admin/brands/{id} | DELETE /api/v1/admin/categories/{id} |
| Reativar | PUT /api/v1/admin/brands/{id}/activate | PUT /api/v1/admin/categories/{id}/activate |

Todos os endpoints administrativos exigem ADMIN; requisições de publicação usam DTOs validados. GETs públicos continuam omitindo itens inativos, sem quebrar os relacionamentos de produtos.
