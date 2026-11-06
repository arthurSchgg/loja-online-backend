package br.com.lojinhadakatia.campaign.service;
import br.com.lojinhadakatia.campaign.dto.*;
import br.com.lojinhadakatia.campaign.entity.Campaign;
import br.com.lojinhadakatia.campaign.repository.CampaignRepository;
import br.com.lojinhadakatia.shared.exception.*;
import java.net.URI;
import java.time.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/** Publicação de banners condicionada a estado e janela temporal. */
@Service @Transactional(readOnly=true)
public class CampaignService {
 private final CampaignRepository repository;
 private final Clock clock;
 public CampaignService(CampaignRepository repository){this.repository=repository;this.clock=Clock.systemUTC();}
 public List<CampaignResponse> published(){
  Instant now=clock.instant();
  return repository.findByPublishedTrueOrderByPositionAscIdAsc().stream()
      .filter(c->c.isVisibleAt(now)).map(this::map).toList();
 }
 public List<CampaignResponse> adminAll(){return repository.findAll().stream().map(this::map).toList();}
 @Transactional public CampaignResponse create(CampaignRequest r){
  validate(r);
  return map(repository.saveAndFlush(new Campaign(r.title().trim(),r.subtitle(),r.imageUrl(),r.linkUrl(),
      r.position(),r.published(),r.startsAt(),r.endsAt())));
 }
 @Transactional public CampaignResponse update(Long id,CampaignRequest r){
  validate(r);
  Campaign c=find(id);c.update(r.title().trim(),r.subtitle(),r.imageUrl(),r.linkUrl(),
      r.position(),r.published(),r.startsAt(),r.endsAt());
  return map(repository.saveAndFlush(c));
 }
 @Transactional public void delete(Long id){
  Campaign c=find(id);
  if(c.isPublished())throw new BusinessException("Despublique a campanha antes de excluir.");
  repository.delete(c);
 }
 private Campaign find(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Campanha não encontrada."));}
 private void validate(CampaignRequest r){
  if(r.startsAt()!=null&&r.endsAt()!=null&&!r.endsAt().isAfter(r.startsAt()))
   throw new BusinessException("Fim da campanha deve ser posterior ao início.");
  validateHttps(r.imageUrl(),"Imagem da campanha");
  validateHttps(r.linkUrl(),"Link da campanha");
 }
 private void validateHttps(String text,String field){
  if(text==null||text.isBlank())return;
  URI uri;
  try{uri=URI.create(text);}catch(Exception e){throw new BusinessException(field+" inválido.");}
  if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null)
   throw new BusinessException(field+" deve usar HTTPS público.");
 }
 private CampaignResponse map(Campaign c){
  return new CampaignResponse(c.getId(),c.getTitle(),c.getSubtitle(),c.getImageUrl(),c.getLinkUrl(),
      c.getPosition(),c.isPublished(),c.getStartsAt(),c.getEndsAt());
 }
}
