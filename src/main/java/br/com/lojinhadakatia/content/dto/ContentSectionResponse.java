package br.com.lojinhadakatia.content.dto;
public record ContentSectionResponse(Long id,String slug,String title,String body,
                                     String imageUrl,int position,boolean published) {}
