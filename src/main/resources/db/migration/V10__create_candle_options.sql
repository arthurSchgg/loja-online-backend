CREATE TABLE candle_options (
 id BIGSERIAL PRIMARY KEY,
 offering_id BIGINT NOT NULL REFERENCES service_offerings(id) ON DELETE CASCADE,
 kind VARCHAR(20) NOT NULL CHECK (kind IN ('AROMA','COLOR','PACKAGING')),
 option_value VARCHAR(100) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 CONSTRAINT uq_candle_option UNIQUE(offering_id,kind,option_value)
);
CREATE INDEX idx_candle_options_offering ON candle_options(offering_id,kind,active);
