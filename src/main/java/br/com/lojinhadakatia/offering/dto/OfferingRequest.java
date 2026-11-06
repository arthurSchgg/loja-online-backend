package br.com.lojinhadakatia.offering.dto;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
/** Conteúdo real da oferta, aprovado pela proprietária; preço opcional. */
public record OfferingRequest(
 @NotBlank @Size(max=180) String title,
 @Size(max=4000) String description,
 @NotNull OfferingType type,
 @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal price,
 @Size(max=2048) String imageUrl,
 boolean published
) {}
