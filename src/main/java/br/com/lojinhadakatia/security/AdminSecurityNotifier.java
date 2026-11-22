package br.com.lojinhadakatia.security;

import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * Sends at most one alert per 30 minutes for the sole admin account.
 * Never includes passwords or the attempted password in the notification.
 */
@Service
public class AdminSecurityNotifier {
    private final AdminCredentialRepository accounts;
    private final AdminEmailOutboxService outbox;
    private Instant lastSuspiciousAlert = Instant.EPOCH;

    public AdminSecurityNotifier(AdminCredentialRepository accounts, AdminEmailOutboxService outbox) {
        this.accounts = accounts;
        this.outbox = outbox;
    }

    public synchronized void suspiciousLogin(String username) {
        if (!outbox.configured() || username == null) return;
        Instant now = Instant.now();
        if (lastSuspiciousAlert.plusSeconds(1800).isAfter(now)) return;
        var account = accounts.findByUsername(username);
        if (account.isEmpty() || account.get().getEmail() == null) return;
        outbox.enqueue(account.get().getEmail(), "Atividade suspeita — Lojinha da Kátia",
            "Foram detectadas repetidas tentativas inválidas de login da conta administrativa. "
            + "Se não reconhece a atividade, revise a segurança do acesso.");
        lastSuspiciousAlert = now;
    }
}
