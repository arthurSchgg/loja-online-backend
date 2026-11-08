package br.com.lojinhadakatia.catalog.dto;

/** Exposto apenas na área administrativa. */
public record StockBalanceResponse(Long productId, int quantity, boolean available) {}
