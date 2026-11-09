CREATE TABLE offering_options (
 id BIGSERIAL PRIMARY KEY,
 offering_id BIGINT NOT NULL REFERENCES service_offerings(id) ON DELETE CASCADE,
 kind VARCHAR(20) NOT NULL CHECK (kind IN ('AROMA', 'COLOR', 'PACKAGING')),
 label VARCHAR(120) NOT NULL,
 position INTEGER NOT NULL DEFAULT 0 CHECK (position >= 0),
 active BOOLEAN NOT NULL DEFAULT TRUE,
 UNIQUE (offering_id, kind, label)
);
CREATE INDEX idx_offering_options_visible ON offering_options(offering_id, active, position);
