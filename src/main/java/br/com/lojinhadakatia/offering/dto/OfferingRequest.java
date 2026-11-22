package br.com.lojinhadakatia.offering.dto;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import br.com.lojinhadakatia.catalog.dto.ImageRequest;
/** Conteúdo real da oferta, aprovado pela proprietária; preço opcional. */
public record OfferingRequest(
 @NotBlank @Size(max=180) String title,
 @Size(max=4000) String description,
 @NotNull OfferingType type,
 @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal price,
 @Size(max=2048) String imageUrl,
 boolean published,
 Boolean available,
 @Size(max=12) List<@NotNull @Valid ImageRequest> images
) {
 public OfferingRequest(String title, String description, OfferingType type, BigDecimal price,
                        String imageUrl, boolean published) {
  this(title, description, type, price, imageUrl, published, null, null);
 }
 public OfferingRequest(String title, String description, OfferingType type, BigDecimal price,
                        String imageUrl, boolean published, Boolean available) {
  this(title,description,type,price,imageUrl,published,available,null);
 }
}
