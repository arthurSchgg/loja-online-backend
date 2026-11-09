package br.com.lojinhadakatia.content.service;
import br.com.lojinhadakatia.content.dto.*;
import br.com.lojinhadakatia.content.entity.ContentSection;
import br.com.lojinhadakatia.content.repository.ContentSectionRepository;
import br.com.lojinhadakatia.shared.exception.*;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mantém páginas institucionais e blocos da home organizados por slug e posição. */
@Service @Transactional(readOnly=true)
public class ContentSectionService {
 private final ContentSectionRepository repository;
 public ContentSectionService(ContentSectionRepository repository){this.repository=repository;}
 public List<ContentSectionResponse> published(){return repository.findByPublishedTrueOrderByPositionAscIdAsc().stream().map(this::map).toList();}
 public List<ContentSectionResponse> all(){return repository.findAll().stream().map(this::map).toList();}
 @Transactional
 public ContentSectionResponse create(ContentSectionRequest request){
  validateImage(request.imageUrl());
  if(repository.existsBySlug(request.slug()))throw new BusinessException("Slug já utilizado.");
  return map(repository.saveAndFlush(new ContentSection(request.slug(),request.title().trim(),request.body(),
   request.imageUrl(),request.position(),request.published())));
 }
 @Transactional
 public ContentSectionResponse update(Long id,ContentSectionRequest request){
  validateImage(request.imageUrl());
  var item=find(id);
  if(!item.getSlug().equals(request.slug()))throw new BusinessException("O slug não pode ser alterado após a criação.");
  item.update(request.title().trim(),request.body(),request.imageUrl(),request.position(),request.published());
  return map(repository.saveAndFlush(item));
 }
 @Transactional
 public void delete(Long id){
  var item=find(id);
  if(item.isPublished())throw new BusinessException("Despublique a seção antes de excluir.");
  repository.delete(item);
 }
 private ContentSection find(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Seção não encontrada."));}
 private void validateImage(String value){
  if(value==null||value.isBlank())return;
  URI uri;
  try{uri=URI.create(value);}catch(Exception e){throw new BusinessException("URL de imagem inválida.");}
  if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getHost()==null||uri.getUserInfo()!=null)
   throw new BusinessException("A imagem precisa de URL HTTPS pública.");
 }
 private ContentSectionResponse map(ContentSection s){
  return new ContentSectionResponse(s.getId(),s.getSlug(),s.getTitle(),s.getBody(),s.getImageUrl(),s.getPosition(),s.isPublished());
 }
}
