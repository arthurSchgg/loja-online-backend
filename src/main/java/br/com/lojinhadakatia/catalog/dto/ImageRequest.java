package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.constraints.*;
/** URL pública HTTPS para uma imagem exibida na vitrine. */
public record ImageRequest(@NotBlank @Size(max=2048) String url,
                           @PositiveOrZero int position,
                           @Size(max=180) String altText) {}
