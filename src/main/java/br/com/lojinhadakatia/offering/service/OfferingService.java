package br.com.lojinhadakatia.offering.service;

import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.entity.*;
import br.com.lojinhadakatia.offering.repository.ServiceOfferingRepository;
import br.com.lojinhadakatia.offering.repository.OfferingOptionRepository;
import br.com.lojinhadakatia.shared.exception.*;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gerencia ofertas informativas; não realiza locações nem reservas. */
@Service @Transactional(readOnly=true)
public class OfferingService {
 private final ServiceOfferingRepository repo;
 private final OfferingOptionRepository options;
 public OfferingService(ServiceOfferingRepository repo, OfferingOptionRepository options){this.repo=repo;this.options=options;}
 public List<OfferingResponse> published(OfferingType type){
  return (type==null?repo.findByPublishedTrueOrderByIdDesc():repo.findByPublishedTrueAndTypeOrderByIdDesc(type))
     .stream().map(this::map).toList();
 }
 public OfferingResponse publicOne(Long id){return map(repo.findById(id).filter(ServiceOffering::isPublished)
    .orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada.")));}
 public List<OfferingResponse> adminAll(){return repo.findAll().stream().map(this::mapAdmin).toList();}
 public OfferingResponse adminOne(Long id){return mapAdmin(find(id));}
 @Transactional public OfferingResponse create(OfferingRequest request){
  validate(request);
  return map(repo.saveAndFlush(new ServiceOffering(request.title().trim(),request.description(),
    request.type(),request.price(),request.imageUrl(),request.published(),
    request.available()==null || request.available())));
 }
 @Transactional public OfferingResponse update(Long id,OfferingRequest request){
  validate(request);
  var entity=find(id);entity.update(request.title().trim(),request.description(),request.type(),request.price(),
    request.imageUrl(),request.published(),request.available()==null?entity.isAvailable():request.available());
  return map(repo.saveAndFlush(entity));
 }
 @Transactional public void delete(Long id){
  var entity=find(id);
  if(entity.isPublished())throw new BusinessException("Despublique a oferta antes de excluir.");
  repo.delete(entity);
 }
 @Transactional
 public List<OfferingOptionResponse> replaceOptions(Long offeringId, OfferingOptionsRequest request) {
  var offering=find(offeringId);
  if(offering.getType()!=OfferingType.CANDLE)
   throw new BusinessException("Somente modelos de vela aceitam personalizações.");
  Set<String> keys=new HashSet<>();
  List<OfferingOption> updated=new ArrayList<>();
  for(var item:request.options()){
   String label=item.label().trim();
   String unique=item.kind().name()+":"+label.toLowerCase(Locale.ROOT);
   if(!keys.add(unique))throw new BusinessException("Opção duplicada para este modelo.");
   updated.add(new OfferingOption(offering,item.kind(),label,item.position(),item.active()));
  }
  options.deleteByOfferingId(offeringId);
  options.flush();
  options.saveAllAndFlush(updated);
  return options.findByOfferingIdOrderByPositionAscIdAsc(offeringId).stream()
      .map(item->new OfferingOptionResponse(item.getKind(),item.getLabel(),item.getPosition(),item.isActive()))
      .toList();
 }

 /** Revalidates exact active selections; never calculates a fabricated candle price. */
 public Map<String,String> validateCustomizations(Long offeringId, Map<String,String> selections) {
  var offering=repo.findById(offeringId).filter(ServiceOffering::isPublished)
       .orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada."));
  if(selections==null||selections.isEmpty())return Map.of();
  if(offering.getType()!=OfferingType.CANDLE)
   throw new BusinessException("Esta oferta não aceita personalizações de vela.");
  var allowed=options.findByOfferingIdOrderByPositionAscIdAsc(offeringId).stream()
       .filter(OfferingOption::isActive).toList();
  Map<String,String> validated=new LinkedHashMap<>();
  for(var kind:OfferingOption.Kind.values()){
   String value=selections.get(kind.name());
   if(value==null)continue;
   if(value.isBlank() || allowed.stream().noneMatch(x->x.getKind()==kind && x.getLabel().equals(value)))
    throw new BusinessException("Personalização indisponível para este modelo.");
   validated.put(kind.name(),value);
  }
  if(validated.size()!=selections.size())
   throw new BusinessException("Tipo de personalização inválido.");
  return validated;
 }

 private ServiceOffering find(Long id){return repo.findById(id).orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada."));}
 private void validate(OfferingRequest request){
  String image=request.imageUrl();
  if(image!=null&&!image.isBlank()){
   URI uri;
   try{uri=URI.create(image);}catch(Exception e){throw new BusinessException("URL da imagem inválida.");}
   if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null)
    throw new BusinessException("A imagem deve usar URL HTTPS pública.");
  }
 }
 private OfferingResponse map(ServiceOffering o){
  return makeResponse(o,true);
 }
 private OfferingResponse mapAdmin(ServiceOffering o){
  return makeResponse(o,false);
 }
 private OfferingResponse makeResponse(ServiceOffering o, boolean publicView){
  List<OfferingOptionResponse> listed=options.findByOfferingIdOrderByPositionAscIdAsc(o.getId()).stream()
      .filter(item->!publicView||item.isActive())
      .map(item->new OfferingOptionResponse(item.getKind(),item.getLabel(),item.getPosition(),item.isActive()))
      .toList();
  return new OfferingResponse(o.getId(),o.getTitle(),o.getDescription(),o.getType(),
    o.getPrice(),o.getImageUrl(),o.isPublished(),o.isAvailable(),listed);
 }
}
