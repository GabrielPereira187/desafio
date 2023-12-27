package br.com.desafio.DTO.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ChangesResponse(@JsonProperty("nome_campo") String property,
                              @JsonProperty("valor_antigo") String oldValue,
                              @JsonProperty("novo_valor") String newValue) {
}
