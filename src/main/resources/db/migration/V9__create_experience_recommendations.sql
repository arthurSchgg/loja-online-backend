CREATE TABLE experience_recommendations (
    id BIGSERIAL PRIMARY KEY,
    result_id BIGINT NOT NULL REFERENCES experience_results(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    position INTEGER NOT NULL CHECK (position >= 0 AND position < 3),
    UNIQUE(result_id, product_id),
    UNIQUE(result_id, position)
);
CREATE INDEX idx_experience_recommendations_result ON experience_recommendations(result_id, position);
