package br.com.desafio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuditItem {

    private Long productId;
    private String entityName;
    private String action;
    private String date;
    private String username;
}
