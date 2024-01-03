package br.com.desafio.DTO.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticationDTO(@NotBlank(message = "O campo email é obrigatorio") @JsonProperty("email") String email,
                                @NotBlank(message = "O campo senha é obrigatorio") @JsonProperty("senha") String password) {
}
