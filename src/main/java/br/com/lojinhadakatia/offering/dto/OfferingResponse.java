package br.com.lojinhadakatia.offering.dto;
import br.com.lojinhadakatia.offering.entity.OfferingType;
import java.math.BigDecimal;
import java.util.List;

public record OfferingResponse(Long id, String title, String description, OfferingType type,
                               BigDecimal price, String imageUrl, boolean published,
                               List<OfferingOptionResponse> options) {}
