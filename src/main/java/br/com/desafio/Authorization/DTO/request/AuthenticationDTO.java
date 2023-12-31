package br.com.desafio.Authorization.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record AuthenticationDTO(@NotBlank(message = "O campo email é obrigatorio") @Email(message = "E-mail inválido") @JsonProperty("email") String email,
                                @NotBlank(message = "O campo senha é obrigatorio") @JsonProperty("senha") String password) {
}
