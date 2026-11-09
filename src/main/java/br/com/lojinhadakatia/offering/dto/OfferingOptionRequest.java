package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.OfferingOption;
import jakarta.validation.constraints.*;

public record OfferingOptionRequest(
 @NotNull OfferingOption.Kind kind,
 @NotBlank @Size(max=120) String value,
 boolean active
) {}
