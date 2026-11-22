package br.com.lojinhadakatia.security;

import java.security.Principal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminEmailChangeController {
    private final AdminEmailChangeService service;

    public AdminEmailChangeController(AdminEmailChangeService service) {
        this.service = service;
    }

    public record RequestEmailChange(
        @NotBlank @Size(max=128) String currentPassword,
        @NotBlank @Email @Size(max = 254) String newEmail
    ) {}
    public record ConfirmEmailChange(@NotBlank @Size(max=256) String token) {}

    @PostMapping("/api/v1/admin/security/change-email")
    public ResponseEntity<Void> request(@Valid @RequestBody RequestEmailChange body, Principal principal) {
        service.requestChange(principal.getName(), body.currentPassword(), body.newEmail());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/v1/auth/email-change/confirm")
    public ResponseEntity<Void> confirm(@Valid @RequestBody ConfirmEmailChange body) {
        service.confirm(body.token());
        return ResponseEntity.noContent().build();
    }
}
