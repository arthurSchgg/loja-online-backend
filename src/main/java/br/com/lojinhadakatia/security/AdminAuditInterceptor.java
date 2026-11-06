package br.com.lojinhadakatia.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Auditoria operacional mínima de requisições administrativas de escrita.
 *
 * Não inclui payloads, senhas, tokens, parâmetros de consulta ou dados privados.
 * Registros persistentes, retenção e monitoramento devem ser definidos no deploy.
 */
@Component
public class AdminAuditInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuditInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if ("GET".equalsIgnoreCase(request.getMethod()) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return;
        }
        String actor = request.getUserPrincipal() == null
            ? "unknown"
            : request.getUserPrincipal().getName();
        Object routeAttribute = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String route = routeAttribute == null ? "unknown" : routeAttribute.toString();
        LOGGER.info("admin_write actor={} method={} route={} http_status={}",
            actor, request.getMethod(), route, response.getStatus());
    }
}
