# Governança — issues, Projects, homologação e releases

## Identificadores
- Histórias `US-001`; tarefas `TASK-BE-001`, `TASK-FE-001`, `TASK-TEST-001`; homologações `HML-001`; incidentes `INC-001`; mudanças `CR-001`; autorização `AUTH-001`.
- Sequências de cada tipo são independentes, únicas e não reutilizáveis. Issues antigas não devem receber novos nomes antes de inventário e aprovação do lote.

## GitHub Projects
- Status: **Backlog → Ready → In Progress → Code Review → Testing → Done**.
- Prioridade: **Alta, Média, Baixa**.
- Story Points: **1, 2, 3, 5, 8, 13**, apenas em histórias; tarefas sem pontuação.
- Uma única pessoa responsável por cada US **e por todas suas tarefas vinculadas**.
- Dependências impeditivas abertas bloqueiam Ready; relações explícitas entre issues; sem aprovação automática.
- Progresso combinado: SP concluídos, fração de US em Done e andamento das tarefas separados; homologação é dimensão independente.
- Labels por tipo, módulo, camada e escopo; prioridade/fase/SP em campos do Projects, sem labels redundantes.
- Catálogo de labels proposto na revisão: tipo `feature`, `bug`, `refactor`, `test`, `docs`, `chore`, `security`, `performance`; módulos `auth`, `user`, `catalog`, `product`, `brand`, `category`, `inventory`, `promotion`, `whatsapp`, `quiz`, `recommendation`, `candle`, `workshop`, `rental`, `content`, `banner`, `contact`, `audit`, `notification`, `backup`; camadas `backend`, `frontend`, `database`, `api`, `security-config`, `infrastructure`, `devops`, `ui-ux`; escopo `MVP`, `post-mvp`. Confirmar resolução de termos próximos antes da criação.

## Padronização em sete lotes
1. Fundação; 2. Catálogo; 3. Promoções/WhatsApp; 4. Experiências; 5. Serviços; 6. Frontend/conteúdo; 7. Administração/qualidade.
- Para cada lote: inventário de issues abertas/fechadas, proposta de mudanças, valores anteriores relevantes, aprovação técnica, aplicação, validação dos vínculos e relatório `docs/github-standardization/batch-XX-*.md`.
- Não reescrever histórico de issues fechadas. Reutilizar issues existentes; duplicações devem ser justificadas.
- **Não** padronizar nenhuma issue antes de concluir as etapas anteriores da sequência de autorização.

## Homologação e autorização
- Cada fase possui issue `HML-###`, checklist e evidências; somente responsável técnico aprova a homologação.
- Após especificação aprovada, diagnóstico e os sete lotes: issue `AUTH-001` registra autorização ou recusa do início da Fase 1.
- Concluir checklist não é o mesmo que registrar aprovação formal.

## Git Flow e releases
- `feature/* → develop → release/* → main`, CI, homologação e aprovação antes de publicar o mesmo artefato testado.
- SemVer, Milestones por versão, GitHub Releases e tags imutáveis. Tags documentais `spec-vX.Y.Z` separadas das tags de software `vX.Y.Z`.
- Escopo congela ao criar `release/*`; somente correções, segurança ou ajustes obrigatórios com registro/aprovação.
- `hotfix/*` a partir da versão efetivamente em produção, teste direcionado, aprovação, nova versão PATCH e integração às branches pertinentes.
- Incidentes `INC-###`: gravidade separada de prioridade; verificar resolução em produção. RCA obrigatório se crítico, recorrente ou com impacto relevante à segurança/integridade.

## Mudança de regra
Usar `CR-###` com justificativa, impacto, responsável técnico, PR e sucessora da regra quando houver. Nunca reescrever silenciosamente decisões anteriores.
