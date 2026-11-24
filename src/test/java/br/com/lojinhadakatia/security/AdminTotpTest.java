package br.com.lojinhadakatia.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTotpTest {
    @Test void rfcVectorAndReplayProtection() {
        // RFC 6238 public test seed, never use it for a real account.
        var totp = new AdminTotp("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ",
            Clock.fixed(Instant.ofEpochSecond(59), ZoneOffset.UTC));
        assertTrue(totp.enabled());
        assertFalse(totp.verify(null));
        assertFalse(totp.verify("invalid"));
        assertTrue(totp.verify("287082"));
        assertFalse(totp.verify("287082"));
    }
    @Test void configurationDisabledUntilOwnerEnrolls() {
        assertTrue(new AdminTotp("").verify(null));
        assertThrows(IllegalStateException.class, () -> new AdminTotp("invalid"));
    }
}
