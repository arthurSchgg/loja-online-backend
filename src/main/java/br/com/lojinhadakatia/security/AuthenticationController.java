package br.com.lojinhadakatia.security;

import java.security.Principal;
import java.util.Map;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrato de autenticação destinado ao painel em JavaScript.
 * O token CSRF é entregue pelo corpo da resposta, nunca por um cookie
 * acessível ao JavaScript. O JSESSIONID permanece HttpOnly.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    /** Obtenha antes do login e novamente após login ou logout. */
    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }

    /** Confirma se a sessão da proprietária continua autenticada. */
    @GetMapping("/me")
    public Map<String, Object> me(Principal principal) {
        return Map.of("authenticated", true, "username", principal.getName());
    }
}
