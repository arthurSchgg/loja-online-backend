package br.com.lojinhadakatia.catalog.dto;
import java.math.BigDecimal;
import java.time.Instant;
/** Closure fields are available only on administrative routes. */
public record PromotionResponse(Long id,Long productId,BigDecimal promotionalPrice,
    Instant startsAt,Instant endsAt,Instant closedAt,String closedReason,String closedBy) {}
