# Matriz de testes de regressão — backend

Cobertura adicionada para os módulos de experiências, ofertas, páginas institucionais e campanhas.

| Área | Cenários cobertos |
| --- | --- |
| Segurança | Bloqueio de escrita anônima, senha inválida, acesso público autorizado |
| Catálogo | Criação de produto, filtros combinados, visibilidade de rascunhos, paginação |
| Loja / WhatsApp | Atualização administrativa e geração de link contextual |
| Experiências | Publicação, cálculo determinístico, opção inválida e rascunho |
| Velas e serviços | Publicação, filtro por tipo, rascunhos, preço inválido |
| Institucional | Rascunhos invisíveis e consulta administrativa |
| Campanhas | Publicação, janela futura e intervalo inválido |
| Banco real | Migrações Flyway e escrita no PostgreSQL em CI |

## Limitações
Testes de regressão não substituem testes de carga ou verificação profissional de segurança. Continuação recomendada: testes de concorrência, manipulação de mídias, versionamento dos contratos, restore de backups e integração real com o frontend.

Revisar o CI a cada PR. O teste de PostgreSQL é obrigatório apenas na pipeline com `RUN_POSTGRES_INTEGRATION=true`.
