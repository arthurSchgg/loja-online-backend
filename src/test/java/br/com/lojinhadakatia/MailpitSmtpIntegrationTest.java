package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminEmailOutboxRepository;
import br.com.lojinhadakatia.security.AdminEmailOutboxService;
import br.com.lojinhadakatia.security.AdminPasswordRecoveryService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real SMTP delivery to a local Mailpit container; no outbound email is sent.
 * This is deliberately opt-in for CI, and is separate from TLS/provider staging checks.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:lojinha_mailpit_ci;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.mail.host=127.0.0.1",
    "spring.mail.port=1025",
    "spring.mail.properties.mail.smtp.connectiontimeout=3000",
    "spring.mail.properties.mail.smtp.timeout=3000",
    "app.security.admin-email=mailpit-ci@example.invalid",
    "app.security.email-outbox-key=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
    "app.security.mail-from=no-reply@example.invalid",
    "app.security.password-reset-url=https://staging.example.invalid/reset-password"
})
@ActiveProfiles("test")
@EnabledIfEnvironmentVariable(named = "RUN_SMTP_INTEGRATION", matches = "true")
@Transactional
class MailpitSmtpIntegrationTest {
    @Autowired AdminPasswordRecoveryService recovery;
    @Autowired AdminEmailOutboxService outbox;
    @Autowired AdminEmailOutboxRepository messages;

    @Test
    void recoveryLinkTravelsThroughActualSmtpButRemainsEncryptedAtRest() throws Exception {
        assertTrue(outbox.configured());
        recovery.request("mailpit-ci@example.invalid");
        var queued = messages.findAll();
        assertEquals(1, queued.size());
        assertFalse(queued.get(0).getEncryptedBody().contains("token="));
        assertNull(queued.get(0).getSentAt());

        // Use the application's real JavaMailSender, not a Mockito mock.
        outbox.deliverPending();
        assertNotNull(messages.findAll().get(0).getSentAt());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://127.0.0.1:8025/api/v1/messages"))
            .timeout(Duration.ofSeconds(5)).GET().build();
        HttpResponse<String> response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("mailpit-ci@example.invalid"),
            "Mailpit must capture the actual SMTP message addressed to the fake CI account.");
    }
}
