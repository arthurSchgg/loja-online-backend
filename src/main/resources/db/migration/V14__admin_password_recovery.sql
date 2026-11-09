CREATE TABLE admin_password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ
);
CREATE INDEX idx_admin_password_reset_expires ON admin_password_reset_tokens(expires_at);

CREATE TABLE admin_email_outbox (
    id BIGSERIAL PRIMARY KEY,
    recipient VARCHAR(254) NOT NULL,
    subject VARCHAR(180) NOT NULL,
    encrypted_body VARCHAR(8192) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_admin_email_outbox_pending ON admin_email_outbox(next_attempt_at, id) WHERE sent_at IS NULL;
