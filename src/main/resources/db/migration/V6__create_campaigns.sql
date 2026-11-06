CREATE TABLE campaigns (
 id BIGSERIAL PRIMARY KEY,
 title VARCHAR(180) NOT NULL,
 subtitle VARCHAR(600),
 image_url VARCHAR(2048),
 link_url VARCHAR(2048),
 position INTEGER NOT NULL DEFAULT 0 CHECK(position >= 0),
 published BOOLEAN NOT NULL DEFAULT FALSE,
 starts_at TIMESTAMPTZ,
 ends_at TIMESTAMPTZ
);
CREATE INDEX idx_campaigns_visible ON campaigns(published, position);
