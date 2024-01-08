package br.com.desafio.util;

import br.com.desafio.RefreshToken.entity.RefreshToken;
import br.com.desafio.User.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RefreshTokenCreator {
    public static RefreshToken createRefreshTokenAdmin() {
        return RefreshToken.builder()
                .email("teste@gmail.com")
                .userRole(UserRole.ADMIN)
                .token("token")
                .expiryDate(LocalDateTime.now().plusSeconds(30).toInstant(ZoneOffset.of("-03:00")))
                .build();
    }

    public static RefreshToken createRefreshTokenEstoquista() {
        return RefreshToken.builder()
                .email("teste2@gmail.com")
                .userRole(UserRole.ESTOQUISTA)
                .token("token")
                .expiryDate(LocalDateTime.now().plusSeconds(30).toInstant(ZoneOffset.of("-03:00")))
                .build();
    }
}
