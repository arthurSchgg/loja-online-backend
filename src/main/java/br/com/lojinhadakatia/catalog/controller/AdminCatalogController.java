package br.com.lojinhadakatia.catalog.controller;
import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/** Administração protegida em SecurityConfiguration. */
@RestController @RequestMapping("/api/v1/admin")
public class AdminCatalogController {
    private final CatalogService catalog;
    public AdminCatalogController(CatalogService catalog){this.catalog=catalog;}
    @GetMapping("/products") public Page<ProductResponse> all(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="12") int size){
        if(page<0||size<1||size>50)throw new IllegalArgumentException("Paginação inválida.");
        return catalog.search(null,null,null,null,PageRequest.of(page,size,Sort.by("id").descending()),true);
    }
    @GetMapping("/products/{id}") public ProductResponse one(@PathVariable Long id){return catalog.adminFind(id);}
    @PostMapping("/products") public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest input){
        var result=catalog.save(input);return ResponseEntity.created(URI.create("/api/v1/admin/products/"+result.id())).body(result);
    }
    @PutMapping("/products/{id}") public ProductResponse update(@PathVariable Long id,@Valid @RequestBody ProductRequest input){return catalog.update(id,input);}
    @DeleteMapping("/products/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){catalog.delete(id);return ResponseEntity.noContent().build();}
    @PostMapping("/brands") public ResponseEntity<LookupDto> createBrand(@Valid @RequestBody LookupRequest input){
        var result=catalog.addBrand(input);return ResponseEntity.created(URI.create("/api/v1/brands/"+result.id())).body(result);
    }
    @PutMapping("/brands/{id}") public LookupDto updateBrand(@PathVariable Long id,@Valid @RequestBody LookupRequest input){return catalog.renameBrand(id,input);}
    @DeleteMapping("/brands/{id}") public ResponseEntity<Void> deleteBrand(@PathVariable Long id){catalog.deactivateBrand(id);return ResponseEntity.noContent().build();}
    @PostMapping("/categories") public ResponseEntity<LookupDto> createCategory(@Valid @RequestBody LookupRequest input){
        var result=catalog.addCategory(input);return ResponseEntity.created(URI.create("/api/v1/categories/"+result.id())).body(result);
    }
    @PutMapping("/categories/{id}") public LookupDto updateCategory(@PathVariable Long id,@Valid @RequestBody LookupRequest input){return catalog.renameCategory(id,input);}
    @DeleteMapping("/categories/{id}") public ResponseEntity<Void> deleteCategory(@PathVariable Long id){catalog.deactivateCategory(id);return ResponseEntity.noContent().build();}
}
