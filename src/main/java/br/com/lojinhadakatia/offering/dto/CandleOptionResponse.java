package br.com.lojinhadakatia.offering.dto;

import br.com.lojinhadakatia.offering.entity.CandleOption;

public record CandleOptionResponse(Long id, CandleOption.Kind kind, String value, boolean active) {
    public static CandleOptionResponse from(CandleOption option) {
        return new CandleOptionResponse(option.getId(), option.getKind(), option.getValue(), option.isActive());
    }
}
