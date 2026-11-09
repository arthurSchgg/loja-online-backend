package br.com.lojinhadakatia.offering.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OfferingOptionsUpdateRequest(
 @NotNull @Size(max=100) List<@NotNull @Valid OfferingOptionRequest> options
) {}
