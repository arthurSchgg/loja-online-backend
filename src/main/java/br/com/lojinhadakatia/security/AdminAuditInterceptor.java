package br.com.lojinhadakatia.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Stores an append-only administrative write audit record after completion.
 * Request bodies, query strings, cookies, secrets and CSRF tokens are never stored.
 */
@Component
public class AdminAuditInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuditInterceptor.class);
    private final AdminAuditWriter writer;

    public AdminAuditInterceptor(AdminAuditWriter writer) {
        this.writer = writer;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method)) return;

        String actor = request.getUserPrincipal() == null
            ? "unknown" : request.getUserPrincipal().getName();
        Object routeAttribute = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String route = routeAttribute == null ? "unknown" : routeAttribute.toString();
        // Never persist raw URI segments, which might contain future user data.
        // Only numeric path variables are recorded; request bodies/queries are discarded.
        String path = route;
        Object variables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (variables instanceof java.util.Map<?, ?> values) {
            String ids = values.entrySet().stream()
                .filter(entry -> entry.getValue() != null &&
                    entry.getValue().toString().matches("[0-9]{1,18}"))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .sorted()
                .collect(java.util.stream.Collectors.joining(","));
            if (!ids.isBlank()) path = route + " [" + ids + "]";
        }
        int status = response.getStatus();

        try {
            writer.append(actor, method, route, path, status);
        } catch (RuntimeException error) {
            // Audit storage should not disclose persistence details to visitors.
            // The underlying exception is logged for operational investigation.
            LOGGER.error("admin_audit_persistence_failed method={} route={} http_status={}",
                method, route, status, error);
        }

        LOGGER.info("admin_write actor={} method={} route={} http_status={}",
            actor, method, route, status);
    }
}
