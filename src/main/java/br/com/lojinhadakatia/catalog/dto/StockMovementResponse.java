package br.com.lojinhadakatia.catalog.dto;

import br.com.lojinhadakatia.catalog.entity.ProductStockMovement;
import java.time.Instant;

public record StockMovementResponse(
    Long id, ProductStockMovement.Type type, int quantity,
    int resultingQuantity, String reason, String actor, Instant createdAt
) {}
