package br.com.desafio.Product.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "O campo nome é obrigatorio")
    @Size(min = 8, max = 50, message = "O campo nome precisa de no minimo 8 e no maximo 50 caracteres")
    @JsonProperty("nome")
    private String name;
    @JsonProperty("sku")
    @NotBlank(message = "O campo sku é obrigatorio")
    private String SKU;
    @NotNull(message = "O campo categoria é obrigatorio")
    @JsonProperty("categoria_id")
    private Long categoryId;
    @NotNull(message = "O campo custo é obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "")
    @JsonProperty("custo")
    private BigDecimal cost;
    @NotNull(message = "O campo icms é obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "")
    @JsonProperty("icms")
    private BigDecimal ICMS;
    @NotNull(message = "O campo valor é obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "")
    @JsonProperty("valor")
    private BigDecimal revenueValue;
    @JsonProperty("imagem")
    private String image;
    @NotNull(message = "O campo usuario é obrigatorio")
    @JsonProperty("usuario_id")
    private Long userId;
    @NotNull(message = "O campo quantidade é obrigatorio")
    @JsonProperty("quantidade")
    private Long quantity;
}
