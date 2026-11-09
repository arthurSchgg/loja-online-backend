CREATE TABLE product_promotions (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
  promotional_price NUMERIC(12,2) NOT NULL CHECK (promotional_price > 0),
  starts_at TIMESTAMPTZ NOT NULL,
  ends_at TIMESTAMPTZ NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_promotion_time_range CHECK (ends_at > starts_at)
);
CREATE INDEX idx_product_promotions_lookup ON product_promotions(product_id, starts_at, ends_at);
