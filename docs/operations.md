# Operações — referência de requisitos

**Status:** planejamento consolidável, não manual de produção validado. Consultar os guias existentes [LOCAL-STAGING.md](LOCAL-STAGING.md), [BACKUP-RECOVERY.md](BACKUP-RECOVERY.md), [DEPLOYMENT.md](DEPLOYMENT.md) e [RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md).

## Ambientes
- DEV, STAGING e PROD isolados, configurações/bancos/segredos distintos. Staging usa dados fictícios.
- Ambiente de staging local HTTPS via Caddy/Docker já descrito; não confundir com publicação em produção.
- Sem monitoramento dedicado e alertas automáticos no MVP segundo a revisão. Documentação e código antigos podem conter health endpoints; sua existência não altera esta decisão.

## Backups aprovados
- Backups PostgreSQL diários com retenção de sete diários e quatro semanais; armazenamento fora do volume principal.
- Cópias externas cifradas semanais com quatro retenções, chave separada; testes mensais de restauração isolada.
- Os scripts existentes executam backup e restore sob demanda: **não comprovam agendamento, retenção, cópia externa nem operação em produção**.

## Log e incidentes
- Logs técnicos com rotação e retenção de até 30 dias, acesso restrito, sem segredos.
- Incidentes `INC-###` com gravidade Crítica/Alta/Média/Baixa separada da prioridade, evidências, versão, responsável e verificação pós-correção.
- RCA e ações preventivas para críticos, recorrentes ou com impacto importante em segurança/integridade.

## Releases
- Git Flow, CI, staging, homologação e autorização explícita para produção; backup pré-migração e rollback compatível com esquema.
- Hotfix crítico parte da tag/commit em produção e volta a develop/release conforme necessário.
