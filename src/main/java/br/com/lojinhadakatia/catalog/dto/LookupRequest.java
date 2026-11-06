package br.com.lojinhadakatia.catalog.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/** Dados para cadastrar ou renomear marca/categoria. */
public record LookupRequest(@NotBlank @Size(max=120) String name) {}
