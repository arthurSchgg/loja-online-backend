# Convenções Git Flow

- `main`: releases estáveis e revisados; não recebe trabalho direto.
- `develop`: integração contínua das funcionalidades e correções.
- `feature/<dominio>`: implementações independentes criadas a partir de `develop`, integradas por Pull Request.
- `release/x.y.z`: estabilização e homologação, quando houver versão pronta para produção.
- `hotfix/<defeito>`: correções urgentes de produção, propagadas a `main` e `develop`.

As branches funcionais criadas neste trabalho representam bootstrap, catálogo, segurança/loja, testes/documentação, experiências, serviços de velas, conteúdo institucional, campanhas, PostgreSQL e operações.

## Commits

Adotar Conventional Commits: `feat(catalog): ...`, `fix(security): ...`, `test(postgres): ...`, `docs(api): ...`.

Cada PR deve ter issue relacionada, checklist de testes, documentação de endpoints, CI verde, revisão e aprovação antes da implantação. O merge para `develop` **não significa** automaticamente que a issue comercial está homologada.
