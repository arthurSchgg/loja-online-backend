# Crosswalk do baseline inicial — requisitos, RN e histórias legadas

**Origem:** documentação inicial recuperada do arquivo `lojinha-da-katia-documentacao-inicial.zip` (pastas `docs/02-requisitos.md`, `docs/03-regras-negocio.md`, `docs/07-historias-casos.md`). É um baseline **anterior** à revisão extensa de negócios RN-###. Não apagar nem reescrever o original; este arquivo preserva equivalências e mudanças, pendentes de validação final.

## Inventário fiel do baseline
- **28 requisitos funcionais**: RF001–RF028.
- **14 requisitos não funcionais**: RNF001–RNF014.
- **17 regras**: RN001–RN017, com estados C/P/V do documento inicial.
- **4 histórias**: US001–US004 no formato **sem hífen**, não equivalentes automaticamente a US-001–US-004 criadas no GitHub.
- Cada identificador do arquivo inicial é tratado aqui como `LEGACY-*`, até que a consolidação atribua equivalência oficial. Não atribuir RF ou RN novos por inferência.

## Regras legadas e relação com a revisão vigente

| ID legado | Enunciado do baseline inicial | Situação perante revisão mais recente | Tratamento |
|---|---|---|---|
| RN001 | Vendas concluídas no WhatsApp, sem checkout | Compatível | Incorporar com confirmação obrigatória |
| RN002 | Produto pertence a categoria obrigatória | **Substituída** | Categoria opcional na regra mais recente |
| RN003 | Produto pode ter marca opcional | **Substituída** | Marca obrigatória na regra mais recente |
| RN004 | Produto pode ter múltiplas imagens | Refinada | 1–12 imagens para produto publicado |
| RN005 | Produto pode ser destaque/novidade | Refinada | Destaques administráveis; revisar novidades |
| RN006 | Indisponível não pode parecer disponível | Refinada | disponibilidade efetiva combina campo manual e saldo >0 |
| RN007 | Valores em BRL | Compatível | BigDecimal/precisão e exibição BRL |
| RN008 | Promoção tem condições e período | Refinada | Sem sobreposições e preço efetivo |
| RN009 | WhatsApp pode conter nome de produto | Refinada | Nome/preço obrigatórios, estrutura fixa backend |
| RN010 | Experiências seguem respostas | Refinada | Até 3 recomendações elegíveis e fallback |
| RN011 | Workshops/aluguel administráveis | Compatível | Consulta WhatsApp, sem agendamento |
| RN012 | Status rascunho/publicado | Compatível | Publicação e arquivamento mais detalhados |
| RN013 | Preço decimal não negativo | Refinada | Preço BRL obrigatório para produto publicado |
| RN014 | Exclusão lógica | Refinada | Arquivamento/regras de dependência |
| RN015 | Exibição do preço indisponível era comercialmente pendente | Exige conciliação | Política posterior de catálogo exige estado visual correto; decisão detalhada deve ser conferida |
| RN016 | Fórmula e condições de desconto pendentes | Parcialmente resolvida | Promoções de preço por vigência aprovadas; fórmula/limites finais requerem fonte específica |
| RN017 | Regras de recomendação pendentes | Parcialmente resolvida | Resultados por perfil e recomendações até 3 aprovados; conteúdo específico dos quizzes é comercial |

## Histórias legadas com IDs sem hífen

| ID legado | Tema no arquivo inicial | Correspondência atual sugerida | Observação |
|---|---|---|---|
| LEGACY-US001 | Administrar produtos | Issues antigas #19–#22, #21 (revisar) | **NÃO corresponde** à US-001 #81 de estoque |
| LEGACY-US002 | Navegar pelo catálogo | Issues #9–#13 e US-006 #86 (frontend) | **NÃO corresponde** à US-002 #82 de promoções |
| LEGACY-US003 | Consultar WhatsApp | #15 + US-003 #83 | Versão atual adiciona confirmação e revalidação |
| LEGACY-US004 | Administração protegida | #17/#18 + US-008 #88 | Versão atual usa sessão/CSRF e recuperação |

**Regra de numeração:** manter prefixo `LEGACY-` apenas nesta tabela de migração, sem renomear retroativamente o arquivo fonte. IDs GitHub `US-001` etc. seguem catálogo atual, mas precisam do aceite final de unicidade antes do encerramento dos lotes.

## Requisitos do baseline revisados quanto à fase
- RF018 promoções: baseline inicial indicava pós-MVP; revisão mais recente atribui **Fase 3 do MVP**.
- RF020–RF022 experiências: baseline pós-MVP; revisão mais recente inclui **Fase 4**.
- RF024–RF027 velas/serviços: baseline pós-MVP; revisão mais recente inclui **Fase 5**.
- RF023 cartões digitais: manter pós-MVP, sem backend obrigatório decidido.
- RF016 segurança: baseline pré-publicação/JWT futuro, substituído por sessão HTTP + CSRF na **Fase 1**.
- RF003–RF014 catálogo: continuam no MVP, com saldo quantitativo e promoções mais detalhados na revisão.
- RNF001–RNF014: conservar como requisitos não funcionais de referência; RNF003 já inclui LCP <=2,5 s, CLS <=0,1 e INP <=200 ms; RNF002 WCAG 2.2 AA como meta. Acrescentar JaCoCo >=80% conforme revisão posterior.

## Divergências com briefing ainda mais antigo
O briefing inicial do projeto indicava não implementar estoque automatizado, JWT futuro e **oito fases**. As decisões posteriores passaram a incluir estoque no MVP, sessão HTTP com CSRF e **sete fases**. O baseline não deve reverter essas escolhas.

## Pendências
- [ ] Anexar/identificar o hash/versionamento da documentação inicial usada como fonte, preservando a cópia original.
- [ ] Recuperar os textos integrais das regras RN-### posteriores para tabela definitiva de estados/sucessoras.
- [ ] Validar formalmente este crosswalk e atualizar IDs de RF/RNF/US no GitHub Projects.
- [ ] Diferenciar regra confirmada pelo briefing inicial de regra confirmada posteriormente por decisão da loja.
