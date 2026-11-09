package br.com.lojinhadakatia.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/security")
public class AdminPasswordController {
    private final AdminCredentialService credentials;
    public AdminPasswordController(AdminCredentialService credentials) {
        this.credentials = credentials;
    }

    public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword,
        @NotBlank String confirmPassword) {}

    @PostMapping("/change-password")
    public ResponseEntity<Void> change(@Valid @RequestBody ChangePasswordRequest body,
                                       Principal principal, HttpServletRequest request) {
        credentials.changePassword(principal.getName(), body.currentPassword(),
            body.newPassword(), body.confirmPassword());
        // The credential version also invalidates all previous server sessions.
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
