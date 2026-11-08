package br.com.lojinhadakatia.experience.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/** Até três preferências manuais por resultado, na ordem indicada. */
public record RecommendationUpdateRequest(
    @NotNull @Size(max=3) List<@NotNull @Positive Long> productIds
) {}
