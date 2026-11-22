-- A technical zero was used by catalog imports to represent an unconfirmed
-- price. Convert only that sentinel, preserving names, stock and publication.
-- The old application contract is not compatible: homologate restore/rollback
-- before deploying this migration.
ALTER TABLE products ALTER COLUMN price DROP NOT NULL;
UPDATE products SET price = NULL WHERE price = 0;
ALTER TABLE products ADD CONSTRAINT chk_products_confirmed_price
    CHECK (price IS NULL OR price > 0);
