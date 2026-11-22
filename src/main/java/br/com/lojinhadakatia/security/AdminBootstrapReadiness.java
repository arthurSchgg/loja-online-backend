package br.com.lojinhadakatia.security;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Public health must not report UP before the administrative credentials
 * are ready for authentication. No usernames, credentials or diagnostics
 * are exposed by this contributor.
 *
 * The application runner marks this contributor ready only AFTER the
 * transactional initial administrator bootstrap has returned/committed.
 */
@Component
public class AdminBootstrapReadiness implements HealthIndicator {
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    /** Called only after the administrator bootstrap has completed successfully. */
    public void markInitialized() {
        initialized.set(true);
    }

    @Override
    public Health health() {
        return initialized.get() ? Health.up().build() : Health.down().build();
    }
}
