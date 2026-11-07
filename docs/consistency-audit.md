# Auditoria de consistência — relatório inicial

**Resultado:** **NÃO APROVÁVEL AINDA**. Esta é uma triagem documental baseada na revisão recuperável e no estado do repositório `develop` consultado em 2026-11-07. Não afirma ter auditado o texto completo de todas as ~mil decisões. Aprovação formal depende da conferência integral das fontes.

## Achados confirmados na revisão e nos arquivos consultados

| Código | Achado | Evidência | Ação necessária |
|---|---|---|---|
| A-01 | Identificadores RN-647–650 reutilizados | Histórico de decisões sobre modelos/variáveis WhatsApp | Inventariar ocorrências e remapear sem perder referencial |
| A-02 | Identificadores RN-1039–1041 reutilizados | Histórico de Change Request e estado das regras | Preservar ambos os textos com IDs únicos |
| A-03 | Identificadores RN-1139–1144 reutilizados | Histórico de diagnóstico e reaproveitamento de issues | Reconciliar IDs com tabela de migração |
| A-04 | WhatsApp: modelo livre vs estrutura fixa | Decisões posteriores RN-661–672 | Marcar instruções incompatíveis antigas como substituídas |
| A-05 | Estoque no README antigo vs estoque aprovado | `README.md` e decisões recentes | Separar comportamento atual da regra-alvo |
| A-06 | Arquitetura cita Basic/JWT pendente | `docs/ARCHITECTURE.md` vs `docs/SECURITY.md` | Prevalece sessão+CSRF; corrigir doc antigo em PR futuro |
| A-07 | Código/contrato WhatsApp usa GET de link | `docs/WHATSAPP.md` vs exigência de confirmação | Abrir requisito técnico futuro, sem alterar API agora |
| A-08 | CI não mostra JaCoCo 80% | `pom.xml` e `.github/workflows/ci.yml` consultados | Avaliar no diagnóstico formal; implementar gate conforme política |
| A-09 | Duplicações de issues | #22/#29, #23/#30, #24/#31, #25/#32, #26/#33, #27/#34, #28/#35 | Investigar estados/históricos, não encerrar sem aprovação do lote |
| A-10 | Backlog antigo propõe formulário de interesse e analytics | issues #46/#50, decisões do MVP | Reclassificar ou manter histórico conforme política post-mvp |
| A-11 | Health endpoints existentes vs ausência de monitoramento dedicado no MVP | README e política posterior | Diferenciar endpoint técnico existente de obrigação de monitoramento |
| A-12 | Documentos preexistentes usam convenções divergentes | `docs/REVIEW-DECISIONS.md`, `docs/ARCHITECTURE.md` | Identificar documentos legados sem apagar seu histórico |

## Pendências bloqueantes da aprovação
- [ ] Recuperar fonte integral de cada aprovação e conciliar todos os RN duplicados.
- [ ] Criar catálogo definitivo RN → estado → sucessora → fonte → fase.
- [ ] Resolver lacunas de critérios de aceitação por história e dependências.
- [ ] Validar conteúdo com documentação preexistente sem confundir implementação com requisito.
- [ ] Revisão por PR e aprovação técnica explícita.
- [ ] Criar tag imutável `spec-vX.Y.Z` **após** a aprovação; nenhuma tag agora.

## Ordem mandatória acordada
1. Consolidação **(em andamento)**.
2. Aprovação técnica formal + referência Git **(pendente)**.
3. Diagnóstico formal da Fase 1 **(não iniciado)**.
4. Sete lotes de padronização, aprovados individualmente **(não iniciados)**.
5. Issue AUTH-001 e autorização expressa **(não concedida)**.
6. Somente depois, trabalho de implementação da Fase 1.

## Observação
Os arquivos `docs/REVIEW-DECISIONS.md` e demais guias são fontes existentes, mas contêm decisões antigas e pendências. Não promover seu texto como verdade comercial atual sem reconciliação.
