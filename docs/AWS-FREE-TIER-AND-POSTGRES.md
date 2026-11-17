> **HISTÓRICO / NÃO UTILIZAR:** a hospedagem AWS foi descartada para a Lojinha da Kátia em 16/11/2026. O plano atual é [Cloudflare Pages + Render + Neon](FREE-HOSTING-NEON-CLOUDFLARE.md). Nenhum serviço AWS deve ser criado ou publicado com base neste arquivo.

# PostgreSQL e AWS Free Tier — Lojinha da Kátia

**Decisão técnica:** não provisionar serviços com cobrança antes de homologação, orçamento, responsabilidade pelo faturamento e aceite explícito. Este documento descreve opções, não autoriza o deploy.

## O que já está implementado

- `compose.yaml`: container PostgreSQL 16 com banco `lojinha_da_katia` (padrão) e volume persistente `local_postgres_data` na máquina local.
- `compose.staging.yaml`: PostgreSQL 16 isolado, banco `lojinha_staging`, volume `staging_postgres_data`, API HTTPS local através de Caddy e Mailpit para e-mails simulados.
- O Spring Boot configura o PostgreSQL por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O Flyway cria/atualiza **todas as tabelas de domínio** automaticamente. Não criar tabelas manualmente em pgAdmin.
- Os bancos nos testes de CI são temporários. Nenhum banco persistente já foi criado na AWS.

## Inicialização local (sem recursos AWS)

1. No repositório backend, copiar `.env.example` para `.env`. Escolher credenciais fortes, diferentes das amostras; **não versionar o arquivo**.
2. Executar `docker compose --env-file .env -f compose.yaml up --build -d` com Docker instalado. Os serviços `postgres` e `backend` são iniciados na própria máquina.
3. Validar `docker compose --env-file .env -f compose.yaml ps` e `http://localhost:8080/actuator/health`.
4. Conferir as migrações com `docker compose --env-file .env -f compose.yaml exec postgres psql -U lojinha -d lojinha_da_katia -c 'SELECT installed_rank, version, success FROM flyway_schema_history ORDER BY installed_rank;'` quando forem usados os valores padrão de usuário e banco.
5. Nunca executar `docker compose down -v` sobre um banco com dados a preservar; ele remove volumes.

O banco local contém estruturas e dados apenas do ambiente local; não é um banco público. Homologação usa credenciais e volume separados.

## Regras de custo e elegibilidade AWS (verificar antes do uso)

Conforme documentação AWS consultada em **novembro de 2026**:

- Novos clientes elegíveis podem escolher **Free account plan**: US$ 100 de créditos iniciais e até US$ 100 adicionais por atividades elegíveis; período de até **6 meses**, encerrado antes se esgotarem os créditos.
- O plano gratuito não cobra antes da atualização para plano pago, mas **é encerrado** ao expirar; sem upgrade, recursos e dados deixam de ficar disponíveis, havendo prazos limitados de recuperação.
- **Amazon RDS PostgreSQL não é gratuito para sempre**. Instâncias e recursos podem consumir os créditos; o plano gratuito pode limitar tipos/serviços disponíveis.
- **ECS/Fargate, ALB, RDS, transferência, armazenamento e backups podem consumir créditos e gerar custos em plano pago**. O desenho ECS Express + RDS + Amplify/CloudFront é uma proposta futura, não um conjunto garantidamente gratuito.
- Região `sa-east-1` e configuração final exigem simulação no AWS Pricing Calculator; não pressupor custo zero em nenhuma região.
- Se houver conta AWS anterior, o direito a novos créditos pode ser diferente: conferir diretamente o tipo de plano no console.
- **AWS Budgets e alertas informam sobre gastos, mas não equivalem a bloqueio automático de cobrança no plano pago.**
- A criação e titularidade de contas com obrigação financeira devem ficar sob responsabilidade de adulto autorizado pela loja.

Documentação oficial:
- https://docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/free-tier-plans.html
- https://aws.amazon.com/free/free-tier-faqs/
- https://aws.amazon.com/rds/free/
- https://calculator.aws/

## Caminho recomendado

**Até a homologação:** usar Docker/PostgreSQL local e CI, sem custo de nuvem. Não inserir conteúdo comercial fictício no banco produtivo.

**Protótipo temporário na AWS, se elegível:** usar o Free account plan apenas com serviços suportados, créditos monitorados e backup exportável fora da conta. Não assumir que o protótipo é uma operação comercial permanente ou disponibilidade 24/7.

**Produção comercial:** avaliar alternativa de baixo custo após domínio, DNS e orçamento aprovados. Confirmar origem HTTPS única para painel/API, CSRF, SMTP, política de sessão e backup/restore; definir responsável pela conta, custos, operação e decisão de upgrade. O site pode usar um endereço temporário emitido pelo provedor antes de registrar domínio próprio, desde que a arquitetura de mesma origem seja validada.

**Bloqueios de release:** HML-001–007, HML-FE-001, conteúdo e direitos de imagem, testes E2E completos, restauração de backup e GO técnico/comercial. Nenhum recurso AWS deve ser criado automaticamente por este planejamento.
