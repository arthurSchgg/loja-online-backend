package br.com.lojinhadakatia.catalog.controller;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** Operações administrativas; todas as rotas são protegidas por SecurityConfiguration. */
@Validated
@RestController
@RequestMapping("/api/v1/admin")
public class AdminCatalogController {

    private final CatalogService catalog;

    public AdminCatalogController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/products")
    public Page<ProductResponse> all(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size) {
        return catalog.search(null, null, null, null,
            PageRequest.of(page, size, Sort.by("id").descending()), true);
    }

    @GetMapping("/products/{id}")
    public ProductResponse one(@PathVariable Long id) {
        return catalog.adminFind(id);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse result = catalog.save(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/products/" + result.id())).body(result);
    }

    @PutMapping("/products/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return catalog.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalog.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/brands")
    public ResponseEntity<LookupDto> createBrand(@Valid @RequestBody LookupRequest request) {
        LookupDto result = catalog.addBrand(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/brands/" + result.id())).body(result);
    }

    @PutMapping("/brands/{id}")
    public LookupDto updateBrand(@PathVariable Long id, @Valid @RequestBody LookupRequest request) {
        return catalog.renameBrand(id, request);
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> deactivateBrand(@PathVariable Long id) {
        catalog.deactivateBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<LookupDto> createCategory(@Valid @RequestBody LookupRequest request) {
        LookupDto result = catalog.addCategory(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/categories/" + result.id())).body(result);
    }

    @PutMapping("/categories/{id}")
    public LookupDto updateCategory(@PathVariable Long id, @Valid @RequestBody LookupRequest request) {
        return catalog.renameCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long id) {
        catalog.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }
}
