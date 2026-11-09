ALTER TABLE products
    ADD COLUMN archived BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_products_public_archive
    ON products(published, archived, id);
