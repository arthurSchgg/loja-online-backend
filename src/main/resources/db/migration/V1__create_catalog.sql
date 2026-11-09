CREATE TABLE brands (id BIGSERIAL PRIMARY KEY, name VARCHAR(120) NOT NULL UNIQUE, active BOOLEAN NOT NULL DEFAULT TRUE);
CREATE TABLE categories (id BIGSERIAL PRIMARY KEY, name VARCHAR(120) NOT NULL UNIQUE, active BOOLEAN NOT NULL DEFAULT TRUE);
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(180) NOT NULL,
  description VARCHAR(4000),
  price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
  available BOOLEAN NOT NULL DEFAULT TRUE,
  published BOOLEAN NOT NULL DEFAULT FALSE,
  brand_id BIGINT NOT NULL REFERENCES brands(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE product_categories (
  product_id BIGINT NOT NULL REFERENCES products(id),
  category_id BIGINT NOT NULL REFERENCES categories(id),
  PRIMARY KEY (product_id, category_id)
);
CREATE TABLE product_images (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  image_url VARCHAR(2048) NOT NULL,
  position INTEGER NOT NULL CHECK (position >= 0),
  alt_text VARCHAR(180)
);
CREATE INDEX idx_products_published ON products (published, id);
CREATE INDEX idx_products_brand ON products (brand_id);
CREATE INDEX idx_product_images_order ON product_images (product_id, position);
