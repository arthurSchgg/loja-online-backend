package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.OfferingOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record OfferingOptionsRequest(
    @NotNull @Size(max = 48) List<@NotNull @Valid Option> options
) {
    public record Option(
        @NotNull OfferingOption.Kind kind,
        @NotBlank @Size(max = 120) String label,
        @Min(0) int position,
        boolean active
    ) {}
}
