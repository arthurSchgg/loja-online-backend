package br.com.lojinhadakatia.security;

import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/admin/audit-events")
public class AdminAuditEventController {
    private final AdminAuditEventService service;
    public AdminAuditEventController(AdminAuditEventService service) {this.service = service;}

    @GetMapping
    public Page<AdminAuditEventResponse> list(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size) {
        return service.list(PageRequest.of(page, size, Sort.by("id").descending()));
    }
}
