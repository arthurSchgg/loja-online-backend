package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

/** Persisted, metadata-only event; request bodies, cookies and tokens are never stored. */
@Entity
@Table(name = "admin_audit_events")
public class AdminAuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 180) private String actor;
    @Column(nullable = false, length = 10) private String method;
    @Column(nullable = false, length = 300) private String route;
    @Column(name = "resource_id", length = 80) private String resourceId;
    @Column(name = "http_status", nullable = false) private int httpStatus;
    @Column(name = "occurred_at", nullable = false, updatable = false) private Instant occurredAt;

    protected AdminAuditEvent() {}
    public AdminAuditEvent(String actor, String method, String route, String resourceId, int httpStatus) {
        this.actor = actor; this.method = method; this.route = route;
        this.resourceId = resourceId; this.httpStatus = httpStatus;
    }
    @PrePersist void created(){ occurredAt = Instant.now(); }
    public Long getId(){return id;}
    public String getActor(){return actor;}
    public String getMethod(){return method;}
    public String getRoute(){return route;}
    public String getResourceId(){return resourceId;}
    public int getHttpStatus(){return httpStatus;}
    public Instant getOccurredAt(){return occurredAt;}
}
