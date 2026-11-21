package br.com.lojinhadakatia;

import br.com.lojinhadakatia.catalog.entity.Product;
import br.com.lojinhadakatia.catalog.entity.ProductPromotion;
import br.com.lojinhadakatia.catalog.repository.ProductRepository;
import br.com.lojinhadakatia.catalog.repository.ProductPromotionRepository;
import br.com.lojinhadakatia.catalog.service.PromotionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionPriceBatchTest {
    @Test void oneLookupResolvesThePricesForTheWholePageAtOneInstant() {
        ProductRepository products=mock(ProductRepository.class);
        ProductPromotionRepository promotions=mock(ProductPromotionRepository.class);
        var service=new PromotionService(products,promotions);
        var discounted=mock(Product.class);
        var regular=mock(Product.class);
        when(discounted.getId()).thenReturn(1L);
        when(discounted.hasConfirmedPrice()).thenReturn(true);
        when(discounted.getPrice()).thenReturn(new BigDecimal("80"));
        when(regular.getId()).thenReturn(2L);
        when(regular.hasConfirmedPrice()).thenReturn(true);
        when(regular.getPrice()).thenReturn(new BigDecimal("50"));
        var promotion=mock(ProductPromotion.class);
        when(promotion.getProduct()).thenReturn(discounted);
        when(promotion.getPromotionalPrice()).thenReturn(new BigDecimal("30"));
        Instant now=Instant.parse("2026-11-21T12:00:00Z");
        when(promotions.findEffectiveForProducts(List.of(1L,2L),now)).thenReturn(List.of(promotion));
        var active=service.activePrices(List.of(1L,2L),now);
        assertEquals(new BigDecimal("30"),service.resolveEffectivePrice(discounted,active));
        assertEquals(new BigDecimal("50"),service.resolveEffectivePrice(regular,active));
        verify(promotions,times(1)).findEffectiveForProducts(List.of(1L,2L),now);
        verifyNoMoreInteractions(promotions);
    }
}
