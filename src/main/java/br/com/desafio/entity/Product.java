package br.com.desafio.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "product_table")
@Data
@Audited
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name", nullable = false)
    @Size(min = 8, max = 50, message = "{campo.nome.size}")
    private String name;
    @Column(name = "product_active")
    private boolean activeProduct;
    @Column(name = "product_sku", unique = true, nullable = false)
    private String SKU;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Long categoryId;
    @Column(name = "product_cost", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "{campo.custo.negativo}")
    private BigDecimal cost;
    @Column(name = "product_ICMS", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "{campo.icms.negativo}")
    private BigDecimal ICMS;
    @Column(name = "product_revenue_value", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "{campo.revenda.negativo}")
    private BigDecimal revenueValue;
    @Column(name = "product_image", nullable = false, columnDefinition = "LONGTEXT")
    @Lob
    private String image;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "product_created_at")
    private LocalDateTime entryDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "product_updated_at")
    private LocalDateTime updatedAt;
    @JoinColumn(name = "userId", referencedColumnName = "user_id", nullable = false)
    private Long userId;
    @Column(name = "product_quantity", nullable = false)
    @Min(value = 0, message = "{campo.quantidade.negativo}")
    private Long quantity;

    @PrePersist
    public void onCreate() {
        this.entryDate = LocalDateTime.now();
        this.activeProduct = true;
    }

}
