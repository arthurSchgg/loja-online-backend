package br.com.lojinhadakatia.experience.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/** Ordered IDs selected by the administrator; public eligibility is checked at request time. */
public record ResultRecommendationsRequest(
    @NotNull @Size(max = 3) List<@NotNull @Positive Long> productIds
) {}
