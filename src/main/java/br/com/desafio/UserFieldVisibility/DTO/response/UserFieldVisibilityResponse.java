package br.com.desafio.UserFieldVisibility.DTO.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UserFieldVisibilityResponse {
    private Long id;
    private String fieldName;
    private boolean isVisible;
}
