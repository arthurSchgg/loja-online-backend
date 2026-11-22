package br.com.lojinhadakatia.content.service;
import br.com.lojinhadakatia.content.dto.*;
import br.com.lojinhadakatia.content.entity.ContentSection;
import br.com.lojinhadakatia.content.repository.ContentSectionRepository;
import br.com.lojinhadakatia.shared.exception.*;
import br.com.lojinhadakatia.media.ImageReferenceValidator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Mantém páginas institucionais e blocos da home organizados por slug e posição. */
@Service @Transactional(readOnly=true)
public class ContentSectionService {
 private final ContentSectionRepository repository;
 private final ImageReferenceValidator imageReferences;
 public ContentSectionService(ContentSectionRepository repository,ImageReferenceValidator imageReferences){this.repository=repository;this.imageReferences=imageReferences;}
 public List<ContentSectionResponse> published(){return repository.findByPublishedTrueOrderByPositionAscIdAsc().stream().map(this::map).toList();}
 public List<ContentSectionResponse> all(){return repository.findAll().stream().map(this::map).toList();}
 @Transactional
 public ContentSectionResponse create(ContentSectionRequest request){
  imageReferences.validate(request.imageUrl(),true);
  if(repository.existsBySlug(request.slug()))throw new BusinessException("Slug já utilizado.");
  return map(repository.saveAndFlush(new ContentSection(request.slug(),request.title().trim(),request.body(),
   request.imageUrl(),request.position(),request.published())));
 }
 @Transactional
 public ContentSectionResponse update(Long id,ContentSectionRequest request){
  imageReferences.validate(request.imageUrl(),true);
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
 private ContentSectionResponse map(ContentSection s){
  return new ContentSectionResponse(s.getId(),s.getSlug(),s.getTitle(),s.getBody(),s.getImageUrl(),s.getPosition(),s.isPublished());
 }
}
