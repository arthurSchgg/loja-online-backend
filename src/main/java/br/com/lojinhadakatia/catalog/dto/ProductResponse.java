package br.com.lojinhadakatia.catalog.dto;
import java.math.BigDecimal;
import java.util.List;
/** Representação de produto; nunca retorna a entidade JPA. */
public record ProductResponse(Long id,String name,String description,BigDecimal price,
                              boolean available,boolean published,LookupDto brand,
                              List<LookupDto> categories,List<ImageResponse> images,BigDecimal effectivePrice) {}
