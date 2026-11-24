package br.com.lojinhadakatia.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminEmailChangeService {
    private final AdminCredentialRepository accounts;
    private final AdminEmailChangeTokenRepository tokens;
    private final AdminEmailOutboxService outbox;
    private final PasswordEncoder passwordEncoder;
    private final String confirmationUrl;
    private final SecureRandom random = new SecureRandom();

    public AdminEmailChangeService(AdminCredentialRepository accounts,
            AdminEmailChangeTokenRepository tokens, AdminEmailOutboxService outbox,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.email-change-url:}") String confirmationUrl) {
        this.accounts = accounts;
        this.tokens = tokens;
        this.outbox = outbox;
        this.passwordEncoder = passwordEncoder;
        this.confirmationUrl = confirmationUrl;
    }

    private boolean configured() {
        return outbox.configured() && AdminRecoveryUrl.valid(confirmationUrl);
    }

    @Transactional
    public void requestChange(String username, String currentPassword, String newEmail) {
        if (!configured()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        AdminCredential admin = accounts.lockAdministrator()
            .orElseThrow(() -> new IllegalStateException("Administrador não configurado."));
        if (!Objects.equals(admin.getUsername(), username)
                || !passwordEncoder.matches(currentPassword, admin.getPasswordHash()))
            throw new BadCredentialsException("Senha atual inválida.");
        String normalized = newEmail.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals(admin.getEmail()))
            throw new IllegalArgumentException("O novo e-mail deve ser diferente do atual.");
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        tokens.saveAndFlush(new AdminEmailChangeToken(
            hash(rawToken), normalized, admin.getCredentialVersion(), Instant.now().plusSeconds(900)));
        outbox.enqueue(normalized, "Confirmar novo e-mail — Lojinha da Kátia",
            "Confirme o novo e-mail em até 15 minutos acessando:\n"
            + confirmationUrl + "?token=" + rawToken
            + "\nSe você não solicitou, ignore esta mensagem.");
        if (admin.getEmail() != null) {
            outbox.enqueue(admin.getEmail(), "Alteração de e-mail solicitada — Lojinha da Kátia",
                "Foi solicitada uma alteração do e-mail administrativo. "
                + "O e-mail atual permanece ativo até a confirmação.");
        }
    }

    @Transactional
    public void confirm(String rawToken) {
        if (rawToken == null || rawToken.length() > 256 || rawToken.isBlank())
            throw invalid();
        // Serialize confirmations and password rotations on the same account row.
        AdminCredential admin = accounts.lockAdministrator()
            .orElseThrow(() -> new IllegalStateException("Administrador não configurado."));
        AdminEmailChangeToken token = tokens.findByTokenHash(hash(rawToken))
            .orElseThrow(this::invalid);
        Instant now = Instant.now();
        if (!token.valid(now, admin.getCredentialVersion())) throw invalid();
        if (token.getNewEmail().equals(admin.getEmail())) throw invalid();
        String oldEmail = admin.getEmail();
        admin.changeEmail(token.getNewEmail());
        token.consume(now);
        accounts.saveAndFlush(admin);
        tokens.saveAndFlush(token);
        // Incrementing the account version revokes old password reset links and sessions.
        if (oldEmail != null && outbox.configured()) {
            outbox.enqueue(oldEmail, "E-mail administrativo alterado — Lojinha da Kátia",
                "O e-mail administrativo foi alterado. Se você não reconhece esta alteração, "
                + "contate o suporte.");
        }
        if (outbox.configured()) {
            outbox.enqueue(admin.getEmail(), "Novo e-mail confirmado — Lojinha da Kátia",
                "Este endereço agora é o e-mail administrativo da loja.");
        }
    }

    private IllegalArgumentException invalid() {
        return new IllegalArgumentException("Token inválido ou expirado.");
    }

    private String hash(String rawToken) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(rawToken.getBytes(StandardCharsets.US_ASCII)));
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível.", ex);
        }
    }
}
