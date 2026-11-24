package br.com.lojinhadakatia.security;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** RFC 6238 authenticator-app factor. The seed lives only in protected runtime configuration. */
@Component
public class AdminTotp {
    private final byte[] seed;
    private final Clock clock;
    private long lastAccepted = -1;

    @org.springframework.beans.factory.annotation.Autowired
    public AdminTotp(@Value("${ADMIN_TOTP_SECRET:}") String encoded) {
        this(encoded, Clock.systemUTC());
    }

    AdminTotp(String encoded, Clock clock) {
        this.clock = clock;
        if (encoded == null || encoded.isBlank()) {
            seed = null;
            return;
        }
        String key = encoded.strip().toUpperCase(Locale.ROOT);
        if (!key.matches("[A-Z2-7]{32,128}") || key.length() % 8 != 0)
            throw new IllegalStateException("Invalid administrator authenticator configuration");
        seed = new byte[key.length() * 5 / 8];
        int buffer = 0, bits = 0, index = 0;
        for (char c : key.toCharArray()) {
            buffer = (buffer << 5) | "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".indexOf(c);
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                seed[index++] = (byte) (buffer >> bits);
            }
        }
    }

    public boolean enabled() { return seed != null; }

    /** Single-replica monotonic replay guard; runtime restart clears it. */
    public synchronized boolean verify(String code) {
        if (!enabled()) return true;
        if (code == null || !code.matches("[0-9]{6}")) return false;
        long step = clock.instant().getEpochSecond() / 30;
        byte[] presented = code.getBytes(StandardCharsets.US_ASCII);
        long match = -1;
        for (long candidate = step - 1; candidate <= step + 1; candidate++) {
            if (candidate >= 0 && MessageDigest.isEqual(presented, generate(candidate))
                    && candidate > lastAccepted) match = candidate;
        }
        if (match < 0) return false;
        lastAccepted = match;
        return true;
    }

    private byte[] generate(long counter) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(seed, "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(8).putLong(counter).array());
            int offset = hash[hash.length - 1] & 15;
            int value = ByteBuffer.wrap(hash, offset, 4).getInt() & 0x7fffffff;
            return String.format(Locale.ROOT, "%06d", value % 1_000_000).getBytes(StandardCharsets.US_ASCII);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Authenticator unavailable", e);
        }
    }

    /** Request-only details; the code is cleared before authentication returns. */
    static final class LoginDetails {
        private String code;
        LoginDetails(String code) { this.code = code; }
        String take() { String value = code; code = null; return value; }
    }
}
