package br.com.desafio.Audit.DTO.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AuditChanges(String description, List<ChangesResponse> changes) {
}
