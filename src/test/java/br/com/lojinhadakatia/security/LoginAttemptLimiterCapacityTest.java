package br.com.lojinhadakatia.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptLimiterCapacityTest {

    @Test
    void saturationMustNotResetAnActiveBlock() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        String blockedAccount = "protected-account";
        String blockedOrigin = "192.0.2.77";

        for (int i = 0; i < 5; i++) {
            limiter.failed(blockedAccount, blockedOrigin);
        }
        assertTrue(limiter.retryAfterSeconds(blockedAccount, blockedOrigin) > 0);

        // Exceed the bound with unrelated identifiers. A previous implementation
        // called attempts.clear(), which silently removed every active block.
        for (int i = 0; i < 10_100; i++) {
            limiter.failed("other-account-" + i, "198.51.100." + i);
        }

        assertTrue(limiter.retryAfterSeconds(blockedAccount, blockedOrigin) > 0,
            "Active blocks must survive capacity cleanup");
        assertEquals(0, limiter.retryAfterSeconds("new-account", "203.0.113.99"));
    }
}
