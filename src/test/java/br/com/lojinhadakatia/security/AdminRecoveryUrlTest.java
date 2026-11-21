package br.com.lojinhadakatia.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminRecoveryUrlTest {
    @Test void recoveryLinksRequireAnExplicitHttpsPage() {
        assertTrue(AdminRecoveryUrl.valid("https://example.invalid/reset-password"));
        assertTrue(AdminRecoveryUrl.valid("https://localhost:8443/confirm-email"));
        for(String invalid:new String[]{"",null,"http://example.invalid/reset","https://example.invalid/",
            "https://example.invalid/reset?x=1","https://example.invalid/reset#fragment","https://","https://user@example.invalid/reset"})
            assertFalse(AdminRecoveryUrl.valid(invalid));
    }
}
