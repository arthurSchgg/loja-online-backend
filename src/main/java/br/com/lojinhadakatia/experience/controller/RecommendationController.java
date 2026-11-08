package br.com.lojinhadakatia.experience.controller;

import br.com.lojinhadakatia.experience.dto.*;
import br.com.lojinhadakatia.experience.service.RecommendationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/** Acesso administrativo exclusivo via sessão e CSRF. */
@RestController
@RequestMapping("/api/v1/admin/experiences/{experienceId}/results/{resultKey}/recommendations")
public class RecommendationController {
    private final RecommendationService service;
    public RecommendationController(RecommendationService service){this.service=service;}
    @GetMapping
    public List<ProductSuggestion> list(@PathVariable Long experienceId,@PathVariable String resultKey){
        return service.suggestions(experienceId,resultKey);
    }
    @PutMapping
    public List<ProductSuggestion> replace(@PathVariable Long experienceId,@PathVariable String resultKey,
        @Valid @RequestBody RecommendationUpdateRequest request){
        return service.replace(experienceId,resultKey,request);
    }
}
