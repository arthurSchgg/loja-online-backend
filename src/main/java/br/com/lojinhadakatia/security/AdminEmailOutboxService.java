package br.com.lojinhadakatia.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encrypts email bodies before persisting; no plaintext reset links in SQL.
 * Set EMAIL_OUTBOX_KEY to a random base64-encoded 32-byte value.
 */
@Service
public class AdminEmailOutboxService {
    private static final Logger LOGGER=LoggerFactory.getLogger(AdminEmailOutboxService.class);
    private final AdminEmailOutboxRepository repository;
    private final AdminMailTransport transport;
    private final byte[] key;
    private final SecureRandom random = new SecureRandom();

    public AdminEmailOutboxService(AdminEmailOutboxRepository repository,
            AdminMailTransport transport,
            @Value("${app.security.email-outbox-key:}") String configuredKey) {
        this.repository = repository;
        this.transport = transport;
        byte[] decoded;
        try { decoded = Base64.getDecoder().decode(configuredKey); }
        catch (IllegalArgumentException ex) { decoded = new byte[0]; }
        this.key = decoded.length == 32 ? decoded : new byte[0];
    }

    public boolean configured() {
        return key.length == 32 && transport.configured();
    }

    @Transactional
    public void enqueue(String recipient, String subject, String body) {
        if (!configured()) throw new IllegalStateException("Envio seguro de email não configurado.");
        repository.save(new AdminEmailOutbox(recipient, subject, encrypt(body)));
    }

    private String encrypt(String text) {
        try {
            byte[] nonce = new byte[12];
            random.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, nonce));
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, result, 0, nonce.length);
            System.arraycopy(encrypted, 0, result, nonce.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception ex) { throw new IllegalStateException("Falha de cifragem do email."); }
    }

    private String decrypt(String value) {
        try {
            byte[] payload = Base64.getDecoder().decode(value);
            byte[] nonce = java.util.Arrays.copyOfRange(payload, 0, 12);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, nonce));
            return new String(cipher.doFinal(payload, 12, payload.length - 12), StandardCharsets.UTF_8);
        } catch (Exception ex) { throw new IllegalStateException("Falha de decifragem do email."); }
    }

    @Scheduled(fixedDelayString = "${app.security.email-outbox-poll-ms:30000}")
    @Transactional
    public void deliverPending() {
        if (!configured()) return;
        for (AdminEmailOutbox email : repository.pending(Instant.now(), PageRequest.of(0, 10))) {
            try {
                transport.send(email,decrypt(email.getEncryptedBody()));
                email.sent();
            } catch (org.springframework.mail.MailException | IllegalStateException ex) {
                // Do not log email contents or links. Retry with capped backoff.
                // A damaged encrypted message must not prevent the rest of the queue.
                email.failed();
                LOGGER.warn("admin_email_delivery_failed id={} attempts={}",email.getId(),email.getAttempts());
            }
        }
    }
}
