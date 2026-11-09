package br.com.lojinhadakatia.experience.service;

import br.com.lojinhadakatia.experience.dto.*;
import br.com.lojinhadakatia.experience.entity.*;
import br.com.lojinhadakatia.experience.repository.ExperienceRepository;
import br.com.lojinhadakatia.experience.repository.ExperienceRecommendationRepository;
import br.com.lojinhadakatia.catalog.repository.ProductRepository;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import br.com.lojinhadakatia.shared.exception.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Engine simples e determinística: cada alternativa soma um voto à chave do
 * resultado associado. Empate: resultado de chave lexicograficamente menor.
 * Não grava respostas; regras sofisticadas podem ser introduzidas sob ADR.
 */
@Service @Transactional(readOnly=true)
public class ExperienceService {
 private final ExperienceRepository repository;
 private final ExperienceRecommendationRepository recommendations;
 private final ProductRepository products;
 private final CatalogService catalog;
 public ExperienceService(ExperienceRepository repository, ExperienceRecommendationRepository recommendations,
                          ProductRepository products, CatalogService catalog) {
  this.repository=repository;this.recommendations=recommendations;this.products=products;this.catalog=catalog;
 }
 public List<ExperienceResponse> published(){return repository.findByPublishedTrueOrderByIdDesc().stream().map(this::map).toList();}
 public ExperienceResponse publicOne(Long id){
  return map(repository.findById(id).filter(Experience::isPublished)
      .orElseThrow(()->new ResourceNotFoundException("Experiência não encontrada.")));
 }
 public ExperienceResponse adminOne(Long id){return map(find(id));}
 public List<ExperienceResponse> adminAll(){return repository.findAll().stream().map(this::map).toList();}
 public EvaluationResponse evaluate(Long id,EvaluationRequest request){
  Experience experience=repository.findById(id).filter(Experience::isPublished)
    .orElseThrow(()->new ResourceNotFoundException("Experiência não encontrada."));
  if(request.answers().size()!=experience.getQuestions().size())
   throw new BusinessException("Responda exatamente uma alternativa por pergunta.");
  Map<Long,Long> selections=new HashMap<>();
  for(var answer:request.answers()){
   if(selections.putIfAbsent(answer.questionId(),answer.optionId())!=null)
    throw new BusinessException("Pergunta respondida mais de uma vez.");
  }
  Map<String,Integer> votes=new HashMap<>();
  for(var q:experience.getQuestions()){
   Long selected=selections.get(q.getId());
   ExperienceOption option=q.getOptions().stream().filter(o->o.getId().equals(selected)).findFirst()
       .orElseThrow(()->new BusinessException("Alternativa não pertence à pergunta."));
   votes.merge(option.getResultKey(),1,Integer::sum);
  }
  String key=votes.entrySet().stream().sorted(Comparator.<Map.Entry<String,Integer>>comparingInt(Map.Entry::getValue)
      .reversed().thenComparing(Map.Entry::getKey)).findFirst()
      .orElseThrow(()->new BusinessException("Experiência sem resultado.")).getKey();
  ExperienceResult winner=experience.getResults().stream().filter(result->result.getResultKey().equals(key))
      .findFirst().orElseThrow(()->new BusinessException("Resultado não configurado."));
  return new EvaluationResponse(winner.getResultKey(),winner.getTitle(),winner.getDescription(), eligibleRecommendations(winner));
 }
 /** Resolve only products actually published, available and with positive stock. */
 private List<ProductResponse> eligibleRecommendations(ExperienceResult winner) {
  var configured=recommendations.findByResultIdOrderByPositionAscIdAsc(winner.getId());
  List<ProductResponse> selected=new ArrayList<>();
  Set<Long> seen=new HashSet<>();
  for(var link:configured){
   var product=link.getProduct();
   if(!seen.add(product.getId()))continue;
   ProductResponse published=eligible(product.getId());
   if(published!=null)selected.add(published);
   if(selected.size()==3)return selected;
  }
  // Prefer the same brand; if necessary, use a shared category.
  // Both searches apply publication, manual availability and stock filters.
  for(var link:configured){
   if(selected.size()==3)break;
   var brandId=link.getProduct().getBrand().getId();
   addFallbacks(catalog.search(null, brandId, null, true,
       PageRequest.of(0, 40, Sort.by("id").descending()), false).getContent(),selected,seen);
  }
  for(var link:configured){
   if(selected.size()==3)break;
   for(var category:link.getProduct().getCategories()){
    if(selected.size()==3)break;
    addFallbacks(catalog.search(null, null, category.getId(), true,
        PageRequest.of(0, 40, Sort.by("id").descending()), false).getContent(),selected,seen);
   }
  }
  return List.copyOf(selected);
 }

