package br.com.lojinhadakatia.offering.controller;
import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import br.com.lojinhadakatia.offering.service.OfferingService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/** Ofertas de velas, oficinas e locação, com escritas administrativas protegidas. */
@RestController
public class OfferingController {
 private final OfferingService service;
 public OfferingController(OfferingService service){this.service=service;}
 @GetMapping("/api/v1/offerings") public List<OfferingResponse> publicList(@RequestParam(required=false) OfferingType type){return service.published(type);}
 @GetMapping("/api/v1/offerings/{id}") public OfferingResponse publicOne(@PathVariable Long id){return service.publicOne(id);}
 @GetMapping("/api/v1/admin/offerings") public List<OfferingResponse> adminList(){return service.adminAll();}
 @GetMapping("/api/v1/admin/offerings/{id}") public OfferingResponse adminOne(@PathVariable Long id){return service.adminOne(id);}
 @PostMapping("/api/v1/admin/offerings") public ResponseEntity<OfferingResponse> create(@Valid @RequestBody OfferingRequest body){
  var result=service.create(body);return ResponseEntity.created(URI.create("/api/v1/admin/offerings/"+result.id())).body(result);
 }
 @PutMapping("/api/v1/admin/offerings/{id}") public OfferingResponse update(@PathVariable Long id,@Valid @RequestBody OfferingRequest body){return service.update(id,body);}
 @DeleteMapping("/api/v1/admin/offerings/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
