package br.com.lojinhadakatia.security.audit;

import jakarta.persistence.*;
import java.time.Instant;

/** Immutable database snapshot of an administrative HTTP write, without request payloads. */
@Entity
@Table(name = "administrative_audit_events")
public class AdminAuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120, updatable = false)
    private String actor;
    @Column(name = "http_method", nullable = false, length = 12, updatable = false)
    private String method;
    @Column(name = "route_pattern", nullable = false, length = 255, updatable = false)
    private String routePattern;
    @Column(name = "resource_id", length = 64, updatable = false)
    private String resourceId;
    @Column(name = "http_status", nullable = false, updatable = false)
    private int status;
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    protected AdminAuditEvent() {}

    public AdminAuditEvent(String actor, String method, String routePattern,
                           String resourceId, int status, Instant occurredAt) {
        this.actor = actor;
        this.method = method;
        this.routePattern = routePattern;
        this.resourceId = resourceId;
        this.status = status;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getMethod() { return method; }
    public String getRoutePattern() { return routePattern; }
    public String getResourceId() { return resourceId; }
    public int getStatus() { return status; }
    public Instant getOccurredAt() { return occurredAt; }
}
