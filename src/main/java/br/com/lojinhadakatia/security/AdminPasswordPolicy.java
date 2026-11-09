package br.com.lojinhadakatia.security;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Offline baseline. This is NOT a substitute for a complete breached-password
 * corpus: enable a vetted breach check during production hardening.
 */
@Component
public class AdminPasswordPolicy {
    private static final Set<String> COMMON = Set.of(
        "123456789012", "123456789123", "123456789101112", "012345678901",
        "password1234", "password12345", "password123456", "password123456789",
        "qwerty123456", "qwertyuiop123", "qwertyuiopasdf",
        "admin12345678", "administrator123", "letmein123456", "iloveyou123456",
        "welcome123456", "welcome12345", "changeme12345", "secret123456",
        "abc123456789", "abcdefgh1234", "aaaaaaaaaaaa", "111111111111",
        "000000000000", "senha12345678", "minhasenha123", "brasil123456",
        "lojinha123456", "lojinhadakatia", "lojinhadakatia123"
    );

    public void validate(String password, String username) {
        if (password == null || password.length() < 12 || password.length() > 128)
            throw new IllegalArgumentException("Senha deve ter entre 12 e 128 caracteres.");
        String normalized = Normalizer.normalize(password, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT).strip();
        if (normalized.isEmpty() || COMMON.contains(normalized))
            throw new IllegalArgumentException("Escolha uma senha menos comum.");
        String compact = normalized.replaceAll("[^\\p{L}\\p{N}]", "");
        if (COMMON.contains(compact) || compact.chars().distinct().count() <= 2)
            throw new IllegalArgumentException("Escolha uma senha menos previsível.");
        String usernameNormalized = username == null ? "" :
            Normalizer.normalize(username, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT).strip();
        if (usernameNormalized.length() >= 5 && normalized.equals(usernameNormalized))
            throw new IllegalArgumentException("A senha não pode ser igual ao usuário.");
    }
}
