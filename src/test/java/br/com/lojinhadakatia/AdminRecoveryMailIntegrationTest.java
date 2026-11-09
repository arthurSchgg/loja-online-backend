package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:lojinha_recovery_mail;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "app.security.admin-email=tester@example.org",
    "app.security.email-outbox-key=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
    "app.security.mail-from=sender@example.org",
    "app.security.password-reset-url=https://example.org/reset"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminRecoveryMailIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired AdminPasswordRecoveryService recovery;
    @Autowired AdminEmailOutboxService outbox;
    @Autowired AdminEmailOutboxRepository emails;
    @Autowired AdminCredentialService credentials;
    @Autowired AdminSecurityNotifier notifier;
    @MockitoBean JavaMailSender sender;

    @Test
    void recoveryRequestStoresOnlyEncryptedLinkAndCanBeDelivered() throws Exception {
        assertTrue(outbox.configured());
        recovery.request("tester@example.org");
        var queued = emails.findAll();
        assertEquals(1, queued.size());
        assertFalse(queued.get(0).getEncryptedBody().contains("token="));
        assertFalse(queued.get(0).getEncryptedBody().contains("https://"));

        outbox.deliverPending();
        var captor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(1)).send(captor.capture());
        String body = captor.getValue().getText();
        assertNotNull(body);
        String token = java.util.regex.Pattern.compile("token=([A-Za-z0-9_-]+)")
            .matcher(body).results().findFirst().orElseThrow().group(1);
        recovery.confirm(token, "a-new-secure-password-1234", "a-new-secure-password-1234");
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm(token, "another-secure-password-1234", "another-secure-password-1234"));
    }

    @Test
    void unknownEmailGetsSameAcceptanceAndNoMail() throws Exception {
        mvc.perform(post("/api/v1/auth/password-reset/request").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"unknown@example.org\"}"))
            .andExpect(status().isAccepted());
        assertEquals(0, emails.findAll().size());
        verifyNoInteractions(sender);
    }

    @Test
    void resetEndpointsRequireCsrf() throws Exception {
        mvc.perform(post("/api/v1/auth/password-reset/request")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"tester@example.org\"}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/auth/password-reset/confirm")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"token\":\"x\",\"newPassword\":\"some-secure-password-123\",\"confirmPassword\":\"some-secure-password-123\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void suspiciousLoginNotificationIsDeduplicated() {
        notifier.suspiciousLogin("tester");
        notifier.suspiciousLogin("tester");
        assertEquals(1, emails.findAll().size());
        assertFalse(emails.findAll().get(0).getEncryptedBody().contains("repetidas tentativas"));
    }

    @Test
    void weakOrInvalidTokenDoesNotRotateCredentials() {
        long version = credentials.credentialVersion("tester");
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm("invalid", "short", "short"));
        assertEquals(version, credentials.credentialVersion("tester"));
    }
}
