package br.com.lojinhadakatia;

import java.sql.DriverManager;
import java.math.BigDecimal;
import java.util.UUID;
import br.com.lojinhadakatia.catalog.service.CatalogPriceStorage;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
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
        var ds=new DriverManagerDataSource(url+(url.contains("?")?"&":"?")+"currentSchema="+schema,user,password);
        var jdbc=new JdbcTemplate(ds);
        var tx=new TransactionTemplate(new DataSourceTransactionManager(ds));
        var storage=new CatalogPriceStorage(jdbc);
        assertEquals(BigDecimal.ZERO,tx.execute(s->storage.toStoredPrice(null)));
        assertEquals(new BigDecimal("29.90"),tx.execute(s->storage.toStoredPrice(new BigDecimal("29.90"))));
        try(var connection=DriverManager.getConnection(url,user,password);
            var statement=connection.createStatement()){
            statement.executeUpdate("INSERT INTO "+schema+".brands(id,name) VALUES (1,'Marca fictícia')");
            statement.executeUpdate("INSERT INTO "+schema+".products(name,price,brand_id,available,published,stock_quantity) VALUES "+
                "('Pendente',0,1,TRUE,TRUE,7),('Confirmado',29.90,1,TRUE,TRUE,4)");
            statement.executeUpdate("INSERT INTO "+schema+".service_offerings(title,type,image_url,published,available) VALUES "+
                "('Vela anterior','CANDLE','https://example.invalid/old.jpg',TRUE,TRUE)");
        }
        var upgraded=Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
            .locations("classpath:db/migration").load();
        assertEquals(3,upgraded.migrate().migrationsExecuted);
        upgraded.validate();
        assertNull(tx.execute(s->storage.toStoredPrice(null)));
        assertEquals("https://example.invalid/old.jpg",jdbc.queryForObject(
            "SELECT image_url FROM offering_images WHERE position=0",String.class));
        // A rollback artifact with the bridge target must still validate a
        // database after V18/V19, without undoing the migrations.
        var bridge=Flyway.configure().dataSource(url,user,password).schemas(schema).defaultSchema(schema)
            .locations("classpath:db/migration").target(MigrationVersion.fromVersion("17")).load();
        assertDoesNotThrow(bridge::validate);
        assertEquals(0,bridge.migrate().migrationsExecuted);
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
