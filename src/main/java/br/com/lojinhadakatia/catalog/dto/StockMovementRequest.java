package br.com.lojinhadakatia.catalog.dto;

import br.com.lojinhadakatia.catalog.entity.ProductStockMovement;
import jakarta.validation.constraints.*;

/** Para ADJUSTMENT, quantity é o novo saldo absoluto. Para ENTRY/EXIT é o delta. */
public record StockMovementRequest(
    @NotNull ProductStockMovement.Type type,
    @NotNull @Min(0) Integer quantity,
    @NotBlank @Size(max = 500) String reason
) {}
