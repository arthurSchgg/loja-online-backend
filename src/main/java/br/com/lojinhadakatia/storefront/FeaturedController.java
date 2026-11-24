package br.com.lojinhadakatia.storefront;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeaturedController {
    private final FeaturedService service;
    public FeaturedController(FeaturedService service){this.service=service;}
    @GetMapping("/api/v1/storefront/featured")
    public FeaturedService.Selection published(){return service.publicSelection();}
    @GetMapping("/api/v1/admin/storefront/featured")
    public FeaturedService.Selection admin(){return service.adminSelection();}
    @PutMapping("/api/v1/admin/storefront/featured")
    public FeaturedService.Selection replace(@Valid @RequestBody FeaturedService.Request request){return service.replace(request);}
}
