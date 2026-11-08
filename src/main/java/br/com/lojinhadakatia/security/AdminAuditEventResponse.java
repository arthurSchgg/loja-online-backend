package br.com.lojinhadakatia.security;

import java.time.Instant;

public record AdminAuditEventResponse(Long id, String actor, String method,
    String route, String resourceId, int httpStatus, Instant occurredAt) {
    public static AdminAuditEventResponse from(AdminAuditEvent e){
        return new AdminAuditEventResponse(e.getId(), e.getActor(), e.getMethod(),
            e.getRoute(), e.getResourceId(), e.getHttpStatus(), e.getOccurredAt());
    }
}
