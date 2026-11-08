# Plano de testes e homologação

**Estado:** critérios propostos para aprovação; não substitui evidência de CI ou homologação.

## Cobertura e gates
- `mvn verify` deve executar unitários, integração (incluindo PostgreSQL real no CI), verificações Flyway e JaCoCo.
- Meta de **>=80% de cobertura de LINHAS** no módulo avaliado, medida no CI; a meta não pode ser reduzida para encobrir ausência de testes.
- O PR de proposta JaCoCo é [#111](https://github.com/arthurSchgg/loja-online/pull/111).
- Não considerar build com falha aprovado. Atualizar testes em vez de excluir código legítimo indiscriminadamente.

## Matriz mínima
| Área | Cenários obrigatórios | Evidência |
|---|---|---|
| Sessão/CSRF | Login inválido, expirado, logout, CSRF ausente, rotação do token | MockMvc, integração |
| Autorização | Rotas admin bloqueadas a anônimos, públicas somente leitura | Security tests |
| Catálogo | Rascunho, publicado, busca e filtros, paginação, preços | Service/REST/JPA |
| Estoque | Entrada, saída, ajuste, saldo negativo e concorrência | PostgreSQL integração |
| Promoção | Períodos válidos/inválidos e sobreposição; fronteiras de vigência | Unitário/integração |
| WhatsApp | Confirmação, mudança de preço, indisponível, personalização inválida | Contrato REST + E2E |
| Quizzes | Alternativas válidas, empates, fallback, zero recomendação | Unitário + REST |
| Velas/serviços | Preço opcional, opção ativa, modelos, sem reservas | Unitário + integração |
| Acessibilidade | Foco, teclado, labels, ordem de leitura, contraste, reduced motion | Manual/automático |
| Responsividade | Mobile, tablet, desktop, estados carregando/vazio/erro | E2E e revisão visual |
| Staging | TLS, cookies, API na mesma origem, dados isolados, migrações | Smoke tests |
| Backup | Restaurar base isolada com integridade | Log/relatório de exercício |

## Homologação
Cada fase usa issue [HML-###](https://github.com/arthurSchgg/loja-online/issues/112) própria com critérios, evidências, defeitos e decisão técnica expressa. O responsável comercial verifica dados reais e conteúdo; o responsável técnico aprova a liberação da fase. Nenhum teste verde isolado substitui isso.

## Condições de produção
CI na release, homologação comercial e técnica, políticas de backup/restauração, segredos/HTTPS, aprovação do conteúdo, LGPD/privacidade e autorização de release. Manter `main` protegida de mudanças diretas.
