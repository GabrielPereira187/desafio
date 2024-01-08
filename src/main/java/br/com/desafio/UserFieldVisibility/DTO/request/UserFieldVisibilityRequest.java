package br.com.desafio.UserFieldVisibility.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserFieldVisibilityRequest(@NotBlank(message = "O campo nome é obrigatório")
                                         @Size(min = 2, max = 30, message = "O nome do campo deve ter entre 2 e 30 caracteres")
                                         String fieldName) {
}
