package br.com.lojinhadakatia.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Queries Pwned Passwords using k-anonymity. Only the first five hexadecimal
 * characters of SHA-1 leave this JVM. SHA-1 is NOT used to store credentials:
 * the authentication hash continues to be PBKDF2.
 */
@Component
public class BreachedPasswordVerifier {
    private static final HttpClient HTTP = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build();
    private static final String ENDPOINT = "https://api.pwnedpasswords.com/range/";
    private static final int MAX_RESPONSE_CHARS = 1_000_000;

    private final boolean enabled;
    private final Function<String, String> fetchRange;

    @Autowired
    public BreachedPasswordVerifier(
            @Value("${app.security.breached-password-check-enabled:true}") boolean enabled) {
        this(enabled, BreachedPasswordVerifier::fetchPwnedRange);
    }

    // Package-private seam allows tests to supply an offline range response.
    BreachedPasswordVerifier(boolean enabled, Function<String, String> fetchRange) {
        this.enabled = enabled;
        this.fetchRange = Objects.requireNonNull(fetchRange);
    }

    public void verify(String password) {
        if (!enabled) return;
        if (password == null) throw new IllegalArgumentException("Senha inválida.");

        String hash = sha1Hex(password);
        String prefix = hash.substring(0, 5);
        String expectedSuffix = hash.substring(5);
        final String response;
        try {
            response = fetchRange.apply(prefix);
        } catch (RuntimeException ex) {
            // No password or hash prefix is included in errors or logs.
            throw unavailable();
        }
        if (response == null || response.isBlank() || response.length() > MAX_RESPONSE_CHARS)
            throw unavailable();

        for (String line : response.split("\\r?\\n")) {
            if (line.isBlank()) continue;
            int colon = line.indexOf(':');
            if (colon != 35 || colon == line.length() - 1) throw unavailable();
            String suffix = line.substring(0, colon);
            String frequency = line.substring(colon + 1).strip();
            if (!suffix.matches("[0-9a-fA-F]{35}") || !frequency.matches("[0-9]+"))
                throw unavailable();
            final long occurrences;
            try {
                occurrences = Long.parseLong(frequency);
            } catch (NumberFormatException ex) {
                throw unavailable();
            }
            if (suffix.equalsIgnoreCase(expectedSuffix) && occurrences > 0)
                throw new IllegalArgumentException(
                    "Esta senha consta em vazamentos conhecidos. Escolha outra senha.");
        }
    }

    private static String sha1Hex(String password) {
        try {
            return HexFormat.of().withUpperCase().formatHex(MessageDigest.getInstance("SHA-1")
                .digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo de verificação indisponível.");
        }
    }

    private static String fetchPwnedRange(String prefix) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT + prefix))
            .timeout(Duration.ofSeconds(5))
            .header("User-Agent", "LojinhaDaKatia-Backend/0.1")
            .header("Add-Padding", "true")
            .GET().build();
        try {
            HttpResponse<String> response = HTTP.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200)
                throw new IllegalStateException("Serviço de senhas indisponível.");
            return response.body();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Verificação interrompida.");
        } catch (IOException ex) {
            throw new IllegalStateException("Verificação de senhas indisponível.");
        }
    }

    private static ResponseStatusException unavailable() {
        // Fail closed: do not save a new password without the required verification.
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
            "Verificação de segurança temporariamente indisponível. Tente novamente.");
    }
}
