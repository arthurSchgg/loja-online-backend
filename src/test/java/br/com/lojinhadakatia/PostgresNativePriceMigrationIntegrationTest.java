package br.com.lojinhadakatia;

import java.sql.DriverManager;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

/** Exercise the V17 -> V19 upgrade on disposable PostgreSQL data. */
@EnabledIfEnvironmentVariable(named="RUN_POSTGRES_INTEGRATION",matches="true")
class PostgresNativePriceMigrationIntegrationTest {
    @Test void convertsOnlyZeroSentinelsAndPreservesCatalog() throws Exception {
        String url=System.getenv("POSTGRES_TEST_URL"),user=System.getenv("POSTGRES_TEST_USER"),
            password=System.getenv("POSTGRES_TEST_PASSWORD");
        assertNotNull(url);
        assertTrue(url.startsWith("jdbc:postgresql:"));
        String schema="ci_price_"+UUID.randomUUID().toString().replace("-","");
        Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
            .locations("classpath:db/migration").target(MigrationVersion.fromVersion("17")).load().migrate();
        try(var connection=DriverManager.getConnection(url,user,password);
            var statement=connection.createStatement()){
            statement.executeUpdate("INSERT INTO "+schema+".brands(id,name) VALUES (1,'Marca fictícia')");
            statement.executeUpdate("INSERT INTO "+schema+".products(name,price,brand_id,available,published,stock_quantity) VALUES "+
                "('Pendente',0,1,TRUE,TRUE,7),('Confirmado',29.90,1,TRUE,TRUE,4)");
        }
        var upgraded=Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
            .locations("classpath:db/migration").load();
        assertEquals(2,upgraded.migrate().migrationsExecuted);
        upgraded.validate();
        try(var connection=DriverManager.getConnection(url,user,password);
            var statement=connection.createStatement();
            var rows=statement.executeQuery("SELECT name,price,available,published,stock_quantity FROM "+
                schema+".products ORDER BY name DESC")){
            assertTrue(rows.next());assertEquals("Pendente",rows.getString(1));assertNull(rows.getBigDecimal(2));
            assertTrue(rows.getBoolean(3));assertTrue(rows.getBoolean(4));assertEquals(7,rows.getInt(5));
            assertTrue(rows.next());assertEquals("Confirmado",rows.getString(1));
            assertEquals(0,new java.math.BigDecimal("29.90").compareTo(rows.getBigDecimal(2)));
            assertEquals(4,rows.getInt(5));assertFalse(rows.next());
        }
        try(var connection=DriverManager.getConnection(url,user,password);
            var statement=connection.createStatement()){
            assertThrows(java.sql.SQLException.class,()->statement.executeUpdate("UPDATE "+schema+
                ".products SET price=0 WHERE name='Confirmado'"));
        }
    }
}
