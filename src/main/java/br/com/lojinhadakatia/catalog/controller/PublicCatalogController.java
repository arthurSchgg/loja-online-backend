package br.com.lojinhadakatia.catalog.controller;
import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import java.util.List;
import java.math.BigDecimal;
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
    public Page<ProductResponse> products(@RequestParam(required=false) @Size(max=180) String q,
        @RequestParam(required=false) @Positive Long brandId,@RequestParam(required=false) @Positive Long categoryId,
        @RequestParam(required=false) Boolean available,
        @RequestParam(defaultValue="0") @Min(0) @Max(10000) int page,
        @RequestParam(defaultValue="12") @Min(1) @Max(50) int size,
        @RequestParam(defaultValue="recent") @Pattern(regexp="recent|name|price-asc|price-desc") String sort,
        @RequestParam(required=false) @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal maxPrice){
        Sort ordering=switch(sort){
            case "name" -> Sort.by(Sort.Order.asc("name"),Sort.Order.desc("id"));
            case "price-asc","price-desc" -> Sort.unsorted();
            default -> Sort.by("id").descending();
        };
        return catalog.search(q,brandId,categoryId,available,PageRequest.of(page,size,ordering),
            false,sort,maxPrice);
    }
    @GetMapping("/products/{id}") public ProductResponse product(@PathVariable Long id){return catalog.published(id);}
    @GetMapping("/products/{id}/related")
    public List<ProductResponse> related(@PathVariable @Positive Long id,
        @RequestParam(defaultValue="4") @Min(1) @Max(8) int limit){
        return catalog.related(id,limit);
    }
}
