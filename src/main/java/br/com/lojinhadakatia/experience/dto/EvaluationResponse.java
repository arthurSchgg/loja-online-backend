package br.com.lojinhadakatia.experience.dto;

import br.com.lojinhadakatia.catalog.dto.ProductResponse;
import java.util.List;

/** Profile is always returned even if no matching published/in-stock product exists. */
public record EvaluationResponse(String key, String title, String description,
                                 List<ProductResponse> recommendations) {}
