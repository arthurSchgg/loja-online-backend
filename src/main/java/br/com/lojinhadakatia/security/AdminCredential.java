package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "admin_credentials")
public class AdminCredential {
    @Id private Long id;
    @Column(nullable = false, unique = true, length = 120) private String username;
    @Column(length = 254) private String email;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Column(name = "credential_version", nullable = false) private long credentialVersion;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;

    protected AdminCredential() {}

    public AdminCredential(String username, String email, String hash) {
        this.id = 1L;
        this.username = username;
        this.email = email;
        this.passwordHash = hash;
        this.credentialVersion = 1L;
        this.updatedAt = Instant.now();
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public long getCredentialVersion() { return credentialVersion; }

    public void changePassword(String hash) {
        this.passwordHash = hash;
        this.credentialVersion++;
        this.updatedAt = Instant.now();
    }
}
