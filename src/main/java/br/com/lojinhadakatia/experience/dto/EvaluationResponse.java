package br.com.lojinhadakatia.experience.dto;
import java.util.List;
/** Resultado calculado para um conjunto de alternativas válidas. */
public record EvaluationResponse(String key,String title,String description,List<ProductSuggestion> recommendations) {}
