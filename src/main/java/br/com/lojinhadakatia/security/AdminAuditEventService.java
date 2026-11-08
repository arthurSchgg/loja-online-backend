package br.com.lojinhadakatia.security;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
public class AdminAuditEventService {
    private final AdminAuditEventRepository repository;
    public AdminAuditEventService(AdminAuditEventRepository repository){this.repository = repository;}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String actor, String method, String route, String resourceId, int status) {
        repository.saveAndFlush(new AdminAuditEvent(actor, method, route, resourceId, status));
    }

    @Transactional(readOnly=true)
    public Page<AdminAuditEventResponse> list(Pageable pageable) {
        return repository.findAll(pageable).map(AdminAuditEventResponse::from);
    }
}
