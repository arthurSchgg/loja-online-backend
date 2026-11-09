package br.com.lojinhadakatia.security.audit;

import java.time.Instant;

/** Public only to the authenticated administrator through the protected admin endpoint. */
public record AdminAuditEventResponse(
    Long id, String actor, String method, String routePattern, String resourceId,
    int status, Instant occurredAt
) {
    public static AdminAuditEventResponse from(AdminAuditEvent e) {
        return new AdminAuditEventResponse(e.getId(), e.getActor(), e.getMethod(),
            e.getRoutePattern(), e.getResourceId(), e.getStatus(), e.getOccurredAt());
    }
}
