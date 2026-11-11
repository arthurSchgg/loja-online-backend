package br.com.lojinhadakatia.store.dto;
import jakarta.validation.constraints.*;
/** Dados públicos fornecidos e aprovados pela proprietária. */
public record StoreRequest(
    @NotBlank @Size(max=180) String displayName,
    @Size(max=240) String slogan,
    @Size(max=4000) String description,
    @Pattern(regexp="^\\+?[0-9]{10,15}$",message="Informe DDI, DDD e número com apenas dígitos.") String whatsapp,
    @Size(max=2048) String instagramUrl,
    @Email @Size(max=254) String email,
    @Size(max=500) String openingHours,
    @Size(max=500) String address,
    @Size(max=180) String region
) {}
