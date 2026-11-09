package br.com.lojinhadakatia.security;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/password-reset")
public class AdminPasswordRecoveryController {
    private final AdminPasswordRecoveryService recovery;
    private final AdminRecoveryLimiter limiter;
    public AdminPasswordRecoveryController(AdminPasswordRecoveryService recovery, AdminRecoveryLimiter limiter) {
        this.recovery = recovery;
        this.limiter = limiter;
    }

    public record ResetRequest(@NotBlank @Email @Size(max=254) String email) {}
    public record ResetConfirm(@NotBlank String token, @NotBlank String newPassword,
                               @NotBlank String confirmPassword) {}

    @PostMapping("/request")
    public ResponseEntity<Void> request(@Valid @RequestBody ResetRequest body, HttpServletRequest request) {
        if (limiter.permit(request.getRemoteAddr(), body.email())) recovery.request(body.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody ResetConfirm body) {
        recovery.confirm(body.token(), body.newPassword(), body.confirmPassword());
        return ResponseEntity.noContent().build();
    }
}
