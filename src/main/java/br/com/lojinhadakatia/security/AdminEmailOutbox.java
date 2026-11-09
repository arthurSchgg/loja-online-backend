package br.com.lojinhadakatia.security;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "admin_email_outbox")
public class AdminEmailOutbox {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 254)
    private String recipient;
    @Column(nullable = false, length = 180)
    private String subject;
    @Column(name = "encrypted_body", nullable = false, length = 8192)
    private String encryptedBody;
    @Column(nullable = false) private int attempts;
    @Column(name = "next_attempt_at", nullable = false) private Instant nextAttemptAt;
    @Column(name = "sent_at") private Instant sentAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected AdminEmailOutbox() {}
    public AdminEmailOutbox(String recipient, String subject, String encryptedBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.encryptedBody = encryptedBody;
        this.createdAt = Instant.now();
        this.nextAttemptAt = createdAt;
    }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getEncryptedBody() { return encryptedBody; }
    public void sent() { sentAt = Instant.now(); }
    public void failed() {
        attempts++;
        nextAttemptAt = Instant.now().plusSeconds(Math.min(3600L, 30L << Math.min(attempts, 6)));
    }
}
