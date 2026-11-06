CREATE TABLE store_information (
    id BIGINT PRIMARY KEY,
    display_name VARCHAR(180) NOT NULL,
    slogan VARCHAR(240),
    description VARCHAR(4000),
    whatsapp VARCHAR(20),
    instagram_url VARCHAR(2048),
    email VARCHAR(254),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
