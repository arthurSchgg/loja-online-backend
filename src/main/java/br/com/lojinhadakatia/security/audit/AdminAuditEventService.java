package br.com.lojinhadakatia.security.audit;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuditEventService {
    private final AdminAuditEventRepository repository;

    public AdminAuditEventService(AdminAuditEventRepository repository) {
        this.repository = repository;
    }

    /** Separate transaction because afterCompletion runs after the HTTP handler's transaction. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String actor, String method, String route, String resourceId, int status) {
        repository.saveAndFlush(new AdminAuditEvent(actor, method, route, resourceId,
            status, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditEventResponse> search(String actor, String route,
        Instant fromTime, Instant toTime, Pageable pageable) {
        return repository.findFiltered(
            blankToNull(actor), blankToNull(route), fromTime, toTime, pageable)
            .map(AdminAuditEventResponse::from);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
