# Desenvolvimento incremental — sete fases

**Status:** divisão aprovada no planejamento; critérios abaixo são referência para detalhamento em US e homologação, não sinalizam entrega.

| Fase | Escopo principal | Critérios funcionais de aceite | Critérios técnicos principais |
|---|---|---|---|
| 1 Fundação e segurança | Autenticação, infraestrutura, ambientes e persistência | Administradora autentica e acessa somente funcionalidades autorizadas | Sessão/CSRF, Flyway, PostgreSQL, Docker/staging, testes e documentação |
| 2 Catálogo e estoque | Produtos, marcas, categorias, imagens, estoque | CRUD, publicação, filtros e movimentos sem saldo negativo | DTOs, integridade, paginação, concorrência e testes |
| 3 Promoções e WhatsApp | Preços programados e solicitação | Vigência correta, confirmação e links validados | Prevenção de conflitos, testes de fronteira temporal e revalidação |
| 4 Experiências | Quizzes/perfis/recomendações | Resultado exibido e recomendações elegíveis sem repetição | Avaliação determinística, validação e testes de substituições |
| 5 Serviços | Velas, oficinas e locações | Personalização válida e contato via WhatsApp | Opções ativas, publicação, disponibilidade e testes |
| 6 Conteúdo e frontend | Home, banners, institucional, acessibilidade | Navegação e conteúdo público integrados | WCAG 2.2 AA como referência, responsividade, cache seletivo e CWV |
| 7 Administração e qualidade | Segurança avançada, auditoria, backups, qualidade final | Fluxos administrativos e manutenção conformes | JaCoCo 80%, restauração, testes integrados, docs, homologação |

**Transversais:** frontend necessário à história desde as primeiras fases; segurança, testes e documentação não ficam adiados até as fases 6–7.

## Condições de conclusão
- Histórias obrigatórias em Done após DoD, revisões e testes.
- Issue de homologação `HML-###` por fase, evidências e aprovação do responsável técnico.
- Pendências não bloqueantes fora dos critérios obrigatórios podem ser adiadas apenas com registro, responsável e aprovação.
- Aprovação de fase **não** autoriza automaticamente deploy em produção.

## Pré-condições à implementação da Fase 1
1. Especificação consolidada, sem inconsistências bloqueantes, tecnicamente aprovada e referenciada por tag `spec-vX.Y.Z`.
2. Diagnóstico formal em `docs/phase-1-diagnostic.md`, avaliando cada requisito como Atendido/Parcial/Pendente, com evidências.
3. Sete lotes de padronização de issues analisados, aprovados e validados.
4. Issue `AUTH-001` com autorização técnica **explícita**.

**Nenhum dos quatro itens acima foi atestado neste rascunho.**
