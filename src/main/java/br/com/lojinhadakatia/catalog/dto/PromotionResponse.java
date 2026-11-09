package br.com.lojinhadakatia.catalog.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PromotionResponse(Long id,Long productId,BigDecimal promotionalPrice,
                                Instant startsAt,Instant endsAt) {}
