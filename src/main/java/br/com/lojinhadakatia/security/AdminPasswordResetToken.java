package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "admin_password_reset_tokens")
public class AdminPasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "credential_version", nullable = false) private long credentialVersion;
    @Column(name = "used_at") private Instant usedAt;

    protected AdminPasswordResetToken() {}
    public AdminPasswordResetToken(String hash, Instant expiresAt) {
        this.tokenHash = hash;
        this.expiresAt = expiresAt;
    }
    public boolean valid(Instant now) {
        return usedAt == null && expiresAt.isAfter(now);
    }
    public void consume(Instant now) { this.usedAt = now; }
}
