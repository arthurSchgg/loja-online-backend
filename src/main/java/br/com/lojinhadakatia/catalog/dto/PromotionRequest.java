package br.com.lojinhadakatia.catalog.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public record PromotionRequest(
    @NotNull @DecimalMin(value="0.00",inclusive=false) @Digits(integer=10,fraction=2)
    BigDecimal promotionalPrice,
    @NotNull Instant startsAt,
    @NotNull Instant endsAt
) {}
