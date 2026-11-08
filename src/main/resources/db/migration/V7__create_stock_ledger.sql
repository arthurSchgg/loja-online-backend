ALTER TABLE products
  ADD COLUMN stock_quantity INTEGER NOT NULL DEFAULT 0
  CONSTRAINT chk_product_stock_quantity_nonnegative CHECK (stock_quantity >= 0);

CREATE TABLE product_stock_movements (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
  movement_type VARCHAR(16) NOT NULL CHECK (movement_type IN ('ENTRY', 'EXIT', 'ADJUSTMENT')),
  quantity INTEGER NOT NULL CHECK (quantity >= 0),
  resulting_quantity INTEGER NOT NULL CHECK (resulting_quantity >= 0),
  reason VARCHAR(500) NOT NULL,
  actor VARCHAR(180) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_stock_movements_product_time ON product_stock_movements(product_id, id DESC);
