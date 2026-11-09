package br.com.lojinhadakatia.experience.dto;

import java.util.List;
public record ResultRecommendationsResponse(String resultKey, List<Long> productIds) {}
