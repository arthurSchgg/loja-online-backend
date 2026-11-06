package br.com.lojinhadakatia.store.controller;
import br.com.lojinhadakatia.store.dto.*;
import br.com.lojinhadakatia.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
/** Publicação institucional consultável e editável apenas por administradores. */
@RestController
public class StoreController {
    private final StoreService service;
    public StoreController(StoreService service){this.service=service;}
    @GetMapping("/api/v1/store") public StoreResponse publicInfo(){return service.publicInfo();}
    @PutMapping("/api/v1/admin/store") public StoreResponse update(@Valid @RequestBody StoreRequest request){return service.update(request);}
}
