package br.com.lojinhadakatia.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression: flooding distinct usernames must not globally clear
 * throttles that were already applied to another account.
 */
class LoginAttemptLimiterCapacityTest {
    @Test
    void blockedAccountCannotBeUnlockedByChangingRemoteAddress() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        for (int i = 0; i < 5; i++) {
            limiter.failed("admin", "198.51.100." + (i + 1));
        }
        assertTrue(limiter.retryAfterSeconds("admin", "203.0.113.42") > 0,
            "Account throttle must apply independently of origin");
    }

    @Test
    void blockedOriginCannotBeUnlockedByChangingUsername() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        for (int i = 0; i < 5; i++) {
            limiter.failed("unique-" + i, "198.51.100.99");
        }
        assertTrue(limiter.retryAfterSeconds("unseen-account", "198.51.100.99") > 0,
            "Origin throttle must apply independently of account");
    }

    @Test
    void capacityEvictionDoesNotClearEveryActiveBlock() {
        LoginAttemptLimiter limiter = new LoginAttemptLimiter();
        for (int i = 0; i < 9990; i++) {
            limiter.failed("noise-" + i, "192.0.2.201");
        }
        for (int i = 0; i < 5; i++) {
            limiter.failed("protected-admin", "198.51.100.21");
        }
        assertTrue(limiter.retryAfterSeconds("protected-admin", "198.51.100.21") > 0);
        for (int i = 9990; i < 10020; i++) {
            limiter.failed("noise-" + i, "192.0.2.201");
        }
        assertTrue(limiter.retryAfterSeconds("protected-admin", "198.51.100.21") > 0,
            "Flooding distinct usernames must not reset all existing login blocks");
    }
}
