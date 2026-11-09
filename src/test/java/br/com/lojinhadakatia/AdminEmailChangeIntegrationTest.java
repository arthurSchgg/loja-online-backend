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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:lojinha_email_change;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "app.security.admin-email=old@example.org",
    "app.security.email-outbox-key=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
    "app.security.mail-from=sender@example.org",
    "app.security.email-change-url=https://example.org/confirm-email"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminEmailChangeIntegrationTest {
    @Autowired AdminEmailChangeService service;
    @Autowired AdminCredentialRepository accounts;
    @Autowired AdminEmailOutboxRepository outboxRepository;
    @Autowired AdminEmailOutboxService mailOutbox;
    @Autowired MockMvc mvc;
    @MockitoBean JavaMailSender sender;

    @Test
    void oldEmailStaysActiveUntilNewAddressConfirmsAndLinkCannotBeReused() {
        long originalVersion = accounts.findById(1L).orElseThrow().getCredentialVersion();
        service.requestChange("tester", "testpassword123", "NEW@example.org");
        assertEquals("old@example.org", accounts.findById(1L).orElseThrow().getEmail());
        assertEquals(2, outboxRepository.findAll().size());
        for (var queued : outboxRepository.findAll()) {
            assertFalse(queued.getEncryptedBody().contains("token="));
        }

        mailOutbox.deliverPending();
        var captor = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender, times(2)).send(captor.capture());
        String confirmation = captor.getAllValues().stream()
            .filter(m -> "new@example.org".equals(m.getTo()[0]))
            .map(SimpleMailMessage::getText).findFirst().orElseThrow();
        String token = java.util.regex.Pattern.compile("token=([A-Za-z0-9_-]+)")
            .matcher(confirmation).results().findFirst().orElseThrow().group(1);
        service.confirm(token);
        assertEquals("new@example.org", accounts.findById(1L).orElseThrow().getEmail());
        assertEquals(originalVersion + 1, accounts.findById(1L).orElseThrow().getCredentialVersion());
        assertThrows(IllegalArgumentException.class, () -> service.confirm(token));
    }

    @Test
    void oldPasswordOrUnchangedEmailCannotInitiateChange() {
        assertThrows(org.springframework.security.authentication.BadCredentialsException.class,
            () -> service.requestChange("tester", "wrong-password", "new@example.org"));
        assertThrows(IllegalArgumentException.class,
            () -> service.requestChange("tester", "testpassword123", "OLD@example.org"));
    }

    @Test
    void requestIsAdminOnlyAndBothEndpointsRequireCsrf() throws Exception {
        String request = "{\"currentPassword\":\"testpassword123\",\"newEmail\":\"new@example.org\"}";
        mvc.perform(post("/api/v1/admin/security/change-email")
            .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/security/change-email")
            .with(user("tester").roles("ADMIN"))
            .contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/auth/email-change/confirm")
            .contentType(MediaType.APPLICATION_JSON).content("{\"token\":\"no-token\"}"))
            .andExpect(status().isForbidden());
    }
}
