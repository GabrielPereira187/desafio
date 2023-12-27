package br.com.desafio.DTO.Response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AggregatedValueResponse(Long productId, BigDecimal cost, BigDecimal totalCost, BigDecimal forecast) {
}
