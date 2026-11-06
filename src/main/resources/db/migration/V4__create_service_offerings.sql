CREATE TABLE service_offerings (
 id BIGSERIAL PRIMARY KEY,
 title VARCHAR(180) NOT NULL,
 description VARCHAR(4000),
 type VARCHAR(30) NOT NULL CHECK (type IN ('CANDLE','WORKSHOP','RENTAL')),
 price NUMERIC(12,2) CHECK(price IS NULL OR price >= 0),
 image_url VARCHAR(2048),
 published BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_service_offerings_visibility ON service_offerings(type,published,id);
