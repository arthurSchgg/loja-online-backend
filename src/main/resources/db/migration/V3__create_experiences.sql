CREATE TABLE experiences (
 id BIGSERIAL PRIMARY KEY,
 title VARCHAR(180) NOT NULL,
 description VARCHAR(2000),
 published BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE experience_results (
 id BIGSERIAL PRIMARY KEY,
 experience_id BIGINT NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
 result_key VARCHAR(80) NOT NULL,
 title VARCHAR(180) NOT NULL,
 description VARCHAR(2000),
 UNIQUE(experience_id, result_key)
);
CREATE TABLE experience_questions (
 id BIGSERIAL PRIMARY KEY,
 experience_id BIGINT NOT NULL REFERENCES experiences(id) ON DELETE CASCADE,
 title VARCHAR(350) NOT NULL,
 position INTEGER NOT NULL CHECK(position >= 0)
);
CREATE TABLE experience_options (
 id BIGSERIAL PRIMARY KEY,
 question_id BIGINT NOT NULL REFERENCES experience_questions(id) ON DELETE CASCADE,
 label VARCHAR(300) NOT NULL,
 position INTEGER NOT NULL CHECK(position >= 0),
 result_key VARCHAR(80) NOT NULL
);
CREATE INDEX idx_experience_published ON experiences(published);
CREATE INDEX idx_questions_experience ON experience_questions(experience_id, position);
CREATE INDEX idx_options_question ON experience_options(question_id,position);
