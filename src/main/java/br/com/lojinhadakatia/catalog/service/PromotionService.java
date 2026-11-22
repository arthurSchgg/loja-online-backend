package br.com.lojinhadakatia.catalog.service;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.entity.*;
import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class PromotionService {
    private final ProductRepository products;
    private final ProductPromotionRepository promotions;
    public PromotionService(ProductRepository products,ProductPromotionRepository promotions){
        this.products=products;this.promotions=promotions;
    }

    public List<PromotionResponse> list(Long productId){
        product(productId);
        return promotions.findByProductIdOrderByStartsAtAsc(productId).stream().map(this::response).toList();
    }

    @Transactional
    public PromotionResponse create(Long productId,PromotionRequest request){
        Product p=lockProduct(productId);
        validate(p,request,null);
        return response(promotions.saveAndFlush(new ProductPromotion(p,request.promotionalPrice(),
            request.startsAt(),request.endsAt())));
    }

    @Transactional
    public PromotionResponse update(Long productId,Long promotionId,PromotionRequest request){
        Product product=lockProduct(productId);
        ProductPromotion promotion=promotions.findById(promotionId)
            .filter(x->x.getProduct().getId().equals(productId))
            .orElseThrow(()->new ResourceNotFoundException("Promoção não encontrada."));
        if(promotion.getClosedAt()!=null || !promotion.getStartsAt().isAfter(Instant.now()))
            throw new BusinessException("Somente promoções futuras e não encerradas podem ser editadas.");
        validate(product,request,promotionId);
        promotion.update(request.promotionalPrice(),request.startsAt(),request.endsAt());
        return response(promotions.saveAndFlush(promotion));
    }

    /** Physical deletion would destroy commercial history: use close for active campaigns. */
    @Transactional
    public void delete(Long productId,Long promotionId){
        lockProduct(productId);
        findPromotion(productId,promotionId);
        throw new BusinessException("Exclusão de promoções não permitida. O histórico deve ser preservado.");
    }

    @Transactional
    public PromotionResponse close(Long productId,Long promotionId,PromotionCloseRequest request,String actor){
        lockProduct(productId);
        ProductPromotion promotion=findPromotion(productId,promotionId);
        Instant now=Instant.now();
        if(promotion.getClosedAt()!=null)
            throw new BusinessException("Esta promoção já foi encerrada.");
        if(now.isBefore(promotion.getStartsAt()) || !now.isBefore(promotion.getEndsAt()))
            throw new BusinessException("Somente promoções ativas podem ser encerradas.");
        String reason=request.reason().trim();
        if(reason.isBlank() || reason.length()>500)
            throw new BusinessException("Informe a justificativa do encerramento.");
        if(actor==null || actor.isBlank() || actor.length()>180)
            throw new BusinessException("Administrador inválido.");
        promotion.close(now,reason,actor);
        return response(promotions.saveAndFlush(promotion));
    }

    private ProductPromotion findPromotion(Long productId,Long promotionId){
        return promotions.findById(promotionId)
            .filter(x->x.getProduct().getId().equals(productId))
            .orElseThrow(()->new ResourceNotFoundException("Promoção não encontrada."));
    }

    public boolean hasPromotions(Long productId){return promotions.existsByProductId(productId);}

    public BigDecimal effectivePrice(Product product){
        return effectivePrice(product,Instant.now());
    }

    public BigDecimal effectivePrice(Product product,Instant now){
        if(!product.hasConfirmedPrice())return null;
        return resolveEffectivePrice(product,activePrices(List.of(product.getId()),now));
    }

    /** Resolve every visible row with one promotion query and the same instant as the SQL filter. */
    public Map<Long,BigDecimal> activePrices(Collection<Long> productIds,Instant now){
        if(productIds.isEmpty())return Map.of();
        Map<Long,BigDecimal> active=new HashMap<>();
        for(ProductPromotion promotion:promotions.findEffectiveForProducts(productIds,now)){
            if(active.putIfAbsent(promotion.getProduct().getId(),promotion.getPromotionalPrice())!=null)
                throw new IllegalStateException("Promoções concorrentes para o produto.");
        }
        return active;
    }

    public BigDecimal resolveEffectivePrice(Product product,Map<Long,BigDecimal> activePrices){
        if(!product.hasConfirmedPrice())return null;
        BigDecimal discount=activePrices.get(product.getId());
        // Do not display a legacy inconsistent promotion above the base price.
        return discount!=null && discount.signum()>0 && discount.compareTo(product.getPrice())<0
            ? discount : product.getPrice();
    }

    /** The caller holds the same product write lock used by promotion and stock operations. */
    public void validateBasePriceChange(Product product,BigDecimal nextPrice,Instant now){
        BigDecimal previous=product.getPrice();
        if(previous==null ? nextPrice==null : nextPrice!=null && previous.compareTo(nextPrice)==0)return;
        boolean incompatible=promotions.findByProductIdOrderByStartsAtAsc(product.getId()).stream()
            .anyMatch(promotion->promotion.getClosedAt()==null && promotion.getEndsAt().isAfter(now) &&
                (nextPrice==null || promotion.getPromotionalPrice().compareTo(nextPrice)>=0));
        if(incompatible)
            throw new BusinessException("Revise as promoções ativas ou futuras antes de alterar o preço base.");
    }

    private void validate(Product product,PromotionRequest r,Long except){
        if(!r.endsAt().isAfter(r.startsAt()))
            throw new BusinessException("O fim da promoção deve ser posterior ao início.");
        if(!product.hasConfirmedPrice())
            throw new BusinessException("Informe um preço confirmado para cadastrar uma promoção.");
        if(r.promotionalPrice().compareTo(product.getPrice())>=0)
            throw new BusinessException("Preço promocional deve ser inferior ao preço base.");
        if(promotions.countOverlaps(product.getId(),r.startsAt(),r.endsAt(),except)>0)
            throw new BusinessException("Já existe promoção sobreposta neste período.");
    }
    private Product product(Long id){
        return products.findById(id).orElseThrow(()->new ResourceNotFoundException("Produto não encontrado."));
    }
    private Product lockProduct(Long id){
        return products.findByIdForStockUpdate(id)
            .orElseThrow(()->new ResourceNotFoundException("Produto não encontrado."));
    }
    private PromotionResponse response(ProductPromotion p){
        return new PromotionResponse(p.getId(),p.getProduct().getId(),
            p.getPromotionalPrice(),p.getStartsAt(),p.getEndsAt(),
            p.getClosedAt(),p.getClosedReason(),p.getClosedBy());
    }
}
