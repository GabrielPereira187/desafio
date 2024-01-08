package br.com.desafio.service;

import br.com.desafio.RefreshToken.entity.RefreshToken;
import br.com.desafio.User.entity.User;
import br.com.desafio.RefreshToken.repository.RefreshTokenRepository;
import br.com.desafio.RefreshToken.service.TokenService;
import br.com.desafio.util.RefreshTokenCreator;
import br.com.desafio.util.UserCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
    User user;
    @InjectMocks
    TokenService tokenService;
    @Mock
    RefreshTokenRepository refreshTokenRepository;
    RefreshToken refreshTokenAdmin;
    RefreshToken refreshTokenEstoquista;
    private static final String TOKEN_ESTOQUISTA_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidGVzdGUyM0BnbWFpbC5jb20iLCJyb2xlIjoiRVNUT1FVSVNUQSIsImV4cCI6MTcwNDMxOTEyNn0.GGiOiGepX_dqBmWo4-zueEZQt7tI4IDu748p-NR-b2g";
    private static final String TOKEN_ADMIN_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoIiwic3ViIjoidGVzdGVAZ21haWwuY29tIiwicm9sZSI6IkFETUlOIiwiZXhwIjoxNzA0MzE4OTk0fQ.k3g8OWiuMij2TGL9cjMFVRPSmWPOWqXF6YWaNicNEyg";

    @BeforeEach
    void setUp() {
        user = UserCreator.createUserAdmin();
        refreshTokenAdmin = RefreshTokenCreator.createRefreshTokenAdmin();
        refreshTokenEstoquista = RefreshTokenCreator.createRefreshTokenEstoquista();
    }

    @Test
    void shouldGenerateToken() {
        String token = tokenService.generateToken(user);

        Assertions.assertNotNull(token);
    }

    @Test
    void shouldReturnAdmin() {
        when(refreshTokenRepository.findByToken(TOKEN_ADMIN_EXAMPLE)).thenReturn(refreshTokenAdmin);

        boolean isAdmin = tokenService.checkIfUserIsAdmin("Bearer " + TOKEN_ADMIN_EXAMPLE);

        Assertions.assertTrue(isAdmin);
    }

    @Test
    void shouldReturnEstoquista() {
        when(refreshTokenRepository.findByToken(TOKEN_ESTOQUISTA_EXAMPLE)).thenReturn(refreshTokenEstoquista);

        boolean isAdmin = tokenService.checkIfUserIsAdmin("Bearer " + TOKEN_ESTOQUISTA_EXAMPLE);

        Assertions.assertFalse(isAdmin);
    }

}
