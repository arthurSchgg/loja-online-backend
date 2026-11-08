package br.com.lojinhadakatia.security;

import br.com.lojinhadakatia.security.audit.AdminAuditEventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Persists administrative writes using the matched route pattern, never a raw URL or payload.
 * Failures remain visible in logs; a failure after completion cannot alter an HTTP response.
 */
@Component
public class AdminAuditInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuditInterceptor.class);
    private final AdminAuditEventService audit;

    public AdminAuditInterceptor(AdminAuditEventService audit) {
        this.audit = audit;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if ("GET".equalsIgnoreCase(request.getMethod()) || "HEAD".equalsIgnoreCase(request.getMethod())
            || "OPTIONS".equalsIgnoreCase(request.getMethod())) return;

        String actor = request.getUserPrincipal() == null ? "unknown" : request.getUserPrincipal().getName();
        if (actor.length() > 120) actor = actor.substring(0, 120);
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String route = pattern == null ? "unknown" : pattern.toString();
        if (route.length() > 255) route = route.substring(0, 255);
        String resourceId = null;
        Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (variables instanceof Map<?, ?> pathVariables) {
            for (String key : new String[] {"id", "productId", "offeringId", "experienceId", "promotionId"}) {
                Object value = pathVariables.get(key);
                if (value != null && value.toString().matches("[0-9]{1,64}")) {
                    resourceId = value.toString();
                    break;
                }
            }
        }

        try {
            audit.record(actor, request.getMethod(), route, resourceId, response.getStatus());
        } catch (RuntimeException failure) {
            LOGGER.error("admin_audit_persistence_failed method={} route={} http_status={}",
                request.getMethod(), route, response.getStatus(), failure);
        }
        LOGGER.info("admin_write actor={} method={} route={} http_status={}",
            actor, request.getMethod(), route, response.getStatus());
    }
}
