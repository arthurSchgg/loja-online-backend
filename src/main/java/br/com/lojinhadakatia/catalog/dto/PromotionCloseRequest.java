package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.constraints.*;
/** Reason for ending an active promotion; the original interval is preserved. */
public record PromotionCloseRequest(@NotBlank @Size(max=500) String reason) {}
