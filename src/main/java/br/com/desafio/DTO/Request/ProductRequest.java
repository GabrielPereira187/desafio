package br.com.desafio.DTO.Request;

import br.com.desafio.entity.Category;
import br.com.desafio.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductRequest {
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    private String name;
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    @JsonProperty("sku")
    private String SKU;
    @NotNull
    @JsonProperty("category")
    private Long categoryId;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    private BigDecimal cost;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    @JsonProperty("icms")
    private BigDecimal ICMS;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    private BigDecimal revenueValue;
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    private String image;
    @NotNull
    @JsonProperty("user")
    private Long userId;
    @NotNull
    @Column(name = "product_active")
    private boolean activeProduct = true;
}