 private void addFallbacks(List<ProductResponse> candidates, List<ProductResponse> selected, Set<Long> seen) {
  for(var candidate:candidates){
   if(selected.size()==3)break;
   if(candidate.available() && seen.add(candidate.id()))selected.add(candidate);
  }
 }

 private ProductResponse eligible(Long productId) {
  try {
   var candidate=catalog.published(productId);
   return candidate.available()?candidate:null;
  } catch(ResourceNotFoundException ignored) {
   return null;
  }
 }

 /** Replace the ordered manual links of a particular result of this experience. */
 @Transactional
 public ResultRecommendationsResponse replaceRecommendations(Long experienceId, String resultKey,
                                                    ResultRecommendationsRequest request) {
  Experience experience=find(experienceId);
  ExperienceResult result=experience.getResults().stream()
      .filter(r->r.getResultKey().equals(resultKey)).findFirst()
      .orElseThrow(()->new ResourceNotFoundException("Resultado não encontrado."));
  List<Long> ids=request.productIds();
  if(new HashSet<>(ids).size()!=ids.size())
   throw new BusinessException("Não repita produtos recomendados.");
  List<ExperienceRecommendation> next=new ArrayList<>();
  for(int i=0;i<ids.size();i++){
   var product=products.findById(ids.get(i))
       .orElseThrow(()->new ResourceNotFoundException("Produto recomendado não encontrado."));
   next.add(new ExperienceRecommendation(result,product,i));
  }
  recommendations.deleteByResultId(result.getId());
  recommendations.flush();
  recommendations.saveAllAndFlush(next);
  return new ResultRecommendationsResponse(resultKey,List.copyOf(ids));
 }

 @Transactional
 public ExperienceResponse create(ExperienceRequest request){
  validate(request);
  Experience e=new Experience(request.title().trim(),request.description());
  apply(e,request);
  return map(repository.saveAndFlush(e));
 }
 @Transactional
 public ExperienceResponse update(Long id,ExperienceRequest request){
  validate(request);
  Experience e=find(id);
  apply(e,request);
  return map(repository.saveAndFlush(e));
 }
 @Transactional
 public void delete(Long id){repository.delete(find(id));}
 private Experience find(Long id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Experiência não encontrada."));}
 private void apply(Experience e,ExperienceRequest request){
  var questions=request.questions().stream().map(q->new ExperienceQuestion(q.title().trim(),q.position(),
      q.options().stream().map(o->new ExperienceOption(o.label().trim(),o.position(),o.resultKey().trim())).toList())).toList();
  var results=request.results().stream().map(r->new ExperienceResult(r.key().trim(),r.title().trim(),r.description())).toList();
  e.replace(request.title().trim(),request.description(),request.published(),questions,results);
 }
 private void validate(ExperienceRequest request){
  if(request.questions().size()>30 || request.results().size()>20)
   throw new BusinessException("Limite de perguntas ou resultados excedido.");
  Set<String> keys=new HashSet<>();
  for(var r:request.results())if(!keys.add(r.key().trim()))throw new BusinessException("Chaves de resultado repetidas.");
  if(request.published()&&(request.questions().isEmpty()||request.results().isEmpty()))
   throw new BusinessException("Não é possível publicar uma experiência vazia.");
  for(var q:request.questions()){
   if(q.options().size()>20)throw new BusinessException("Limite de alternativas excedido.");
   if(request.published()&&q.options().size()<2)throw new BusinessException("Cada pergunta publicada precisa de duas alternativas.");
   for(var option:q.options())if(!keys.contains(option.resultKey().trim()))
    throw new BusinessException("Alternativa referencia resultado inexistente.");
  }
 }
 private ExperienceResponse map(Experience e){
  return new ExperienceResponse(e.getId(),e.getTitle(),e.getDescription(),e.getQuestions().stream()
    .map(q->new ExperienceResponse.Question(q.getId(),q.getTitle(),q.getPosition(),q.getOptions().stream()
      .map(o->new ExperienceResponse.Option(o.getId(),o.getLabel(),o.getPosition())).toList())).toList());
 }
}
