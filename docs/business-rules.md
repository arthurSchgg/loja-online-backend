# Regras de negócio — inventário de consolidação (rascunho)

> **Estado:** EM CONSOLIDAÇÃO, sem aprovação técnica. Este documento é um índice fiel das decisões **recuperáveis** da revisão; **não** é uma transcrição completa de RN-001 a RN-1295. Não usar como evidência de homologação ou autorização de implementação.

## Estado das regras
- **Ativa:** aprovada e pertinente ao escopo vigente; não implica implementação.
- **Substituída:** preservada historicamente, com referência explícita à decisão sucessora.
- **Futura:** fora do escopo vigente até seleção/aprovação de versão.
- Identificadores encontrados em mensagens anteriores são **provisórios** até reconciliação; não atribuir IDs novos retroativamente sem conferir a fonte original.

## Catálogo e estoque
- Produtos publicados: preço em BRL obrigatório, marca obrigatória, categorias opcionais, galeria de 1–12 imagens; produtos em rascunho podem ter 0 imagens.
- Indisponíveis continuam visíveis, mas não podem gerar solicitação.
- Disponibilidade efetiva combina estado manual e estoque positivo. Movimentações ENTRY/EXIT/ADJUSTMENT com histórico, integridade e proteção contra concorrência.
- Busca por nome/descrição e filtros combinados; ordenação recente, preço efetivo e nome; paginação de 12 por página.
- Promoções por período e preço efetivo, sem intervalos sobrepostos; histórico de mudanças.
- Destaques da página inicial escolhidos pela proprietária. Produtos relacionados automáticos, até seis.
- Arquivamento/restauração com restrições de exclusão. Sem avaliações públicas no MVP.

## Experiências
- Quizzes exibem resultado por perfil e até três recomendações manuais elegíveis; substituição por produtos disponíveis de marca/categoria semelhante quando faltar item recomendado.
- Não repetir produto. Exibir o perfil mesmo sem recomendações.

## Velas, oficinas e locações
- Serviços apenas para consulta pelo WhatsApp, sem agendamento, pagamento ou pedido persistido.
- Preço fixo opcional; na ausência, exibir **Valor sob consulta**.
- Velas: opções ativas de aroma, cor e embalagem vinculadas ao modelo e validadas pelo backend.
- Publicação, disponibilidade, galeria e arquivamento seguindo as regras específicas de serviços.

## Site e conteúdo
- Institucional e contatos editáveis pela proprietária; informações opcionais vazias ficam ocultas.
- Banners gerenciados com links internos válidos; carrossel automático a cada 5 s com pausa, controles e respeito a movimento reduzido.
- Página inicial com seções definidas; menu responsivo acessível, rodapé completo.
- Política de privacidade publicada; somente cookies operacionais/de segurança, sem analytics ou marketing no MVP.

## Administração, segurança e operação
- Única administradora; Spring Security com sessão HTTP, CSRF e cookies seguros; sem JWT, registro público ou conta padrão.
- Sessões expiram após 30 min de inatividade; proteção progressiva contra abuso; recuperação por token único com prazo de 15 min e notificações.
- Histórico de auditoria de operações importantes e movimentação de estoque; logs técnicos com rotação/retenção separada.
- PostgreSQL, Flyway, Docker; DEV/STAGING/PROD isolados. Backups diários e cópias externas semanais, com testes de restauração conforme decisão aprovada.
- Git Flow, revisão, staging, autorização explícita e rollback controlado. Testes e meta de **80% de cobertura de linhas JaCoCo** como condição de release.

## Frontend e WhatsApp
- Referência WCAG 2.2 AA, navegação por teclado, foco, contraste, leitores de tela, responsividade.
- Metas Core Web Vitals: LCP até 2,5 s, INP até 200 ms, CLS até 0,1 (p75 quando houver dados reais).
- Cache seletivo: arquivos estáticos podem ser cacheados; preço/promoções/estoque/disponibilidade revalidados em novas consultas.
- Atualização ao retornar à aba e ao solicitar; **sem polling contínuo**.
- Antes de qualquer solicitação, confirmação obrigatória com dados vigentes; backend revalida e bloqueia indisponíveis.
- **Decisão mais recente de mensagens:** backend define estrutura fixa por tipo (produto, vela, oficina e locação); proprietária edita **apenas saudação e encerramento**. Nome, preço ou “Valor sob consulta” e personalizações válidas são obrigatórios.
- Após confirmar, abrir WhatsApp; fornecer link manual e cópia da mensagem se necessário. Sem pedido persistido.

## Organização da entrega
- Sete fases detalhadas em [development-phases.md](development-phases.md).
- GitHub Projects com Backlog, Ready, In Progress, Code Review, Testing, Done; prioridades Alta/Média/Baixa; 1,2,3,5,8,13 SP somente em US.
- Único responsável pela história e suas tarefas. Dependências impeditivas bloqueiam Ready.
- Homologação por fase em issue, com aprovação formal do responsável técnico; pendências opcionais não podem contornar critérios obrigatórios.
- Issues US-###, TASK-BE-###, TASK-FE-###, TASK-TEST-###. Rastreabilidade documental e sete lotes de padronização com aprovação individual.
- SemVer, Milestones, releases, congelamento de escopo na branch release/*, hotfix/* controlado, incidentes e análise de causa raiz conforme gravidade.
- Especificação precisa de auditoria, aprovação técnica e tag Git própria antes do diagnóstico formal. Fase 1 só inicia após diagnóstico, sete lotes e AUTH-001 aprovada.

## Decisões refinadas/substituídas conhecidas
| Assunto | Disposição histórica | Decisão vigente | Tratamento |
|---|---|---|---|
| WhatsApp | Mensagens com variáveis arbitrárias editáveis | Estrutura fixa do backend; saudação/encerramento editáveis | Marcar cláusulas incompatíveis como substituídas após conciliar RN-638–RN-672 |
| Escopo de serviços | Issue histórica sugere formulário de interesse | Apenas WhatsApp no MVP | Tratar proposta antiga como fora do escopo |
| Banners | Código/docs antigos mencionam campanhas por período | Banners sem agendamento no escopo vigente; promoções têm vigência própria | Reconciliar domínios |
| Estoque | README antigo diz não haver estoque transacional | Estoque e ledger aprovados para o MVP | Distinguir estado do código e requisito |
| Monitoramento | Docs antigos sugerem health endpoints/alertas | Sem monitoramento dedicado ou alertas automáticos no MVP | Distinguir código existente e exigência |

## Auditoria de numeração — bloqueante
- RN-647–650, RN-1039–1041 e RN-1139–1144 foram reutilizadas em partes do histórico da revisão.
- O maior número citado foi RN-1295, **não** significa 1295 identificadores exclusivos e verificados.
- Conferir texto integral de cada aprovação e criar tabela: localização original, ID provisório, ID definitivo, estado, sucessora, decisão/Change Request.
- Não preencher RN ausentes por inferência. Consulte [consistency-audit.md](consistency-audit.md).

## Referências
Decisões recuperáveis da revisão do projeto e documentação preexistente em `docs/`. O código atual em `develop` pode divergir do comportamento aprovado. Este arquivo **não é uma assinatura da especificação**.
