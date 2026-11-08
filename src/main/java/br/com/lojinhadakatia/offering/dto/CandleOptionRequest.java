package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.CandleOption;
import jakarta.validation.constraints.*;

public record CandleOptionRequest(
    @NotNull CandleOption.Kind kind,
    @NotBlank @Size(max = 100) String value,
    boolean active
) {}
