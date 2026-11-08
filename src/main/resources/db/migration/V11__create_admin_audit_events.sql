-- Audit only administrative write requests. Never persist request payload, query or secrets.
CREATE TABLE admin_audit_events (
    id BIGSERIAL PRIMARY KEY,
    actor VARCHAR(180) NOT NULL,
    http_method VARCHAR(12) NOT NULL,
    route_pattern VARCHAR(300) NOT NULL,
    resource_path VARCHAR(300) NOT NULL,
    http_status INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_admin_audit_events_created_at ON admin_audit_events(created_at DESC, id DESC);
CREATE INDEX idx_admin_audit_events_actor ON admin_audit_events(actor, created_at DESC);
