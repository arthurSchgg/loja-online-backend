package br.com.lojinhadakatia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Check every authenticated session against the persisted password version. */
public class AdminSessionVersionFilter extends OncePerRequestFilter {
    public static final String VERSION_ATTRIBUTE = "ADMIN_CREDENTIAL_VERSION";
    private final AdminCredentialService credentials;

    public AdminSessionVersionFilter(AdminCredentialService credentials) {
        this.credentials = credentials;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (session != null && auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            Object recorded = session.getAttribute(VERSION_ATTRIBUTE);
            // A session without a version (e.g. created before the V13 rollout)
            // must not bypass revocation after a password or email change.
            // New logins always set this attribute in the authentication handler.
            if (!(recorded instanceof Long version)
                    || version != credentials.credentialVersion(auth.getName())) {
                session.invalidate();
                SecurityContextHolder.clearContext();
                response.sendError(401, "Sessão expirada após alteração das credenciais.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
