package br.com.lojinhadakatia.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class BreachedPasswordVerifierTest {
    private static String sha1(String input) throws Exception {
        return HexFormat.of().withUpperCase().formatHex(MessageDigest.getInstance("SHA-1")
            .digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void sendsOnlyFiveHashCharactersAndBlocksMatchedExposedPassword() throws Exception {
        String secret = "a-test-secret-that-matches-a-breach";
        String digest = sha1(secret);
        AtomicInteger count = new AtomicInteger();
        var verifier = new BreachedPasswordVerifier(true, prefix -> {
            assertEquals(digest.substring(0, 5), prefix);
            assertEquals(5, prefix.length());
            assertFalse(prefix.contains(secret));
            count.incrementAndGet();
            return digest.substring(5) + ":12\r\n" + "A".repeat(35) + ":0";
        });

        BreachedPasswordException error = assertThrows(BreachedPasswordException.class,
            () -> verifier.verify(secret));
        assertTrue(error.getMessage().contains("vazamentos"));
        assertEquals(1, count.get());
    }

    @Test
    void acceptsUnlistedPasswordAndIgnoresPaddingWithCountZero() throws Exception {
        String secret = "a-unique-long-passphrase-for-the-test";
        String suffix = sha1(secret).substring(5);
        var verifier = new BreachedPasswordVerifier(true, prefix ->
            suffix + ":0\r\n" + "B".repeat(35) + ":19");
        assertDoesNotThrow(() -> verifier.verify(secret));
    }

    @Test
    void refusesWhenProviderIsOfflineOrResponseIsMalformed() {
        var offline = new BreachedPasswordVerifier(true, prefix -> {
            throw new IllegalStateException("network down");
        });
        ResponseStatusException unavailable = assertThrows(ResponseStatusException.class,
            () -> offline.verify("a-long-private-test-password"));
        assertEquals(503, unavailable.getStatusCode().value());
        assertFalse(unavailable.getMessage().contains("a-long-private-test-password"));

        var malformed = new BreachedPasswordVerifier(true, prefix -> "BAD_RESPONSE");
        assertEquals(503, assertThrows(ResponseStatusException.class,
            () -> malformed.verify("another-test-secret-password")).getStatusCode().value());
    }

    @Test
    void testProfileDisablesNetworkLookupExplicitly() {
        var verifier = new BreachedPasswordVerifier(false, prefix -> {
            fail("Disabled verifier must not query a network provider");
            return "";
        });
        assertDoesNotThrow(() -> verifier.verify("any-value"));
    }

    @Test
    void hashesUtf8WithoutSendingRawPasswordOrCompleteHash() throws Exception {
        String secret = "Uma senha de teste com ç e palavras privadas";
        String digest = sha1(secret);
        var verifier = new BreachedPasswordVerifier(true, prefix -> {
            assertEquals(digest.substring(0, 5), prefix);
            return "C".repeat(35) + ":1";
        });
        assertDoesNotThrow(() -> verifier.verify(secret));
    }
}
