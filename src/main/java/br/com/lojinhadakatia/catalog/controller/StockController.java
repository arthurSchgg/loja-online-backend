package br.com.lojinhadakatia.catalog.controller;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.StockService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.security.Principal;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** A SecurityConfiguration já protege todo o namespace /api/v1/admin/**. */
@RestController @Validated
@RequestMapping("/api/v1/admin/products/{productId}/stock")
public class StockController {
    private final StockService service;
    public StockController(StockService service) { this.service = service; }

    @GetMapping public StockBalanceResponse balance(@PathVariable Long productId) {
        return service.balance(productId);
    }

    @GetMapping("/movements")
    public Page<StockMovementResponse> history(@PathVariable Long productId,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.history(productId, PageRequest.of(page, size));
    }

    @PostMapping("/movements")
    public ResponseEntity<StockMovementResponse> record(@PathVariable Long productId,
        @Valid @RequestBody StockMovementRequest request, Principal principal) {
        var result = service.record(productId, request, principal.getName());
        return ResponseEntity.created(URI.create(
            "/api/v1/admin/products/" + productId + "/stock/movements/" + result.id())).body(result);
    }
}
