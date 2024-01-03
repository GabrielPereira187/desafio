package br.com.desafio.service;

import br.com.desafio.entity.User;
import br.com.desafio.entity.enums.UserRole;
import br.com.desafio.security.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

public class TokenServiceTest {
    User user;
    @Mock
    TokenService tokenService;

    private static final String TOKEN_ESTOQUISTA_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidGVzdGUyM0BnbWFpbC5jb20iLCJyb2xlIjoiRVNUT1FVSVNUQSIsImV4cCI6MTcwNDMxOTEyNn0.GGiOiGepX_dqBmWo4-zueEZQt7tI4IDu748p-NR-b2g";
    private static final String TOKEN_ADMIN_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidGVzdGVAZ21haWwuY29tIiwicm9sZSI6IkFETUlOIiwiZXhwIjoxNzA0MzE4OTk0fQ.k3g8OWiuMij2TGL9cjMFVRPSmWPOWqXF6YWaNicNEyg";

    @BeforeEach
    void setUp() {
        user = User
                .builder()
                .id(1L)
                .email("teste@gmail.com")
                .userRole(UserRole.ADMIN)
                .build();
        tokenService = new TokenService();
    }

    @Test
    void shouldGenerateToken() {
        String token = tokenService.generateToken(user);

        Assertions.assertNotNull(token);
    }

    @Test
    void shouldReturnAdmin() {
        boolean isAdmin = tokenService.checkIfUserIsAdmin("Bearer " + TOKEN_ADMIN_EXAMPLE);

        Assertions.assertTrue(isAdmin);
    }

    @Test
    void shouldReturnEstoquista() {
        boolean isAdmin = tokenService.checkIfUserIsAdmin("Bearer " + TOKEN_ESTOQUISTA_EXAMPLE);

        Assertions.assertFalse(isAdmin);
    }

}
