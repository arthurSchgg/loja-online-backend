package br.com.lojinhadakatia;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Executes every versioned migration against real PostgreSQL in the CI job.
 * Uses a dedicated schema to avoid touching the application's test data.
 */
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_INTEGRATION", matches = "true")
class PostgreSqlFlywayV14IntegrationTest {
    @Test
    void allMigrationsApplyToCleanPostgresSchema() {
        String url = System.getenv("POSTGRES_TEST_URL");
        String username = System.getenv("POSTGRES_TEST_USER");
        String password = System.getenv("POSTGRES_TEST_PASSWORD");
        assertTrue(url != null && url.startsWith("jdbc:postgresql:"));
        Flyway flyway = Flyway.configure()
            .dataSource(url, username, password)
            .schemas("ci_admin_credentials_migration")
            .defaultSchema("ci_admin_credentials_migration")
            .createSchemas(true)
            .locations("classpath:db/migration")
            .load();

        var result = flyway.migrate();
        assertTrue(result.migrationsExecuted >= 15,
            "Expected Flyway V1 through V15 to execute in a fresh PostgreSQL schema");
        flyway.validate();
    }
}
