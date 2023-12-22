package br.com.desafio.DTO.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
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
    private Long userId;
}
