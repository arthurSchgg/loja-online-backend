package br.com.lojinhadakatia.whatsapp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

/** Consulta comercial, não persistência de pedidos. Seleções nunca controlam preço. */
public record WhatsAppConfirmationRequest(
 @NotNull Type type,
 @NotNull @Positive Long id,
 @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal expectedPrice,
 @Size(max=3) Map<@NotBlank String,@NotBlank @Size(max=120) String> selections
) {
 public enum Type { PRODUCT, OFFERING }
 public WhatsAppConfirmationRequest(Type type,Long id,BigDecimal expectedPrice){
  this(type,id,expectedPrice,Map.of());
 }
}
