package br.com.lojinhadakatia;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that later security migrations do not overwrite the administrator
 * account or existing outbox data. Uses an isolated PostgreSQL CI schema.
 */
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_INTEGRATION", matches = "true")
class PostgreSqlExistingDataUpgradeIntegrationTest {

    private static final String SCHEMA = "ci_security_existing_data_upgrade";

    @Test
    void v13AndV14DataSurviveUpgradeToV15() throws Exception {
        String url = System.getenv("POSTGRES_TEST_URL");
        String username = System.getenv("POSTGRES_TEST_USER");
        String password = System.getenv("POSTGRES_TEST_PASSWORD");
        assertNotNull(url);
        assertTrue(url.startsWith("jdbc:postgresql:"));

        Flyway baseline = flyway(url, username, password, "13");
        baseline.migrate();
        baseline.validate();

        try (var connection = DriverManager.getConnection(url, username, password);
             var insert = connection.prepareStatement(
                "INSERT INTO " + SCHEMA + ".admin_credentials " +
                "(id, username, email, password_hash, credential_version) " +
                "VALUES (1, ?, ?, ?, ?)")) {
            insert.setString(1, "previous-admin");
            insert.setString(2, "unchanged@example.invalid");
            insert.setString(3, "{pbkdf2}persisted-test-hash");
            insert.setLong(4, 7L);
            assertEquals(1, insert.executeUpdate());
        }

        Flyway recovery = flyway(url, username, password, "14");
        recovery.migrate();
        recovery.validate();

        try (var connection = DriverManager.getConnection(url, username, password);
             var insert = connection.prepareStatement(
                "INSERT INTO " + SCHEMA + ".admin_email_outbox " +
                "(recipient, subject, encrypted_body, attempts) VALUES (?, ?, ?, ?)")) {
            insert.setString(1, "existing@example.invalid");
            insert.setString(2, "Pending notification");
            insert.setString(3, "encrypted-placeholder-with-no-real-token");
            insert.setInt(4, 2);
            assertEquals(1, insert.executeUpdate());
        }

        Flyway upgraded = flyway(url, username, password, null);
        var result = upgraded.migrate();
        upgraded.validate();
        assertTrue(result.migrationsExecuted >= 1,
            "An upgrade from V14 must apply the next pending migration.");

        try (var connection = DriverManager.getConnection(url, username, password);
             var admin = connection.createStatement().executeQuery(
                "SELECT username, email, password_hash, credential_version " +
                "FROM " + SCHEMA + ".admin_credentials WHERE id = 1")) {
            assertTrue(admin.next());
            assertEquals("previous-admin", admin.getString(1));
            assertEquals("unchanged@example.invalid", admin.getString(2));
            assertEquals("{pbkdf2}persisted-test-hash", admin.getString(3));
            assertEquals(7L, admin.getLong(4));
            assertFalse(admin.next());
        }

        try (var connection = DriverManager.getConnection(url, username, password);
             var outbox = connection.createStatement().executeQuery(
                "SELECT recipient, encrypted_body, attempts, sent_at " +
                "FROM " + SCHEMA + ".admin_email_outbox WHERE subject = 'Pending notification'")) {
            assertTrue(outbox.next());
            assertEquals("existing@example.invalid", outbox.getString(1));
            assertEquals("encrypted-placeholder-with-no-real-token", outbox.getString(2));
            assertEquals(2, outbox.getInt(3));
            assertNull(outbox.getTimestamp(4));
        }

        try (var connection = DriverManager.getConnection(url, username, password);
             var tables = connection.createStatement().executeQuery(
                "SELECT count(*) FROM information_schema.tables " +
                "WHERE table_schema = '" + SCHEMA + "' " +
                "AND table_name = 'admin_email_change_tokens'")) {
            assertTrue(tables.next());
            assertEquals(1, tables.getInt(1), "V15 must have created email change tokens.");
        }
    }

    private static Flyway flyway(String url, String user, String password, String target) {
        var configuration = Flyway.configure()
            .dataSource(url, user, password)
            .schemas(SCHEMA)
            .defaultSchema(SCHEMA)
            .createSchemas(true)
            .locations("classpath:db/migration");
        if (target != null) configuration.target(MigrationVersion.fromVersion(target));
        return configuration.load();
    }
}
