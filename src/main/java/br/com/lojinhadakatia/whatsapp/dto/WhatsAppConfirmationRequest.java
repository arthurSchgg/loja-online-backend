package br.com.lojinhadakatia.whatsapp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Confirmação sem pedido ou pagamento. O valor exibido ao visitante precisa
 * coincidir com o preço atual verificado novamente pelo servidor.
 */
public record WhatsAppConfirmationRequest(
    @NotNull Type type,
    @NotNull @Positive Long id,
    @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal expectedPrice,
    @Size(max = 3) Map<@NotBlank String, @NotBlank String> customizations
) {
    public enum Type { PRODUCT, OFFERING }
}
