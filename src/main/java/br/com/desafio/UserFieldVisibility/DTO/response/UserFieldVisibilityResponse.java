package br.com.desafio.UserFieldVisibility.DTO.response;

import lombok.Builder;

@Builder
public record UserFieldVisibilityResponse(Long id, String name, boolean isVisible) {
}
