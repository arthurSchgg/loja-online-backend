package br.com.lojinhadakatia.experience.dto;

import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import java.util.List;

/** Perfil sempre presente; pode não haver produtos disponíveis para recomendar. */
public record EvaluationResponse(
    String key, String title, String description, List<ProductResponse> recommendations
) {}
