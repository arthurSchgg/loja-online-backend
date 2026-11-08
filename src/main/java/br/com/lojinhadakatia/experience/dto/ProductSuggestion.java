package br.com.lojinhadakatia.experience.dto;

import java.math.BigDecimal;

public record ProductSuggestion(Long id,String name,BigDecimal effectivePrice,String imageUrl) {}
