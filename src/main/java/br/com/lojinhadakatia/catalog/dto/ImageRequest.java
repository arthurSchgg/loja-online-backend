package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.constraints.*;
/** Foto da biblioteca, arquivo do catálogo já distribuído ou endereço HTTPS de imagem. */
public record ImageRequest(@NotBlank @Size(max=2048) String url,
                           @PositiveOrZero int position,
                           @Size(max=180) String altText) {}
