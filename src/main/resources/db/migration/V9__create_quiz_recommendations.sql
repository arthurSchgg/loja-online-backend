CREATE TABLE experience_product_recommendations (
  id BIGSERIAL PRIMARY KEY,
  experience_id BIGINT NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
  result_key VARCHAR(80) NOT NULL,
  product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
  position INTEGER NOT NULL CHECK (position >= 0),
  UNIQUE (experience_id, result_key, product_id)
);
CREATE INDEX idx_quiz_recommendations_result
  ON experience_product_recommendations(experience_id, result_key, position);
