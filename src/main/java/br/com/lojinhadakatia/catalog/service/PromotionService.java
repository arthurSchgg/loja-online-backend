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
        validate(product,request,promotionId);
        promotion.update(request.promotionalPrice(),request.startsAt(),request.endsAt());
        return response(promotions.saveAndFlush(promotion));
    }

    @Transactional
    public void delete(Long productId,Long promotionId){
        lockProduct(productId);
        ProductPromotion promotion=promotions.findById(promotionId)
            .filter(x->x.getProduct().getId().equals(productId))
            .orElseThrow(()->new ResourceNotFoundException("Promoção não encontrada."));
        promotions.delete(promotion);
    }

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
            p.getPromotionalPrice(),p.getStartsAt(),p.getEndsAt());
    }
}
