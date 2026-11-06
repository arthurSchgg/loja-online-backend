package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
/** Entrada administrativa: published=false cria rascunhos sem exposição pública. */
public record ProductRequest(
    @NotBlank @Size(max=180) String name,
    @Size(max=4000) String description,
    @NotNull @DecimalMin("0.00") @Digits(integer=10,fraction=2) BigDecimal price,
    @NotNull Long brandId,
    @NotNull Set<@NotNull Long> categoryIds,
    boolean available,
    boolean published,
    @NotNull List<@Valid ImageRequest> images
) {}
