package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

/** Immutable record of an administrative HTTP write attempt; never stores payloads or tokens. */
@Entity
@Table(name = "admin_audit_events")
public class AdminAuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180, updatable = false)
    private String actor;

    @Column(name = "http_method", nullable = false, length = 12, updatable = false)
    private String httpMethod;

    @Column(name = "route_pattern", nullable = false, length = 300, updatable = false)
    private String routePattern;

    @Column(name = "resource_path", nullable = false, length = 300, updatable = false)
    private String resourcePath;

    @Column(name = "http_status", nullable = false, updatable = false)
    private int httpStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AdminAuditEvent() {}

    public AdminAuditEvent(String actor, String httpMethod, String routePattern,
                           String resourcePath, int httpStatus) {
        this.actor = actor;
        this.httpMethod = httpMethod;
        this.routePattern = routePattern;
        this.resourcePath = resourcePath;
        this.httpStatus = httpStatus;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getHttpMethod() { return httpMethod; }
    public String getRoutePattern() { return routePattern; }
    public String getResourcePath() { return resourcePath; }
    public int getHttpStatus() { return httpStatus; }
    public Instant getCreatedAt() { return createdAt; }
}
