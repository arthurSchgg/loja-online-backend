-- A promotion is never physically removed once recorded.
-- Closure metadata preserves the original agreed period and price for audit.
ALTER TABLE product_promotions
  ADD COLUMN closed_at TIMESTAMPTZ,
  ADD COLUMN closed_reason VARCHAR(500),
  ADD COLUMN closed_by VARCHAR(180);

ALTER TABLE product_promotions
  ADD CONSTRAINT chk_promotion_closed_metadata CHECK (
    (closed_at IS NULL AND closed_reason IS NULL AND closed_by IS NULL)
    OR (closed_at IS NOT NULL AND closed_reason IS NOT NULL AND length(btrim(closed_reason)) > 0 AND closed_by IS NOT NULL AND length(btrim(closed_by)) > 0)
  );
CREATE INDEX idx_product_promotions_active ON product_promotions(product_id, starts_at, ends_at) WHERE closed_at IS NULL;
