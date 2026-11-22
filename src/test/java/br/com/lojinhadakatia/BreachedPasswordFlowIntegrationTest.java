package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.AdminCredentialService;
import br.com.lojinhadakatia.security.BreachedPasswordException;
import br.com.lojinhadakatia.security.BreachedPasswordVerifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BreachedPasswordFlowIntegrationTest {
    @Autowired AdminCredentialService credentials;
    @Autowired br.com.lojinhadakatia.security.AdminPasswordResetRepository resetTokens;
    @Autowired br.com.lojinhadakatia.security.AdminPasswordRecoveryService recovery;
    @Autowired MockMvc mvc;
    @MockitoBean BreachedPasswordVerifier breachedVerifier;

    private static final String UNSAFE = "a-previously-exposed-credential-for-testing";

    @Test
    void authenticatedChangeReturns422AndPreservesCurrentPassword() throws Exception {
        String before = credentials.loadUserByUsername("tester").getPassword();
        long version = credentials.credentialVersion("tester");
        doThrow(new BreachedPasswordException()).when(breachedVerifier).verify(UNSAFE);

        mvc.perform(post("/api/v1/admin/security/change-password")
                .with(user("tester").roles("ADMIN")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"currentPassword\":\"testpassword123\",\"newPassword\":\""
                    + UNSAFE + "\",\"confirmPassword\":\"" + UNSAFE + "\"}"))
            .andExpect(status().isUnprocessableEntity());
        assertEquals(before, credentials.loadUserByUsername("tester").getPassword());
        assertEquals(version, credentials.credentialVersion("tester"));
    }

    @Test
    void passwordRecoveryRejectionDoesNotConsumeResetToken() throws Exception {
        String token = "valid-recovery-token-to-test-rejection";
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(token.getBytes(StandardCharsets.US_ASCII)));
        long version = credentials.credentialVersion("tester");
        resetTokens.saveAndFlush(new br.com.lojinhadakatia.security.AdminPasswordResetToken(
            hash, Instant.now().plusSeconds(900), version));
        doThrow(new BreachedPasswordException()).when(breachedVerifier).verify(UNSAFE);

        assertThrows(BreachedPasswordException.class,
            () -> recovery.confirm(token, UNSAFE, UNSAFE));
        assertEquals(version, credentials.credentialVersion("tester"));
        // The token is still valid after the blocked candidate was rejected.
        assertDoesNotThrow(() -> recovery.confirm(token, "a-different-private-test-passphrase",
            "a-different-private-test-passphrase"));
    }
}
