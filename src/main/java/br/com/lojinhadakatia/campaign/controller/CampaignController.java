package br.com.lojinhadakatia.campaign.controller;
import br.com.lojinhadakatia.campaign.dto.*;
import br.com.lojinhadakatia.campaign.service.CampaignService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/** Vitrines promocionais informativas, sem preços calculados. */
@RestController
public class CampaignController {
 private final CampaignService service;
 public CampaignController(CampaignService service){this.service=service;}
 @GetMapping("/api/v1/campaigns") public List<CampaignResponse> published(){return service.published();}
 @GetMapping("/api/v1/admin/campaigns") public List<CampaignResponse> all(){return service.adminAll();}
 @PostMapping("/api/v1/admin/campaigns") public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest body){
  var result=service.create(body);return ResponseEntity.created(URI.create("/api/v1/admin/campaigns/"+result.id())).body(result);
 }
 @PutMapping("/api/v1/admin/campaigns/{id}") public CampaignResponse update(@PathVariable Long id,@Valid @RequestBody CampaignRequest body){return service.update(id,body);}
 @DeleteMapping("/api/v1/admin/campaigns/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){service.delete(id);return ResponseEntity.noContent().build();}
}
