package br.com.lojinhadakatia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Explicit SMTP or HTTPS transport; a misconfigured provider never falls back to another. */
@Component
public class AdminMailTransport {
    private static final URI RESEND_ENDPOINT = URI.create("https://api.resend.com/emails");
    private final ObjectProvider<JavaMailSender> smtp;
    private final ObjectMapper json;
    private final String mode;
    private final String sender;
    private final String apiKey;
    private final HttpClient http;

    @Autowired
    public AdminMailTransport(ObjectProvider<JavaMailSender> smtp, ObjectMapper json,
            @Value("${app.security.email-transport:smtp}") String mode,
            @Value("${app.security.mail-from:}") String sender,
            @Value("${app.security.resend-api-key:}") String apiKey) {
        this(smtp,json,mode,sender,apiKey,"resend".equalsIgnoreCase(mode)
            ? HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(4))
                .followRedirects(HttpClient.Redirect.NEVER).build() : null);
    }

    AdminMailTransport(ObjectProvider<JavaMailSender> smtp, ObjectMapper json,
            String mode, String sender, String apiKey, HttpClient http) {
        this.smtp=smtp; this.json=json; this.mode=mode; this.sender=sender;
        this.apiKey=apiKey; this.http=http;
    }

    public boolean configured() {
        if (sender==null || sender.isBlank()) return false;
        if ("smtp".equalsIgnoreCase(mode)) return smtp.getIfAvailable()!=null;
        return "resend".equalsIgnoreCase(mode) && http!=null && apiKey!=null
            && !apiKey.isBlank() && apiKey.chars().noneMatch(Character::isWhitespace);
    }

    public void send(AdminEmailOutbox email, String body) {
        if (!configured()) throw new IllegalStateException("Transporte de e-mail indisponível.");
        if ("smtp".equalsIgnoreCase(mode)) {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(email.getRecipient());
            message.setSubject(email.getSubject());
            message.setText(body);
            smtp.getObject().send(message);
            return;
        }
        try {
            String payload=json.writeValueAsString(Map.of("from",sender,"to",List.of(email.getRecipient()),
                "subject",email.getSubject(),"text",body));
            HttpRequest request=HttpRequest.newBuilder(RESEND_ENDPOINT).timeout(Duration.ofSeconds(10))
                .header("Authorization","Bearer "+apiKey)
                .header("Content-Type","application/json")
                .header("Idempotency-Key",idempotencyKey(email))
                .POST(HttpRequest.BodyPublishers.ofString(payload,StandardCharsets.UTF_8)).build();
            int status=http.send(request,HttpResponse.BodyHandlers.discarding()).statusCode();
            if (status<200 || status>=300) throw new IllegalStateException("Envio HTTPS não aceito.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envio HTTPS interrompido.");
        } catch (IOException | IllegalArgumentException ex) {
            // Do not include vendor response, recipient, body, key, or exception details.
            throw new IllegalStateException("Falha no transporte HTTPS de e-mail.");
        }
    }

    private String idempotencyKey(AdminEmailOutbox email) {
        try {
            // Ciphertext has a random nonce; the key is stable for retries and
            // distinct even when a restored database reuses numeric IDs.
            return "katia-admin-"+HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(email.getEncryptedBody().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Identificador de e-mail indisponível.");
        }
    }
}
