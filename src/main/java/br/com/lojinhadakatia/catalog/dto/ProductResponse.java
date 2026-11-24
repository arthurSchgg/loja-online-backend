package br.com.lojinhadakatia.catalog.dto;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
/** Representação de produto; nunca retorna a entidade JPA. */
public record ProductResponse(Long id,String name,String description,BigDecimal price,
                              boolean available,boolean published,LookupDto brand,
                              List<LookupDto> categories,List<ImageResponse> images,BigDecimal effectivePrice,
                              ProductPriceStatus priceStatus,
                              @Schema(description="Saldo físico no admin; quantidade disponível para consulta na vitrine.")
                              int stockQuantity,
                              @Schema(description="Disponibilidade manual configurada; presente somente no admin.",nullable=true)
                              @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
                              Boolean manualAvailable) {}
