-- The primary key covers product-first joins; public category filtering also
-- needs the reverse lookup for larger catalogs and related products.
CREATE INDEX IF NOT EXISTS idx_product_categories_category_product
    ON product_categories (category_id, product_id);
