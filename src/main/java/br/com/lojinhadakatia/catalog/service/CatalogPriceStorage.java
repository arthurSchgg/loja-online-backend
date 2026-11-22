package br.com.lojinhadakatia.catalog.service;

import java.math.BigDecimal;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The release bridge must be able to write pending prices on either side of
 * V18. A zero is only a database representation on V17, never a price exposed
 * to an API consumer. Once the column is nullable, persist NULL exclusively.
 */
@Component
public class CatalogPriceStorage {
    private final JdbcTemplate jdbc;

    public CatalogPriceStorage(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional(propagation=Propagation.MANDATORY)
    public BigDecimal toStoredPrice(BigDecimal confirmedOrNull) {
        if (confirmedOrNull != null) return confirmedOrNull;
        return jdbc.execute((ConnectionCallback<BigDecimal>) connection -> {
            try (var statement = connection.createStatement()) {
                // PostgreSQL holds this lock through the enclosing product
                // transaction. V18's ALTER TABLE cannot race a legacy write.
                if ("PostgreSQL".equals(connection.getMetaData().getDatabaseProductName()))
                    statement.execute("LOCK TABLE products IN ROW EXCLUSIVE MODE");
                try (var rows = statement.executeQuery("""
                    SELECT is_nullable FROM information_schema.columns
                    WHERE table_schema = CURRENT_SCHEMA
                      AND lower(table_name) = 'products'
                      AND lower(column_name) = 'price'
                    """)) {
                    if (!rows.next()) throw new IllegalStateException("Coluna de preço indisponível.");
                    String nullable = rows.getString(1);
                    if (rows.next() || !("YES".equals(nullable) || "NO".equals(nullable)))
                        throw new IllegalStateException("Esquema de preços inesperado.");
                    return "YES".equals(nullable) ? null : BigDecimal.ZERO;
                }
            }
        });
    }
}
