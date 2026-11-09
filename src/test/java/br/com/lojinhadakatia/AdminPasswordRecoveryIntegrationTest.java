package br.com.lojinhadakatia;

import br.com.lojinhadakatia.security.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminPasswordRecoveryIntegrationTest {
    @Autowired AdminCredentialService credentials;
    @Autowired AdminPasswordResetRepository tokens;
    @Autowired AdminPasswordRecoveryService recovery;

    private String hash(String token) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(token.getBytes(StandardCharsets.US_ASCII)));
    }

    @Test
    void tokenIsSingleUseAndUpdatesPersistentPassword() throws Exception {
        String token = "integration-test-high-entropy-token-not-real";
        tokens.saveAndFlush(new AdminPasswordResetToken(hash(token),
            Instant.now().plusSeconds(900), credentials.credentialVersion("tester")));
        recovery.confirm(token, "new-test-passphrase-1234", "new-test-passphrase-1234");
        assertTrue(credentials.loadUserByUsername("tester").getPassword().startsWith("$2"));
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm(token, "another-test-passphrase-1234", "another-test-passphrase-1234"));
    }

    @Test
    void expiredOrSupersededTokensAreRejected() throws Exception {
        String expired = "expired-test-token";
        tokens.saveAndFlush(new AdminPasswordResetToken(hash(expired),
            Instant.now().minusSeconds(1), credentials.credentialVersion("tester")));
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm(expired, "new-test-passphrase-1234", "new-test-passphrase-1234"));

        String prior = "stale-test-token";
        tokens.saveAndFlush(new AdminPasswordResetToken(hash(prior),
            Instant.now().plusSeconds(900), credentials.credentialVersion("tester")));
        credentials.changePassword("tester", "testpassword123",
            "updated-test-passphrase-1234", "updated-test-passphrase-1234");
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm(prior, "new-test-passphrase-1234", "new-test-passphrase-1234"));
    }

    @Test
    void unknownTokenIsRejectedWithoutChangingPassword() {
        assertThrows(IllegalArgumentException.class, () ->
            recovery.confirm("unknown-token", "new-test-passphrase-1234", "new-test-passphrase-1234"));
    }
}
