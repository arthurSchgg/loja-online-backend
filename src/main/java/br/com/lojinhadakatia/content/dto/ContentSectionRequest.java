package br.com.lojinhadakatia.content.dto;
import jakarta.validation.constraints.*;
/** Texto simples, sem HTML renderizável; front trata exibição segura. */
public record ContentSectionRequest(
 @NotBlank @Pattern(regexp="^[a-z0-9-]{3,80}$") String slug,
 @NotBlank @Size(max=180) String title,
 @Size(max=8000) String body,
 @Size(max=2048) String imageUrl,
 @Min(0) int position,
 boolean published
) {}
