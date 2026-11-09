package br.com.lojinhadakatia.experience.service;

import br.com.lojinhadakatia.experience.dto.*;
import br.com.lojinhadakatia.experience.entity.*;
import br.com.lojinhadakatia.experience.repository.ExperienceRepository;
import br.com.lojinhadakatia.catalog.repository.ProductRepository;
import br.com.lojinhadakatia.catalog.entity.Product;
import br.com.lojinhadakatia.catalog.service.CatalogService;
import br.com.lojinhadakatia.catalog.dto.ProductResponse;
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
 private final ProductRepository products;
 private final CatalogService catalog;
 public ExperienceService(ExperienceRepository repository, ProductRepository products, CatalogService catalog){
  this.repository=repository;this.products=products;this.catalog=catalog;
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
  return new EvaluationResponse(winner.getResultKey(),winner.getTitle(),winner.getDescription(),recommend(winner));
 }
 /** Admin chooses up to three ordered product IDs per result profile. */
 @Transactional
 public ResultRecommendationsResponse updateRecommendations(Long experienceId, String key, ResultRecommendationsRequest request) {
  Experience e=find(experienceId);
  ExperienceResult result=e.getResults().stream().filter(r->r.getResultKey().equals(key))
    .findFirst().orElseThrow(()->new ResourceNotFoundException("Perfil de resultado não encontrado."));
  if(new HashSet<>(request.productIds()).size()!=request.productIds().size())
   throw new BusinessException("Produto recomendado repetido.");
  List<Product> selected=new ArrayList<>();
  for(Long id:request.productIds()){
   Product p=products.findById(id).orElseThrow(()->new BusinessException("Produto recomendado inexistente."));
   selected.add(p);
  }
  result.setRecommendations(selected);
  repository.saveAndFlush(e);
  return new ResultRecommendationsResponse(key, selected.stream().map(Product::getId).toList());
 }
 public ResultRecommendationsResponse recommendations(Long experienceId,String key){
  Experience e=find(experienceId);
  ExperienceResult result=e.getResults().stream().filter(r->r.getResultKey().equals(key))
    .findFirst().orElseThrow(()->new ResourceNotFoundException("Perfil de resultado não encontrado."));
  return new ResultRecommendationsResponse(key,result.getRecommendations().stream()
    .map(r->r.getProduct().getId()).toList());
 }
 private boolean eligible(Product p){
  return p.isPublished() && p.isAvailable() && p.getStockQuantity()>0;
 }
 /** Fallback only uses similar products from the brands/categories of curated products. */
 private List<ProductResponse> recommend(ExperienceResult result){
  var curated=result.getRecommendations().stream().map(ExperienceResultRecommendation::getProduct).toList();
  LinkedHashMap<Long,Product> selected=new LinkedHashMap<>();
  for(Product p:curated)if(eligible(p))selected.putIfAbsent(p.getId(),p);
  if(selected.size()<3 && !curated.isEmpty()){
   Set<Long> brandIds=new HashSet<>();
   Set<Long> categoryIds=new HashSet<>();
   for(Product p:curated){
    brandIds.add(p.getBrand().getId());
    p.getCategories().forEach(c->categoryIds.add(c.getId()));
   }
   for(Product candidate:products.findAll(org.springframework.data.domain.Sort.by("id").ascending())){
    if(selected.size()>=3)break;
    boolean similar=brandIds.contains(candidate.getBrand().getId()) ||
      candidate.getCategories().stream().anyMatch(c->categoryIds.contains(c.getId()));
    if(similar && eligible(candidate))selected.putIfAbsent(candidate.getId(),candidate);
   }
  }
  return selected.values().stream().limit(3).map(p->catalog.published(p.getId())).toList();
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
