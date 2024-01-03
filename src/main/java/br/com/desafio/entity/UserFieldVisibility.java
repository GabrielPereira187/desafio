package br.com.desafio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_visibility_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserFieldVisibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    @Column(name = "isVisibleForEstoquista")
    private boolean isVisible = true;
}
