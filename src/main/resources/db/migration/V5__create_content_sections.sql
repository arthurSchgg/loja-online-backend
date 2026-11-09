CREATE TABLE content_sections (
 id BIGSERIAL PRIMARY KEY,
 slug VARCHAR(80) NOT NULL UNIQUE,
 title VARCHAR(180) NOT NULL,
 body VARCHAR(8000),
 image_url VARCHAR(2048),
 position INTEGER NOT NULL DEFAULT 0 CHECK(position >= 0),
 published BOOLEAN NOT NULL DEFAULT FALSE,
 updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_content_sections_published ON content_sections(published, position);
