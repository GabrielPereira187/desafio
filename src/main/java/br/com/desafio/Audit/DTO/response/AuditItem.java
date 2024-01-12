package br.com.desafio.Audit.DTO.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuditItem (Integer auditId,
        Long productId,
        String action,
        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime date,
        String username){
}
