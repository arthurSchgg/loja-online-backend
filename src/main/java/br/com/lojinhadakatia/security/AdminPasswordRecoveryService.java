package br.com.lojinhadakatia.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminPasswordRecoveryService {
    private final AdminCredentialRepository accounts;
    private final AdminPasswordResetRepository resetTokens;
    private final AdminEmailOutboxService outbox;
    private final PasswordEncoder encoder;
    private final String resetUrl;
    private final SecureRandom random = new SecureRandom();

    public AdminPasswordRecoveryService(AdminCredentialRepository accounts,
            AdminPasswordResetRepository resetTokens, AdminEmailOutboxService outbox,
            PasswordEncoder encoder,
            @Value("${app.security.password-reset-url:}") String resetUrl) {
        this.accounts = accounts;
        this.resetTokens = resetTokens;
        this.outbox = outbox;
        this.encoder = encoder;
        this.resetUrl = resetUrl;
    }

    private boolean configured() {
        return outbox.configured() && resetUrl.startsWith("https://")
            && !resetUrl.contains("?") && !resetUrl.contains("#");
    }

    /** Always returns the same status regardless of whether the email matches. */
    @Transactional
    public void request(String email) {
        if (!configured()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        var account = accounts.findById(1L).orElseThrow();
        if (account.getEmail() == null || !account.getEmail().equals(
                email.trim().toLowerCase(Locale.ROOT))) return;
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        resetTokens.save(new AdminPasswordResetToken(hash(token), Instant.now().plusSeconds(900), account.getCredentialVersion()));
        outbox.enqueue(account.getEmail(), "Recuperação de acesso — Lojinha da Kátia",
            "Para redefinir a senha, abra este link em até 15 minutos:\n"
            + resetUrl + "?token=" + token
            + "\nSe você não solicitou, ignore esta mensagem.\n");
    }

    @Transactional
    public void confirm(String token, String next, String confirmation) {
        if (token == null || token.isBlank()) throw invalid();
        if (next == null || next.length() < 12 || next.length() > 128 || !next.equals(confirmation))
            throw new IllegalArgumentException("Nova senha inválida.");
        // Serialized by locking the only administrator to prevent simultaneous token reuse.
        AdminCredential account = accounts.lockAdministrator().orElseThrow();
        AdminPasswordResetToken reset = resetTokens.findByTokenHash(hash(token)).orElseThrow(this::invalid);
        Instant now = Instant.now();
        if (!reset.valid(now, account.getCredentialVersion())) throw invalid();
        if (encoder.matches(next, account.getPasswordHash()))
            throw new IllegalArgumentException("Escolha uma senha diferente.");
        account.changePassword(encoder.encode(next));
        reset.consume(now);
        accounts.saveAndFlush(account);
        resetTokens.saveAndFlush(reset);
        // The account version invalidates previously authenticated sessions.
        if (account.getEmail() != null && outbox.configured()) {
            outbox.enqueue(account.getEmail(), "Senha alterada — Lojinha da Kátia",
                "A senha administrativa foi alterada. Se não reconhece esta ação, contate o suporte.");
        }
    }

    private IllegalArgumentException invalid() {
        return new IllegalArgumentException("Token inválido ou expirado.");
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.US_ASCII)));
        } catch (Exception e) { throw new IllegalStateException("SHA-256 indisponível."); }
    }
}
