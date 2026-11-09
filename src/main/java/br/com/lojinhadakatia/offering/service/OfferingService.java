package br.com.lojinhadakatia.offering.service;

import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.entity.*;
import br.com.lojinhadakatia.offering.repository.ServiceOfferingRepository;
import br.com.lojinhadakatia.shared.exception.*;
import java.net.URI;
import java.util.List;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gerencia ofertas informativas; não realiza locações nem reservas. */
@Service @Transactional(readOnly=true)
public class OfferingService {
 private final ServiceOfferingRepository repo;
 public OfferingService(ServiceOfferingRepository repo){this.repo=repo;}
 public List<OfferingResponse> published(OfferingType type){
  return (type==null?repo.findByPublishedTrueOrderByIdDesc():repo.findByPublishedTrueAndTypeOrderByIdDesc(type))
     .stream().map(this::map).toList();
 }
 public OfferingResponse publicOne(Long id){return map(repo.findById(id).filter(ServiceOffering::isPublished)
    .orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada.")));}
 public List<OfferingResponse> adminAll(){return repo.findAll().stream().map(this::map).toList();}
 public OfferingResponse adminOne(Long id){return map(find(id));}
 @Transactional public OfferingResponse create(OfferingRequest request){
  validate(request);
  return map(repo.saveAndFlush(new ServiceOffering(request.title().trim(),request.description(),
    request.type(),request.price(),request.imageUrl(),request.published())));
 }
 @Transactional public OfferingResponse update(Long id,OfferingRequest request){
  validate(request);
  var entity=find(id);
  if(request.type()!=OfferingType.CANDLE && !entity.getOptions().isEmpty())
   throw new BusinessException("Remova as opções de vela antes de alterar o tipo.");
  entity.update(request.title().trim(),request.description(),request.type(),request.price(),
    request.imageUrl(),request.published());
  return map(repo.saveAndFlush(entity));
 }
 @Transactional public void delete(Long id){
  var entity=find(id);
  if(entity.isPublished())throw new BusinessException("Despublique a oferta antes de excluir.");
  repo.delete(entity);
 }
 public List<OfferingOptionResponse> publicOptions(Long id){
  var offering=repo.findById(id).filter(ServiceOffering::isPublished)
    .orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada."));
  if(offering.getType()!=OfferingType.CANDLE)return List.of();
  return offering.getOptions().stream().filter(OfferingOption::isActive)
    .map(this::optionResponse).toList();
 }
 public List<OfferingOptionResponse> adminOptions(Long id){
  return find(id).getOptions().stream().map(this::optionResponse).toList();
 }
 @Transactional public List<OfferingOptionResponse> replaceOptions(Long id,OfferingOptionsUpdateRequest request){
  ServiceOffering offering=find(id);
  if(offering.getType()!=OfferingType.CANDLE)
   throw new BusinessException("Personalizações são permitidas apenas para velas.");
  Set<String> keys=new HashSet<>();
  var list=new ArrayList<OfferingOption>();
  for(var input:request.options()){
   String value=input.value().trim();
   String key=input.kind().name()+":"+value.toLowerCase(Locale.ROOT);
   if(!keys.add(key))throw new BusinessException("Opção de vela repetida.");
   list.add(new OfferingOption(input.kind(),value,input.active()));
  }
  offering.replaceOptions(list);
  repo.saveAndFlush(offering);
  return offering.getOptions().stream().map(this::optionResponse).toList();
 }
 /** Revalidates current public options; returns only server-trusted labels. */
 public String validateSelections(Long id,Map<String,String> selections){
  var offering=repo.findById(id).filter(ServiceOffering::isPublished)
    .orElseThrow(()->new ResourceNotFoundException("Oferta não encontrada."));
  Map<String,String> choices=selections==null?Map.of():selections;
  if(offering.getType()!=OfferingType.CANDLE){
   if(!choices.isEmpty())throw new BusinessException("Serviço não aceita personalização.");
   return "";
  }
  var active=offering.getOptions().stream().filter(OfferingOption::isActive).toList();
  Set<String> kinds=new HashSet<>();
  for(var option:active)kinds.add(option.getKind().name());
  if(!choices.keySet().equals(kinds))
   throw new BusinessException("Escolha uma opção válida de cada personalização disponível.");
  List<String> messages=new ArrayList<>();
  for(var kind:List.of(OfferingOption.Kind.AROMA,OfferingOption.Kind.COLOR,OfferingOption.Kind.PACKAGING)){
   if(!kinds.contains(kind.name()))continue;
   String value=choices.get(kind.name());
   boolean valid=active.stream().anyMatch(o->o.getKind()==kind&&o.getValue().equals(value));
   if(!valid)throw new BusinessException("Opção de personalização indisponível.");
   String label=switch(kind){
    case AROMA -> "Aroma"; case COLOR -> "Cor"; case PACKAGING -> "Embalagem";
   };
   messages.add(label+": "+value);
  }
  return messages.isEmpty()?"":" Personalização: "+String.join("; ",messages)+".";
 }
 private OfferingOptionResponse optionResponse(OfferingOption o){
  return new OfferingOptionResponse(o.getId(),o.getKind(),o.getValue(),o.isActive());
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
  return new OfferingResponse(o.getId(),o.getTitle(),o.getDescription(),o.getType(),
    o.getPrice(),o.getImageUrl(),o.isPublished());
 }
}
