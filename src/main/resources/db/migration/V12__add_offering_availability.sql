ALTER TABLE service_offerings
    ADD COLUMN available BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_service_offerings_public_availability
    ON service_offerings(published, available, type, id);
