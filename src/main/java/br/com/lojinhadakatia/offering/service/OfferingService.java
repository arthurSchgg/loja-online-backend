package br.com.lojinhadakatia.offering.service;

import br.com.lojinhadakatia.offering.dto.*;
import br.com.lojinhadakatia.offering.entity.*;
import br.com.lojinhadakatia.offering.repository.ServiceOfferingRepository;
import br.com.lojinhadakatia.shared.exception.*;
import java.net.URI;
import java.util.List;
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
  var entity=find(id);entity.update(request.title().trim(),request.description(),request.type(),request.price(),
    request.imageUrl(),request.published());
  return map(repo.saveAndFlush(entity));
 }
 @Transactional public void delete(Long id){
  var entity=find(id);
  if(entity.isPublished())throw new BusinessException("Despublique a oferta antes de excluir.");
  repo.delete(entity);
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
