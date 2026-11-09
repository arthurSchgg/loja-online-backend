package br.com.lojinhadakatia.security.audit;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;

/** GET only, authenticated by the existing /api/v1/admin/** security rule. */
@Validated
@RestController
@RequestMapping("/api/v1/admin/audit-events")
public class AdminAuditEventController {
    private final AdminAuditEventService service;

    public AdminAuditEventController(AdminAuditEventService service) {
        this.service = service;
    }

    @GetMapping
    public Page<AdminAuditEventResponse> search(
        @RequestParam(required = false) String actor,
        @RequestParam(required = false) String route,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size
    ) {
        if (from != null && to != null && !from.isBefore(to))
            throw new IllegalArgumentException("Período inválido.");
        return service.search(actor, route, from, to,
            PageRequest.of(page, size, Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id"))));
    }
}
