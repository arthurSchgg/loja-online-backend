package br.com.lojinhadakatia.catalog.controller;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
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
            @RequestParam(required=false) @Size(max=180) String q,
            @RequestParam(required=false) @Positive Long brandId,
            @RequestParam(required=false) @Positive Long categoryId,
            @RequestParam(required=false) Boolean published,
            @RequestParam(required=false) Boolean available,
            @RequestParam(defaultValue = "0") @Min(0) @Max(10000) int page,
            @RequestParam(defaultValue = "12") @Min(1) @Max(50) int size) {
        return catalog.search(q,brandId,categoryId,available,
            PageRequest.of(page,size,Sort.by("id").descending()),true,"recent",null,published);
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


    @GetMapping("/brands")
    public List<AdminLookupDto> brands() { return catalog.adminBrands(); }

    @GetMapping("/brands/{id}")
    public AdminLookupDto brand(@PathVariable Long id) { return catalog.adminBrand(id); }

    @PutMapping("/brands/{id}/activate")
    public ResponseEntity<Void> activateBrand(@PathVariable Long id) {
        catalog.activateBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/brands")
    public ResponseEntity<AdminLookupDto> createBrand(@Valid @RequestBody LookupRequest request) {
        AdminLookupDto result = catalog.addBrand(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/brands/" + result.id())).body(result);
    }

    @PutMapping("/brands/{id}")
    public AdminLookupDto updateBrand(@PathVariable Long id, @Valid @RequestBody LookupRequest request) {
        return catalog.renameBrand(id, request);
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<Void> deactivateBrand(@PathVariable Long id) {
        catalog.deactivateBrand(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/categories")
    public List<AdminLookupDto> categories() { return catalog.adminCategories(); }

    @GetMapping("/categories/{id}")
    public AdminLookupDto category(@PathVariable Long id) { return catalog.adminCategory(id); }

    @PutMapping("/categories/{id}/activate")
    public ResponseEntity<Void> activateCategory(@PathVariable Long id) {
        catalog.activateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    public ResponseEntity<AdminLookupDto> createCategory(@Valid @RequestBody LookupRequest request) {
        AdminLookupDto result = catalog.addCategory(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/categories/" + result.id())).body(result);
    }

    @PutMapping("/categories/{id}")
    public AdminLookupDto updateCategory(@PathVariable Long id, @Valid @RequestBody LookupRequest request) {
        return catalog.renameCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long id) {
        catalog.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }
}
