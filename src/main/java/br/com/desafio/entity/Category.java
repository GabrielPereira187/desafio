package br.com.desafio.entity;

import br.com.desafio.entity.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "tbl_category")
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    @NotNull
    @Column(name = "category_name", unique = true)
    @Size(min = 5, max = 50, message = "error")
    @JsonProperty("categoryName")
    private String categoryName;
    @NotNull
    @Column(name = "category_active")
    private boolean activeCategory;
    @NotNull
    @Column(name = "category_type")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
}
