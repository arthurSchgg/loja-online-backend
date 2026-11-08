package br.com.lojinhadakatia.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Single-node progressive throttling. The runtime must use one application replica.
 * For horizontal scaling, replace this map with an atomic shared store.
 */
@Component
public class LoginAttemptLimiter {
    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_SECONDS = 600;
    private static final int MAX_RECORDS = 10000;

    private final Map<String, Attempts> attempts = new HashMap<>();

    private record Attempts(int failures, Instant began, Instant blockedUntil, int strikes) {}

    public synchronized long retryAfterSeconds(String username, String remoteAddress) {
        Instant now = Instant.now();
        long accountWait = waitFor(key("account", username), now);
        long originWait = waitFor(key("origin", remoteAddress), now);
        return Math.max(accountWait, originWait);
    }

    public synchronized void failed(String username, String remoteAddress) {
        Instant now = Instant.now();
        update(key("account", username), now);
        update(key("origin", remoteAddress), now);
        if (attempts.size() > MAX_RECORDS) {
            attempts.entrySet().removeIf(x -> x.getValue().blockedUntil().isBefore(now)
                && x.getValue().began().plusSeconds(WINDOW_SECONDS).isBefore(now));
        }
        // Never accept unbounded memory growth from arbitrary usernames or addresses.
        if (attempts.size() > MAX_RECORDS) attempts.clear();
    }

    /** A valid login clears only this account/origin pair. */
    public synchronized void succeeded(String username, String remoteAddress) {
        attempts.remove(key("account", username));
        attempts.remove(key("origin", remoteAddress));
    }

    private long waitFor(String key, Instant now) {
        Attempts old = attempts.get(key);
        if (old == null || !old.blockedUntil().isAfter(now)) return 0;
        return Math.max(1, old.blockedUntil().getEpochSecond() - now.getEpochSecond());
    }

    private void update(String key, Instant now) {
        Attempts old = attempts.get(key);
        if (old == null || old.began().plusSeconds(WINDOW_SECONDS).isBefore(now)) {
            attempts.put(key, new Attempts(1, now, Instant.EPOCH, 0));
            return;
        }
        if (old.blockedUntil().isAfter(now)) return;
        int count = old.failures() + 1;
        if (count < MAX_FAILURES) {
            attempts.put(key, new Attempts(count, old.began(), Instant.EPOCH, old.strikes()));
        } else {
            int strikes = Math.min(old.strikes() + 1, 6);
            long delay = Math.min(900, 30L << Math.min(strikes - 1, 5));
            attempts.put(key, new Attempts(0, now, now.plusSeconds(delay), strikes));
        }
    }

    private String key(String prefix, String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > 120) normalized = normalized.substring(0, 120);
        return prefix + ":" + normalized;
    }
}
