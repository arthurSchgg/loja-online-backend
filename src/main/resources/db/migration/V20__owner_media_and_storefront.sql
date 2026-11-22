-- Additive release: existing product, store, image, account and stock data are preserved.
CREATE TABLE media_assets (
  id UUID PRIMARY KEY,
  sha256 VARCHAR(64) NOT NULL UNIQUE,
  content_type VARCHAR(20) NOT NULL CHECK (content_type IN ('image/jpeg','image/png')),
  byte_length INTEGER NOT NULL CHECK (byte_length BETWEEN 1 AND 786432),
  width INTEGER NOT NULL CHECK (width BETWEEN 1 AND 1600),
  height INTEGER NOT NULL CHECK (height BETWEEN 1 AND 1600),
  payload BYTEA NOT NULL CHECK (octet_length(payload)=byte_length),
  created_at TIMESTAMPTZ NOT NULL
);
-- Serialize allocation and removal across overlapping deployment instances.
CREATE TABLE media_capacity (id INTEGER PRIMARY KEY CHECK (id=1));
INSERT INTO media_capacity(id) VALUES (1);
CREATE TABLE offering_images (
  id BIGSERIAL PRIMARY KEY,
  offering_id BIGINT NOT NULL REFERENCES service_offerings(id) ON DELETE CASCADE,
  image_url VARCHAR(2048) NOT NULL,
  position INTEGER NOT NULL CHECK(position BETWEEN 0 AND 11),
  alt_text VARCHAR(180)
);
CREATE INDEX idx_offering_images_order ON offering_images(offering_id, position);
INSERT INTO offering_images(offering_id,image_url,position,alt_text)
  SELECT id,image_url,0,NULL FROM service_offerings WHERE image_url IS NOT NULL AND trim(image_url)<>'';
CREATE INDEX idx_product_images_url ON product_images(image_url);
CREATE INDEX idx_offering_images_url ON offering_images(image_url);
CREATE TABLE storefront_settings (
  id INTEGER PRIMARY KEY CHECK(id=1),
  featured_customized BOOLEAN NOT NULL DEFAULT FALSE
);
INSERT INTO storefront_settings(id,featured_customized) VALUES (1,FALSE);
CREATE TABLE featured_products (
  product_id BIGINT PRIMARY KEY REFERENCES products(id) ON DELETE CASCADE,
  position INTEGER NOT NULL UNIQUE CHECK(position BETWEEN 0 AND 7)
);
CREATE TABLE whatsapp_message_templates (
  kind VARCHAR(20) PRIMARY KEY CHECK(kind IN ('PRODUCT','CANDLE','WORKSHOP','RENTAL')),
  template VARCHAR(1200) NOT NULL
);
