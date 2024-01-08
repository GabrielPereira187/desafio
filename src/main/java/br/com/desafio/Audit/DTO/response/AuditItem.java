package br.com.desafio.Audit.DTO.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
public record AuditItem (Integer auditId,
        Long productId,
        String action,
        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime date,
        String username){
}
