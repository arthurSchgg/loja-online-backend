package br.com.lojinhadakatia.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Writes after request completion in an independent transaction, so rejected
 * changes are auditable even when a business transaction was rolled back.
 */
@Service
public class AdminAuditWriter {
    private final AdminAuditRepository repository;

    public AdminAuditWriter(AdminAuditRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void append(String actor, String method, String route, String path, int status) {
        repository.saveAndFlush(new AdminAuditEvent(
            limited(actor, 180), limited(method, 12),
            limited(route, 300), limited(path, 300), status));
    }

    private static String limited(String input, int max) {
        if (input == null || input.isBlank()) return "unknown";
        return input.length() <= max ? input : input.substring(0, max);
    }
}
