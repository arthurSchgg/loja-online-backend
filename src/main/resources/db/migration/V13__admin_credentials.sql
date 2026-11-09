CREATE TABLE admin_credentials (
  id BIGINT PRIMARY KEY CHECK (id = 1),
  username VARCHAR(120) NOT NULL UNIQUE,
  email VARCHAR(254),
  password_hash VARCHAR(255) NOT NULL,
  credential_version BIGINT NOT NULL DEFAULT 1,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uq_admin_credentials_email ON admin_credentials(lower(email));
