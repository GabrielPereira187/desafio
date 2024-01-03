package br.com.desafio.DTO.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private Long productId;
    private String name;
    private boolean activeProduct;
    private String SKU;
    private Long categoryId;
    private BigDecimal cost;
    private BigDecimal ICMS;
    private BigDecimal revenueValue;
    private String image;
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime entryDate;
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long userId;
    private Long quantity;
}
