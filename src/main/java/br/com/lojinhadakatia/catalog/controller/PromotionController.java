package br.com.lojinhadakatia.catalog.controller;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.service.PromotionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products/{productId}/promotions")
public class PromotionController {
    private final PromotionService service;
    public PromotionController(PromotionService service){this.service=service;}
    @GetMapping public List<PromotionResponse> list(@PathVariable Long productId){
        return service.list(productId);
    }
    @PostMapping public ResponseEntity<PromotionResponse> create(@PathVariable Long productId,
        @Valid @RequestBody PromotionRequest request){
        var result=service.create(productId,request);
        return ResponseEntity.created(URI.create("/api/v1/admin/products/"+productId+
            "/promotions/"+result.id())).body(result);
    }
    @PutMapping("/{promotionId}")
    public PromotionResponse update(@PathVariable Long productId,@PathVariable Long promotionId,
        @Valid @RequestBody PromotionRequest request){
        return service.update(productId,promotionId,request);
    }
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId,@PathVariable Long promotionId){
        service.delete(productId,promotionId);
        return ResponseEntity.noContent().build();
    }
}
