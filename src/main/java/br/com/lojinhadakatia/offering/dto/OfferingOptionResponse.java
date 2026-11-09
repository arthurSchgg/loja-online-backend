package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.OfferingOption;

public record OfferingOptionResponse(OfferingOption.Kind kind, String label,
                                     int position, boolean active) {}
