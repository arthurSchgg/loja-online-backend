package br.com.lojinhadakatia.experience.service;

import br.com.lojinhadakatia.catalog.entity.Product;
import br.com.lojinhadakatia.catalog.repository.ProductRepository;
import br.com.lojinhadakatia.catalog.service.PromotionService;
import br.com.lojinhadakatia.experience.dto.*;
import br.com.lojinhadakatia.experience.entity.*;
import br.com.lojinhadakatia.experience.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import jakarta.persistence.criteria.Join;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional(readOnly=true)
public class RecommendationService {
    private final ExperienceRepository experiences;
    private final ExperienceProductRecommendationRepository recommendations;
    private final ProductRepository products;
    private final PromotionService promotions;

    public RecommendationService(ExperienceRepository experiences,
        ExperienceProductRecommendationRepository recommendations,
        ProductRepository products,PromotionService promotions){
        this.experiences=experiences;this.recommendations=recommendations;
        this.products=products;this.promotions=promotions;
    }

    public List<ProductSuggestion> suggestions(Long experienceId,String resultKey){
        var defined=recommendations.findByExperienceIdAndResultKeyOrderByPositionAscIdAsc(experienceId,resultKey);
        List<ProductSuggestion> result=new ArrayList<>();
        Set<Long> selected=new HashSet<>();
        for(var item:defined){
            if(result.size()>=3)break;
            Product p=item.getProduct();
            if(!eligible(p))p=fallback(p,selected);
            if(p!=null&&eligible(p)&&selected.add(p.getId()))result.add(toSuggestion(p));
        }
        return result;
    }

    /** Salva escolhas manuais ordenadas; produto em rascunho pode ser escolhido e fica oculto ao visitante. */
    @Transactional
    public List<ProductSuggestion> replace(Long experienceId,String resultKey,RecommendationUpdateRequest request){
        Experience experience=experiences.findById(experienceId)
            .orElseThrow(()->new ResourceNotFoundException("Experiência não encontrada."));
        boolean resultExists=experience.getResults().stream().anyMatch(x->x.getResultKey().equals(resultKey));
        if(!resultExists)throw new BusinessException("Chave de resultado não pertence à experiência.");
        List<Long> ids=request.productIds();
        if(new HashSet<>(ids).size()!=ids.size())
            throw new BusinessException("Não repita produtos na recomendação.");
        List<Product> found=new ArrayList<>();
        for(Long id:ids){
            Product product=products.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Produto não encontrado."));
            found.add(product);
        }
        recommendations.deleteByExperienceIdAndResultKey(experienceId,resultKey);
        recommendations.flush();
        for(int i=i<found.size();i++)
            recommendations.save(new ExperienceProductRecommendation(experience,resultKey,found.get(i),i));
        recommendations.flush();
        return suggestions(experienceId,resultKey);
    }

    @Transactional
    public void clearForExperience(Long experienceId){
        recommendations.deleteByExperienceId(experienceId);
        recommendations.flush();
    }

    public boolean hasRecommendationsForProduct(Long productId){
        return recommendations.existsByProductId(productId);
    }

    private boolean eligible(Product p){
        return p.isPublished()&&p.isAvailable()&&p.getStockQuantity()>0;
    }

    /** Primeiro mesma marca; depois uma categoria em comum, sem repetir produtos. */
    private Product fallback(Product unavailable,Set<Long> excluded){
        Long brandId=unavailable.getBrand().getId();
        var byBrand=products.findAll((root,query,cb)->cb.and(
            cb.isTrue(root.get("published")),cb.isTrue(root.get("available")),
            cb.greaterThan(root.<Integer>get("stockQuantity"),0),
            cb.equal(root.get("brand").get("id"),brandId)),
            PageRequest.of(0,30,Sort.by("id"))).getContent();
        for(Product candidate:byBrand)if(!excluded.contains(candidate.getId()))return candidate;

        var categoryIds=unavailable.getCategories().stream().map(x->x.getId()).toList();
        if(categoryIds.isEmpty())return null;
        var byCategory=products.findAll((root,query,cb)->{
            query.distinct(true);
            var cat=root.join("categories");
            return cb.and(cb.isTrue(root.get("published")),cb.isTrue(root.get("available")),
                cb.greaterThan(root.<Integer>get("stockQuantity"),0),
                cat.get("id").in(categoryIds));
        },PageRequest.of(0,30,Sort.by("id"))).getContent();
        for(Product candidate:byCategory)if(!excluded.contains(candidate.getId()))return candidate;
        return null;
    }

    private ProductSuggestion toSuggestion(Product p){
        String image=p.getImages().isEmpty()?null:p.getImages().get(0).getImageUrl();
        return new ProductSuggestion(p.getId(),p.getName(),promotions.effectivePrice(p),image);
    }
}
