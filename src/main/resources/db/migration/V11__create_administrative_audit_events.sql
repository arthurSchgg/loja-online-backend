CREATE TABLE administrative_audit_events (
  id BIGSERIAL PRIMARY KEY,
  actor VARCHAR(120) NOT NULL,
  http_method VARCHAR(12) NOT NULL,
  route_pattern VARCHAR(255) NOT NULL,
  resource_id VARCHAR(64),
  http_status INTEGER NOT NULL CHECK (http_status BETWEEN 100 AND 599),
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_administrative_audit_events_time ON administrative_audit_events (occurred_at DESC, id DESC);
CREATE INDEX idx_administrative_audit_events_actor_time ON administrative_audit_events (actor, occurred_at DESC);
CREATE INDEX idx_administrative_audit_events_route_time ON administrative_audit_events (route_pattern, occurred_at DESC);
