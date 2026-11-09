package br.com.lojinhadakatia.experience.dto;

import jakarta.validation.constraints.*;
import java.util.List;

/** Administradora escolhe até três produtos por perfil, na ordem desejada. */
public record ResultRecommendationsRequest(
    @NotNull @Size(max = 3) List<@NotNull @Positive Long> productIds
) {}
