package br.com.lojinhadakatia.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminPasswordPolicyTest {
    private final AdminPasswordPolicy policy = new AdminPasswordPolicy();

    @Test
    void rejectsCommonPasswordVariantsAndRepetition() {
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("PASSWORD123456", "admin"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("Password-123456", "admin"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("aaaaaaaaaaaa", "admin"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("qwerty123456", "admin"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("short", "admin"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("x".repeat(129), "admin"));
    }

    @Test
    void acceptsLongPassphraseWithoutTruncationOrForcedComplexity() {
        assertDoesNotThrow(() -> policy.validate(
            "Uma frase longa e particular que eu escolhi", "admin"));
        assertDoesNotThrow(() -> policy.validate("x".repeat(70) + "word", "admin"));
    }

    @Test
    void acceptsLegacyTestFixtureWhileBlockingDirectUsernameMatch() {
        assertDoesNotThrow(() -> policy.validate("testpassword123", "tester"));
        assertThrows(IllegalArgumentException.class,
            () -> policy.validate("administrator123", "administrator123"));
    }
}
