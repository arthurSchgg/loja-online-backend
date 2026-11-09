package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "admin_email_change_tokens")
public class AdminEmailChangeToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(name = "new_email", nullable = false, length = 254)
    private String newEmail;
    @Column(name = "credential_version", nullable = false)
    private long credentialVersion;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "used_at")
    private Instant usedAt;

    protected AdminEmailChangeToken() {}
    public AdminEmailChangeToken(String tokenHash, String newEmail, long credentialVersion, Instant expiresAt) {
        this.tokenHash = tokenHash;
        this.newEmail = newEmail;
        this.credentialVersion = credentialVersion;
        this.expiresAt = expiresAt;
    }
    public boolean valid(Instant now, long currentVersion) {
        return usedAt == null && expiresAt.isAfter(now) && credentialVersion == currentVersion;
    }
    public String getNewEmail() { return newEmail; }
    public void consume(Instant now) { usedAt = now; }
}
