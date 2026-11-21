package br.com.lojinhadakatia.catalog.dto;

/** The administrator can see and restore inactive lookup entries. */
public record AdminLookupDto(Long id, String name, boolean active) {}
