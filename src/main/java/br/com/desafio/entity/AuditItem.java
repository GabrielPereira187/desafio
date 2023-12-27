package br.com.desafio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class AuditItem {
    private Integer auditId;
    private Long productId;
    private String action;
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime date;
    private String username;
}
