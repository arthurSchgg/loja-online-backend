package br.com.lojinhadakatia.whatsapp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;
import br.com.lojinhadakatia.offering.entity.CandleOption;

/**
 * Confirmação sem pedido ou pagamento. O valor exibido ao visitante precisa
 * coincidir com o preço atual verificado novamente pelo servidor.
 */
public record WhatsAppConfirmationRequest(
    @NotNull Type type,
    @NotNull @Positive Long id,
    @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal expectedPrice,
    Map<CandleOption.Kind, @NotBlank @Size(max = 100) String> options
) {
    public WhatsAppConfirmationRequest(Type type, Long id, BigDecimal expectedPrice) {
        this(type, id, expectedPrice, null);
    }
    public enum Type { PRODUCT, OFFERING }
}
