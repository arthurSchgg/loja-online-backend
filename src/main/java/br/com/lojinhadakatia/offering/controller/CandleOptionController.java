package br.com.lojinhadakatia.offering.controller;

import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.service.CandleOptionService;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
public class CandleOptionController {
    private final CandleOptionService service;
    public CandleOptionController(CandleOptionService service) {this.service = service;}

    @GetMapping("/api/v1/offerings/{id}/options")
    public List<CandleOptionResponse> publicOptions(@PathVariable Long id) {
        return service.publicOptions(id);
    }

    @GetMapping("/api/v1/admin/offerings/{id}/options")
    public List<CandleOptionResponse> adminOptions(@PathVariable Long id) {
        return service.adminOptions(id);
    }

    @PutMapping("/api/v1/admin/offerings/{id}/options")
    public List<CandleOptionResponse> replace(@PathVariable Long id,
        @RequestBody @NotNull @Size(max = 60) List<@NotNull @Valid CandleOptionRequest> requests) {
        return service.replace(id, requests);
    }
}
