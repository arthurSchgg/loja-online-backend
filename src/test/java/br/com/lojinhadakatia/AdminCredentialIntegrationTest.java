package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminCredentialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminCredentialIntegrationTest {
    @Autowired AdminCredentialService credentials;
    @Autowired PasswordEncoder encoder;
    @Autowired MockMvc mvc;

    @Test
    void bootstrapIsIdempotentAndNewPasswordSurvivesInitialization() {
        var before = credentials.loadUserByUsername("tester");
        long version = credentials.credentialVersion("tester");
        credentials.changePassword("tester", "testpassword123",
            "new-secure-test-password-123", "new-secure-test-password-123");
        assertEquals(version + 1, credentials.credentialVersion("tester"));
        assertTrue(encoder.matches("new-secure-test-password-123",
            credentials.loadUserByUsername("tester").getPassword()));
        credentials.initialize("tester", "testpassword123", "");
        assertTrue(encoder.matches("new-secure-test-password-123",
            credentials.loadUserByUsername("tester").getPassword()));
        assertFalse(encoder.matches("testpassword123",
            credentials.loadUserByUsername("tester").getPassword()));
    }

    @Test
    void longPassphraseDoesNotIgnoreCharactersAfterSeventyTwoBytes() {
        String full = "a".repeat(80) + "x";
        String different = "a".repeat(80) + "y";
        credentials.changePassword("tester", "testpassword123", full, full);
        String stored = credentials.loadUserByUsername("tester").getPassword();
        assertTrue(encoder.matches(full, stored));
        assertFalse(encoder.matches(different, stored));
    }

    @Test
    void changePasswordRequiresAuthenticationAndCsrf() throws Exception {
        String body = """
            {"currentPassword":"testpassword123",
             "newPassword":"new-secure-test-password-123",
             "confirmPassword":"new-secure-test-password-123"}
            """;
        mvc.perform(post("/api/v1/admin/security/change-password")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/security/change-password")
                .with(user("tester").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/admin/security/change-password")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.replace("testpassword123", "invalid-current-password")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectWeakOrMismatchedPassword() {
        assertThrows(IllegalArgumentException.class, () -> credentials.changePassword(
            "tester", "testpassword123", "short", "short"));
        assertThrows(IllegalArgumentException.class, () -> credentials.changePassword(
            "tester", "testpassword123", "new-secure-test-password-123", "not-the-same"));
    }
}
