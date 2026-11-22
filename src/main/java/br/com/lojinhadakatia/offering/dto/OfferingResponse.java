package br.com.lojinhadakatia.offering.dto;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import java.math.BigDecimal;
import java.util.List;
import br.com.lojinhadakatia.catalog.dto.ImageResponse;

public record OfferingResponse(Long id, String title, String description, OfferingType type,
                               BigDecimal price, String imageUrl, boolean published, boolean available,
                               List<OfferingOptionResponse> options,List<ImageResponse> images) {
 public OfferingResponse(Long id,String title,String description,OfferingType type,BigDecimal price,
                         String imageUrl,boolean published,boolean available,List<OfferingOptionResponse> options){
  this(id,title,description,type,price,imageUrl,published,available,options,List.of());
 }
}
