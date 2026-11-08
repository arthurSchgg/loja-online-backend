package br.com.lojinhadakatia.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/** Audits write metadata without logging payloads, query strings or credentials. */
@Component
public class AdminAuditInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuditInterceptor.class);
    private final AdminAuditEventService events;

    public AdminAuditInterceptor(AdminAuditEventService events) {this.events = events;}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if ("GET".equalsIgnoreCase(request.getMethod()) ||
            "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
            "HEAD".equalsIgnoreCase(request.getMethod())) return;

        String actor = request.getUserPrincipal() == null
            ? "unknown" : request.getUserPrincipal().getName();
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String route = pattern == null ? "unknown" : pattern.toString();

        String resourceId = null;
        Object templateValues = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (templateValues instanceof Map<?, ?> values) {
            for (String key : new String[]{"id", "productId", "offeringId", "promotionId", "movementId"}) {
                Object value = values.get(key);
                if (value != null && String.valueOf(value).matches("[0-9]{1,18}")) {
                    resourceId = String.valueOf(value); break;
                }
            }
        }
        actor = actor.length() > 180 ? actor.substring(0, 180) : actor;
        route = route.length() > 300 ? route.substring(0, 300) : route;
        try {
            events.record(actor, request.getMethod(), route, resourceId, response.getStatus());
        } catch (RuntimeException e) {
            // Response has already been committed. Ensure audit failures are observable.
            LOGGER.error("admin_audit_persistence_failed route={} status={}", route, response.getStatus(), e);
        }
        LOGGER.info("admin_write actor={} method={} route={} http_status={}",
            actor, request.getMethod(), route, response.getStatus());
    }
}
