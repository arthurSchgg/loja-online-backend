package br.com.lojinhadakatia.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Small in-memory safety net; deploy with WAF and shared limits for multiple replicas. */
@Component
public class AdminRecoveryLimiter {
    private static final int MAX_KEYS = 10000;
    private final Map<String, Entry> entries = new HashMap<>();
    private record Entry(int count, Instant windowStart) {}

    public synchronized boolean permit(String remoteIp, String email) {
        Instant now = Instant.now();
        String ip = "ip:" + normalize(remoteIp);
        String account = "email:" + normalize(email);
        int additional=(entries.containsKey(ip)?0:1)+(entries.containsKey(account)?0:1);
        if (entries.size()+additional > MAX_KEYS) {
            entries.entrySet().removeIf(e -> e.getValue().windowStart.plusSeconds(900).isBefore(now));
            // Fail closed when under deliberate key exhaustion rather than permit spam.
            additional=(entries.containsKey(ip)?0:1)+(entries.containsKey(account)?0:1);
            if (entries.size()+additional > MAX_KEYS)
                return false;
        }
        if (tooMany(ip, now) || tooMany(account, now)) return false;
        increment(ip, now);
        increment(account, now);
        return true;
    }

    private boolean tooMany(String key, Instant now) {
        Entry existing = entries.get(key);
        return existing != null && existing.windowStart.plusSeconds(900).isAfter(now)
            && existing.count >= 3;
    }

    private void increment(String key, Instant now) {
        Entry existing = entries.get(key);
        if (existing == null || !existing.windowStart.plusSeconds(900).isAfter(now))
            entries.put(key, new Entry(1, now));
        else entries.put(key, new Entry(existing.count + 1, existing.windowStart));
    }

    private String normalize(String input) {
        if (input == null) return "";
        String v = input.strip().toLowerCase(Locale.ROOT);
        return v.length() > 254 ? v.substring(0, 254) : v;
    }
}
