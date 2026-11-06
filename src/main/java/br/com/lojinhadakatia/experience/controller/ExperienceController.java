package br.com.lojinhadakatia.experience.controller;
import br.com.lojinhadakatia.experience.dto.*;
import br.com.lojinhadakatia.experience.service.ExperienceService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/** APIs públicas e administrativas de quizzes e experiências. */
@RestController
public class ExperienceController {
 private final ExperienceService service;
 public ExperienceController(ExperienceService service){this.service=service;}
 @GetMapping("/api/v1/experiences") public List<ExperienceResponse> all(){return service.published();}
 @GetMapping("/api/v1/experiences/{id}") public ExperienceResponse one(@PathVariable Long id){return service.publicOne(id);}
 @PostMapping("/api/v1/experiences/{id}/evaluate")
 public EvaluationResponse evaluate(@PathVariable Long id,@Valid @RequestBody EvaluationRequest body){return service.evaluate(id,body);}
 @GetMapping("/api/v1/admin/experiences") public List<ExperienceResponse> adminAll(){return service.adminAll();}
 @GetMapping("/api/v1/admin/experiences/{id}") public ExperienceResponse adminOne(@PathVariable Long id){return service.adminOne(id);}
 @PostMapping("/api/v1/admin/experiences")
 public ResponseEntity<ExperienceResponse> create(@Valid @RequestBody ExperienceRequest body){
  var result=service.create(body);
  return ResponseEntity.created(URI.create("/api/v1/admin/experiences/"+result.id())).body(result);
 }
 @PutMapping("/api/v1/admin/experiences/{id}")
 public ExperienceResponse update(@PathVariable Long id,@Valid @RequestBody ExperienceRequest body){return service.update(id,body);}
 @DeleteMapping("/api/v1/admin/experiences/{id}")
 public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
