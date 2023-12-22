package br.com.desafio.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "product_table")
@Data
@Audited
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    private String name;
    @NotNull
    @Column(name = "product_active")
    private boolean activeProduct = true;
    @Column(name = "product_sku", unique = true)
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    private String SKU;
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @NotNull
    private Long categoryId;
    @NotNull
    @Column(name = "product_cost")
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    private BigDecimal cost;
    @NotNull
    @Column(name = "product_ICMS")
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    private BigDecimal ICMS;
    @NotNull
    @Column(name = "product_revenue_value")
    @DecimalMin(value = "0.0", inclusive = false, message = "error")
    private BigDecimal revenueValue;
    @Column(name = "product_image")
    @NotNull
    @Size(min = 5, max = 50, message = "error")
    private String image;
    @NotNull
    private LocalDateTime entryDate = LocalDateTime.now();
    @JoinColumn(name = "userId", referencedColumnName = "user_id")
    private Long userId;
}
