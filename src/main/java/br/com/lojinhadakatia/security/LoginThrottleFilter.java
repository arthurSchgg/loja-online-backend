package br.com.lojinhadakatia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/** Refuses repeated authentication attempts before password verification. */
public class LoginThrottleFilter extends OncePerRequestFilter {
    private final LoginAttemptLimiter limiter;

    public LoginThrottleFilter(LoginAttemptLimiter limiter) { this.limiter = limiter; }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && path.startsWith(context))
            path = path.substring(context.length());
        return !"POST".equalsIgnoreCase(request.getMethod())
            || !"/api/v1/auth/login".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long seconds = limiter.retryAfterSeconds(
            request.getParameter("username"), request.getRemoteAddr());
        if (seconds > 0) {
            response.setHeader("Retry-After", Long.toString(seconds));
            response.sendError(429, "Muitas tentativas. Tente novamente mais tarde.");
            return;
        }
        chain.doFilter(request, response);
    }
}
