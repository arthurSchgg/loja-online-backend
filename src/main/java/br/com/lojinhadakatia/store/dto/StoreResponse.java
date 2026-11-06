package br.com.lojinhadakatia.store.dto;
/** Contatos publicados, sem credenciais ou atributos internos. */
public record StoreResponse(String displayName,String slogan,String description,
                            String whatsapp,String instagramUrl,String email) {}
