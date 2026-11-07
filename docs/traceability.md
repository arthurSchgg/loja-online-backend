# Matriz de rastreabilidade — esqueleto inicial

> **NÃO HOMOLOGADA.** IDs de US e tarefas ainda não foram reconciliados; não inventar referências. Uma linha sem vínculo é **pendência de planejamento**, nunca evidência de descumprimento implementado.

| Regra / grupo | Escopo | Fase | US | Tarefas | Testes / evidência | Homologação | Situação |
|---|---|---:|---|---|---|---|---|
| Catálogo / produtos | Ativa, IDs a validar | 2 | A mapear | A mapear | A mapear | HML-002 planejada | Sem rastreio validado |
| Estoque / saldo e movimentos | Ativa, IDs a validar | 2 | A mapear | A mapear | A mapear | HML-002 planejada | Sem rastreio validado |
| Promoções / vigência | Ativa, IDs a validar | 3 | A mapear | A mapear | A mapear | HML-003 planejada | Sem rastreio validado |
| WhatsApp / confirmação e estrutura | Ativa após reconciliação | 3, 5 | A mapear | A mapear | A mapear | HML-003 / HML-005 planejadas | Regras históricas conflitantes |
| Quizzes e recomendações | Ativa, IDs a validar | 4 | A mapear | A mapear | A mapear | HML-004 planejada | Sem rastreio validado |
| Serviços e personalizações | Ativa, IDs a validar | 5 | A mapear | A mapear | A mapear | HML-005 planejada | Sem rastreio validado |
| Conteúdo e WCAG | Ativa, IDs a validar | 6 | A mapear | A mapear | A mapear | HML-006 planejada | Sem rastreio validado |
| Sessão administrativa/CSRF | Ativa, IDs a validar | 1 | A mapear | A mapear | A verificar em testes | HML-001 planejada | Implementação existe; não homologada |
| Qualidade/CI/JaCoCo | Ativa, IDs a validar | 1–7 | A mapear | A mapear | CI existente; JaCoCo a verificar | Todas planejadas | Pendente diagnóstico |
| Backup e incidentes | Ativa, IDs a validar | 7, transversal | A mapear | A mapear | Scripts sob demanda; operação não validada | HML-007 planejada | Pendente diagnóstico |

## Regras de manutenção
- Só preencher US/Task com issue encontrada, validada e padronizada, respeitando o planejamento por lotes.
- Registrar separadamente implementação, testes e homologação; `Done` na issue não é homologação.
- Regras substituídas mantêm trilha histórica em `business-rules.md`; futuras ficam fora da cobertura obrigatória do MVP.
