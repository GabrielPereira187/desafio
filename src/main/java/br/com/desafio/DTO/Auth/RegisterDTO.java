package br.com.desafio.DTO.Auth;

import br.com.desafio.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RegisterDTO(@NotNull String email, @NotNull String password, @NotNull UserRole role) {
}
