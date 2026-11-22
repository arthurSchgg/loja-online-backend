package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
/** Entrada administrativa: published=false cria rascunhos sem exposição pública. */
public record ProductRequest(
    @NotBlank @Size(max=180) String name,
    @Size(max=4000) String description,
    @Schema(description="Preço base positivo; null somente quando priceStatus=ON_REQUEST.",nullable=true)
    @DecimalMin(value="0.00",inclusive=false) @Digits(integer=10,fraction=2) BigDecimal price,
    @NotNull @Positive Long brandId,
    @NotNull Set<@NotNull @Positive Long> categoryIds,
    boolean available,
    boolean published,
    @NotNull @Size(max=12) List<@NotNull @Valid ImageRequest> images,
    @Schema(description="CONFIRMED para valor informado; ON_REQUEST exige price=null.")
    ProductPriceStatus priceStatus
) {
    // Existing clients may omit the status when submitting a positive price.
    // A missing price must be an explicit administrative choice.
    @AssertTrue(message="Informe um preço positivo ou selecione valor sob consulta explicitamente.")
    public boolean isPriceSelectionValid() {
        if (priceStatus == ProductPriceStatus.ON_REQUEST) return price == null;
        return price != null && price.signum() > 0;
    }
}
