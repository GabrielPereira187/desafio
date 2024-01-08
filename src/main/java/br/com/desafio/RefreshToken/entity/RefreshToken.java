package br.com.desafio.RefreshToken.entity;

import br.com.desafio.User.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Entity(name = "tbl_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "token_expiryDate")
    private Instant expiryDate;
    @Column(name = "token_userId")
    private Long userInfo;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_userRole")
    private UserRole userRole;
    @Column(name = "token_userEmail")
    private String email;
}
