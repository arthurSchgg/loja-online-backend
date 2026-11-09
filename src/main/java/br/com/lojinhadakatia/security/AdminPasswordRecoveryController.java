package br.com.lojinhadakatia.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/password-reset")
public class AdminPasswordRecoveryController {
    private final AdminPasswordRecoveryService recovery;
    public AdminPasswordRecoveryController(AdminPasswordRecoveryService recovery) {
        this.recovery = recovery;
    }

    public record ResetRequest(@NotBlank @Email String email) {}
    public record ResetConfirm(@NotBlank String token, @NotBlank String newPassword,
                               @NotBlank String confirmPassword) {}

    @PostMapping("/request")
    public ResponseEntity<Void> request(@Valid @RequestBody ResetRequest body) {
        recovery.request(body.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody ResetConfirm body) {
        recovery.confirm(body.token(), body.newPassword(), body.confirmPassword());
        return ResponseEntity.noContent().build();
    }
}
