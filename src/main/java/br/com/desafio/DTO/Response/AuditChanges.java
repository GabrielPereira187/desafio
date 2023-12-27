package br.com.desafio.DTO.Response;

import lombok.Builder;

import java.util.List;

@Builder
public record AuditChanges(String description, List<ChangesResponse> changes) {
}
