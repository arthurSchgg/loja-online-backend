package br.com.lojinhadakatia.security;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Only accessible under the existing /api/v1/admin/** security rule.
 * Supports limited, combined audit filters without returning payloads or secrets.
 */
@Validated
@RestController
@RequestMapping("/api/v1/admin/audit-events")
public class AdminAuditController {
    private final AdminAuditRepository repository;

    public AdminAuditController(AdminAuditRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<AdminAuditResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) @Pattern(regexp = "POST|PUT|PATCH|DELETE") String action,
            @RequestParam(required = false) @Size(max = 180) String actor,
            @RequestParam(required = false) @Size(max = 300) String resource,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("O intervalo de datas é inválido.");
        }

        Specification<AdminAuditEvent> filter = (root, query, cb) -> cb.conjunction();
        if (action != null) filter = filter.and((root, query, cb) ->
            cb.equal(root.get("httpMethod"), action));
        if (actor != null && !actor.isBlank()) filter = filter.and((root, query, cb) ->
            cb.equal(root.get("actor"), actor.trim()));
        if (resource != null && !resource.isBlank()) {
            String prefix = resource.trim().replace("\\", "\\\\")
                .replace("%", "\\%").replace("_", "\\_");
            filter = filter.and((root, query, cb) ->
                cb.like(root.get("resourcePath"), prefix + "%", '\\'));
        }
        if (from != null) filter = filter.and((root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("createdAt"), from));
        if (to != null) filter = filter.and((root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("createdAt"), to));

        return repository.findAll(filter, PageRequest.of(page, size,
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))))
            .map(AdminAuditResponse::from);
    }
}
