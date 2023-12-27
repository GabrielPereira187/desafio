package br.com.desafio.DTO.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
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


@SqlResultSetMapping(
        name = "ProductResponseMapping",
        classes = @ConstructorResult(
                targetClass = ProductResponse.class,
                columns = {
                        @ColumnResult(name = "productId", type = Long.class),
                        @ColumnResult(name = "name", type = String.class),
                        @ColumnResult(name = "activeProduct", type = Boolean.class),
                        @ColumnResult(name = "categoryId", type = Long.class),
                        @ColumnResult(name = "cost", type = BigDecimal.class),
                        @ColumnResult(name = "ICMS", type = BigDecimal.class),
                        @ColumnResult(name = "revenueValue", type = BigDecimal.class),
                        @ColumnResult(name = "image", type = String.class),
                        @ColumnResult(name = "entryDate", type = LocalDateTime.class),
                        @ColumnResult(name = "updatedDate", type = LocalDateTime.class),
                        @ColumnResult(name = "userId", type = Long.class),
                        @ColumnResult(name = "quantity", type = Long.class),
                }
        )
)

@AllArgsConstructor @NoArgsConstructor
@Data
@Builder
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
    private LocalDateTime updatedDate;
    private Long userId;
    private Long quantity;
}
