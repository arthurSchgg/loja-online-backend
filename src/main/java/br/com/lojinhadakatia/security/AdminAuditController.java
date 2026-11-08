package br.com.lojinhadakatia.security;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Only accessible under the existing /api/v1/admin/** security rule. */
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
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return repository.findAllByOrderByCreatedAtDescIdDesc(PageRequest.of(page, size))
            .map(AdminAuditResponse::from);
    }
}
