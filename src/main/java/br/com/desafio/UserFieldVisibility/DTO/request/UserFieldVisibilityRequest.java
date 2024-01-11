package br.com.desafio.UserFieldVisibility.DTO.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor @Data
public class UserFieldVisibilityRequest {
    @NotBlank(message = "O campo nome é obrigatório")
    @Size(min = 2, max = 30, message = "O nome do campo deve ter entre 2 e 30 caracteres")
    private String fieldName;
}
