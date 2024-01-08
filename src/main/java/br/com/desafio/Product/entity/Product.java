package br.com.desafio.Product.entity;

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
@Entity(name = "tbl_product")
@Data
@Audited
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name", nullable = false)
    @Size(min = 8, max = 50, message = "Nome tem que ter entre 8 e 50 caracteres")
    private String name;
    @Column(name = "product_active")
    private boolean activeProduct;
    @Size(min = 2, max = 20, message = "SKU tem que ter entre 2 e 20 caracteres")
    @Column(name = "product_sku", unique = true, nullable = false)
    private String SKU;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private Long categoryId;
    @Column(name = "product_cost", nullable = false)
    private BigDecimal cost;
    @Column(name = "product_ICMS", nullable = false)
    private BigDecimal ICMS;
    @Column(name = "product_revenue_value", nullable = false)
    private BigDecimal revenueValue;
    @Column(name = "product_image", nullable = false)
    private String image;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "product_createdAt")
    private LocalDateTime entryDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "product_updatedAt")
    private LocalDateTime updatedAt;
    @JoinColumn(name = "userId", referencedColumnName = "user_id", nullable = false)
    private Long userId;
    @Column(name = "product_quantity", nullable = false)
    private Long quantity;

    @PrePersist
    public void onCreate() {
        this.entryDate = LocalDateTime.now();
        this.activeProduct = true;
    }



}
