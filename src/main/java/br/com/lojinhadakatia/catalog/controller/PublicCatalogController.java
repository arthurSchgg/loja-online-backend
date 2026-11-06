package br.com.lojinhadakatia.catalog.controller;
import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
/** Endpoints de leitura disponíveis aos visitantes. */
@Validated @RestController @RequestMapping("/api/v1")
public class PublicCatalogController {
    private final CatalogService catalog;
    public PublicCatalogController(CatalogService catalog){this.catalog=catalog;}
    @GetMapping("/brands") public List<LookupDto> brands(){return catalog.brands();}
    @GetMapping("/categories") public List<LookupDto> categories(){return catalog.categories();}
    @GetMapping("/products")
    public Page<ProductResponse> products(@RequestParam(required=false) String q,
        @RequestParam(required=false) Long brandId,@RequestParam(required=false) Long categoryId,
        @RequestParam(required=false) Boolean available,
        @RequestParam(defaultValue="0") @Min(0) int page,
        @RequestParam(defaultValue="12") @Min(1) @Max(50) int size){
        return catalog.search(q,brandId,categoryId,available,PageRequest.of(page,size,Sort.by("id").descending()),false);
    }
    @GetMapping("/products/{id}") public ProductResponse product(@PathVariable Long id){return catalog.published(id);}
}
