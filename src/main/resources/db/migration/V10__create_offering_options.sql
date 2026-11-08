CREATE TABLE offering_options (
 id BIGSERIAL PRIMARY KEY,
 offering_id BIGINT NOT NULL REFERENCES service_offerings(id) ON DELETE CASCADE,
 kind VARCHAR(16) NOT NULL CHECK (kind IN ('AROMA','COLOR','PACKAGING')),
 value VARCHAR(120) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 UNIQUE(offering_id, kind, value)
);
CREATE INDEX idx_offering_options_offering ON offering_options(offering_id,kind,id);
