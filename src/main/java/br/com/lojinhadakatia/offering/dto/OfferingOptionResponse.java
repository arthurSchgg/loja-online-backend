package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.OfferingOption;

public record OfferingOptionResponse(Long id, OfferingOption.Kind kind,String value,boolean active) {}
