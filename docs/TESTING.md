# Testes de backend

- `CatalogIntegrationTest` e `StoreIntegrationTest`: smoke tests rápidos usando H2 (Spring Test + MockMvc).
- `PostgresMigrationIntegrationTest`: teste de integração que conecta ao PostgreSQL real, executa todas as migrações Flyway e valida Hibernate/schema + endpoint administrativo.

O workflow `.github/workflows/ci.yml` instala e inicia PostgreSQL no runner, prepara o banco `lojinha_ci` e define `RUN_POSTGRES_INTEGRATION=true`, além de todas as variáveis de teste necessárias. As credenciais de teste do CI **não** devem ser reutilizadas em produção.

## Local
Execute `mvn clean verify` para os testes H2 sem dependências extras. Para incluir o teste PostgreSQL, inicie PostgreSQL local e configure `RUN_POSTGRES_INTEGRATION=true`, `POSTGRES_TEST_URL`, `POSTGRES_TEST_USER`, `POSTGRES_TEST_PASSWORD`. Use um banco temporário vazio dedicado aos testes.

Os testes ainda **não** cobrem todos os fluxos da aplicação; antes do lançamento são necessários cenários negativos e de domínio para todos os módulos, segurança em profundidade e homologação.
