package br.com.lojinhadakatia.content.controller;
import br.com.lojinhadakatia.content.dto.*;
import br.com.lojinhadakatia.content.service.ContentSectionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/** Editor de seções institucionais da home e páginas informativas. */
@RestController
public class ContentSectionController {
 private final ContentSectionService service;
 public ContentSectionController(ContentSectionService service){this.service=service;}
 @GetMapping("/api/v1/content-sections") public List<ContentSectionResponse> published(){return service.published();}
 @GetMapping("/api/v1/admin/content-sections") public List<ContentSectionResponse> all(){return service.all();}
 @PostMapping("/api/v1/admin/content-sections") public ResponseEntity<ContentSectionResponse> create(@Valid @RequestBody ContentSectionRequest body){
  var result=service.create(body);
  return ResponseEntity.created(URI.create("/api/v1/admin/content-sections/"+result.id())).body(result);
 }
 @PutMapping("/api/v1/admin/content-sections/{id}") public ContentSectionResponse update(@PathVariable Long id,@Valid @RequestBody ContentSectionRequest body){return service.update(id,body);}
 @DeleteMapping("/api/v1/admin/content-sections/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
