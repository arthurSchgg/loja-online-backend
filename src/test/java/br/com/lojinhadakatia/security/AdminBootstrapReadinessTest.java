package br.com.lojinhadakatia.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

/** The health gate fails closed until bootstrapping has finished. */
class AdminBootstrapReadinessTest {
    @Test
    void healthIsDownUntilAdministratorInitializationCompletes() {
        var indicator = new AdminBootstrapReadiness();
        assertEquals(Status.DOWN, indicator.health().getStatus());
        indicator.markInitialized();
        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void contributorDoesNotLeakAdministrativeCredentialsOrIdentity() {
        var indicator = new AdminBootstrapReadiness();
        assertEquals(null, indicator.health().getDetails().get("password"));
        indicator.markInitialized();
        assertEquals(0, indicator.health().getDetails().size());
    }
}
