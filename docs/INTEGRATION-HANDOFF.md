# Handoff de integração — backend MVP (sem homologação)

**Data da verificação:** 2026-11-08. **Estado:** branch técnica preparada para revisão; não integrada a develop/main, não publicada.

## Resultado reproduzível do CI

- Execução: https://github.com/arthurSchgg/loja-online/actions/runs/42505843197
- Conclusão GitHub Actions: **success**.
- JaCoCo LINE: **860 cobertas / 105 não cobertas / 965 no total = 89,12%**.
- Regra Maven mantida: mínimo **80% de linhas cobertas**; nenhuma exclusão artificial de classes no gate.
- Testes Maven/PostgreSQL, verificação de Compose, backup/restauração CI executados conforme workflow da branch.
- O CI comprova o build na branch; **não é um teste de produção nem aprovação de negócio**.

## Componentes consolidados

| Componente | Origem | Observações |
|---|---|---|
| Catálogo base, sessão, CSRF, PostgreSQL, Flyway V1–V6 | develop | Pré-existentes |
| Estoque + ledger V7 | PR #120 | Requer inventário inicial dos saldos legados |
| Promoções e preço efetivo V8 | PR #121 | Revalidar vigências e concorrência em staging |
| WhatsApp com revalidação | PR #122 | Confirmar preço, disponibilidade, serviços; sem checkout |
| Recomendação de quiz V9 | PR #123 | Perfis com até 3 produtos e fallback |
| Opções de vela V10 | PR #126 | Aroma, cor e embalagem administráveis |
| Auditoria persistente V11 | PR #129 | Registros de operações que alcançam o MVC |
| Proteção progressiva de login | PR #132 | Em memória, limitada a uma réplica |
| Gate JaCoCo e testes | PR #111 | CI atualizado e relatórios em artifact |
| Preparação AWS | PR #119 | Perfil prod, manual ECR publish, ECS/RDS/Amplify documentados |

**PRs alternativos #124 e #125:** não misturar seus conjuntos de migrações V9/V10 sem comparar e encerrar duplicações. A integração utiliza #123 e #126.

## Sequência de merge proposta, somente após autorizações aplicáveis

1. Aprovar especificação PR #78 e tag documental. Concluir diagnóstico, padronizar os sete lotes e aprovar AUTH-001.
2. Revisar o PR de integração #134 e diffs individuais; investigar os PRs alternativos #124/#125.
3. Executar migrações Flyway V7–V11 em uma cópia de staging do banco real, com backup anterior e restauração verificada; **V7 define saldo inicial zero**.
4. Executar testes de ponta a ponta do frontend separado, com imagens, contatos e dados de produtos comerciais aprovados.
5. Validar sessão/CSRF/limitação de login, auditoria, atualização do estoque e consistência do preço do WhatsApp em ambiente de homologação real.
6. Concluir lacunas restantes: recuperação e alteração de senha seguras, outbox de e-mails, auditoria detalhada, disponibilidade/galeria de serviços, editor completo de quizzes, política de privacidade e design final.
7. Homologar HML-001 a HML-007 e autorizar release formal. Só então fazer merge/release/deploy.

## AWS: preparação pronta, implantação real pendente

- Backend: Docker + ECS Express Mode + ECR; RDS PostgreSQL privado, Secrets Manager e domínio HTTPS.
- Frontend: ZIP `lojinha-da-katia-frontend-atualizado.zip`, repositório GitHub **separado** e Amplify Hosting para vitrine.
- Painel admin deve compartilhar a **mesma origem** HTTPS da API, com CloudFront/roteamento equivalente.
- A criação de recursos pagos, orçamento, credenciais, domínio e dados reais **não foi realizada**.
- Não publicar sem planejar exportação cifrada semanal, retenção de backup, restauração mensal, logs e rollback.

## Pendências ainda bloqueantes

- [ ] Aprovações documentais/técnicas de especificação e lotes.
- [ ] AUTH-001 registrada como autorizada por pessoa responsável após checklist.
- [ ] Revisões de segurança e teste E2E de cenários críticos.
- [ ] Completar recuperação e alteração de senha/segurança de e-mail.
- [ ] Políticas comerciais reais e identidade visual aprovadas.
- [ ] Conta AWS e despesas aprovadas; repositório frontend criado.
- [ ] Homologações de sete fases e release antes de produção.

**Nota de integridade:** a disponibilidade deste PR demonstra implementação e validação de CI de módulos específicos, não significa que a totalidade da especificação tenha sido atendida ou que o site esteja pronto para vender.

## Complemento de revisão — 2026-11-09 (PR #139, ainda sem homologação)

- PR [#139](https://github.com/arthurSchgg/loja-online/pull/139) empilhado sobre #134: acrescenta a migration única `V12__add_offering_availability.sql`, o campo `available` para serviços e testes de visibilidade/WhatsApp.
- `published` e `available` permanecem independentes. Serviço público indisponível fica visível e não gera link de WhatsApp; requests antigos sem `available` preservam a compatibilidade.
- Proteção do login: a limpeza global `attempts.clear()` foi substituída por descarte pontual para impedir o reset simultâneo de todos os bloqueios. A implementação continua **em memória**, com limitações de proteção sob sobrecarga e inadequada para múltiplas réplicas AWS sem armazenamento compartilhado.
- **Validação necessária:** CI do commit final do PR #139, migrações Flyway V1–V12, testes com dados pré-existentes, cenários de ofertas e revisão de segurança. Não considerar os 89,12% da branch anterior como prova para #139.
- **Lacunas de escopo além deste PR:** recuperação/alteração de senha, notificações e e-mail seguro, galeria completa, editor de experiências, política de privacidade, disponibilidade de itens adicionais e políticas operacionais. Não declarar backend integralmente concluído enquanto estiverem pendentes.
- **Decisão:** branch para revisão, sem merge em `develop`/`main`, sem AWS, sem AUTORIZADA na AUTH-001 e sem homologações presumidas.
