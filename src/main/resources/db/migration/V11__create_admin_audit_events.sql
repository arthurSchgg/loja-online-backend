CREATE TABLE admin_audit_events (
 id BIGSERIAL PRIMARY KEY,
 actor VARCHAR(180) NOT NULL,
 method VARCHAR(10) NOT NULL,
 route VARCHAR(300) NOT NULL,
 resource_id VARCHAR(80),
 http_status INTEGER NOT NULL CHECK (http_status >= 100 AND http_status <= 599),
 occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_admin_audit_events_occurrence ON admin_audit_events(occurred_at DESC, id DESC);
CREATE INDEX idx_admin_audit_events_actor ON admin_audit_events(actor, id DESC);
