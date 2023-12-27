package br.com.desafio.DTO.Auth;

import br.com.desafio.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotBlank(message = "O campo email é obrigatorio") @Email(message = "Email Invalido") @JsonProperty("email") String email,
                          @NotBlank(message = "O campo senha é obrigatorio") @JsonProperty("senha") String password,
                          @NotNull(message = "O campo tipo é obrigatorio") @JsonProperty("tipo") UserRole role) {
}
