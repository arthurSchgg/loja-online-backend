package br.com.lojinhadakatia.catalog.service;

import br.com.lojinhadakatia.catalog.dto.*;
import br.com.lojinhadakatia.catalog.entity.*;
import br.com.lojinhadakatia.catalog.repository.*;
import br.com.lojinhadakatia.shared.exception.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
        List<ProductPromotion> active=promotions.findEffective(product.getId(),Instant.now());
        if(active.size()>1)throw new IllegalStateException("Promoções concorrentes para o produto.");
        return active.isEmpty()?product.getPrice():active.get(0).getPromotionalPrice();
    }

    private void validate(Product product,PromotionRequest r,Long except){
        if(!r.endsAt().isAfter(r.startsAt()))
            throw new BusinessException("O fim da promoção deve ser posterior ao início.");
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
