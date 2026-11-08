package br.com.lojinhadakatia.security;

import java.time.Instant;

/** Restricted administrative view without HTTP bodies, parameters or credentials. */
public record AdminAuditResponse(Long id, String actor, String httpMethod,
                                 String routePattern, String resourcePath,
                                 int httpStatus, Instant createdAt) {
    static AdminAuditResponse from(AdminAuditEvent e) {
        return new AdminAuditResponse(e.getId(), e.getActor(), e.getHttpMethod(),
            e.getRoutePattern(), e.getResourcePath(), e.getHttpStatus(), e.getCreatedAt());
    }
}
