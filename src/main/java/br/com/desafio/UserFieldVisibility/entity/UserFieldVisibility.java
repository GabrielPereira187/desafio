package br.com.desafio.UserFieldVisibility.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "tbl_user_visibility")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserFieldVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_visibility_id")
    private Long id;
    @Column(name = "user_visibility_fieldName", nullable = false)
    private String fieldName;
    @Column(name = "user_visibility_isVisible", nullable = false)
    private boolean isVisible = true;
}
