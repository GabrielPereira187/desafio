package br.com.desafio.security;

import br.com.desafio.entity.RefreshToken;
import br.com.desafio.entity.User;
import br.com.desafio.entity.enums.UserRole;
import br.com.desafio.repository.RefreshTokenRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
@Slf4j
public class TokenService {

    private final String secret = "secret";

    private final RefreshTokenRepository refreshTokenRepository;

    private static final String ISSUER = "auth";

    private static final String ROLE = "role";
    
    @Value("${jwt-time-expiration-in-minutes}")
    private Integer TOKEN_EXPIRATION_TIME;

    public TokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateToken(User user) {
        try {
            log.info("Gerando token para usuario com id:{}", user.getId());

            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withClaim(ROLE, user.getUserRole().toString())
                    .withExpiresAt(this.getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("ERROR WHILE GENERATING TOKEN", e);
        }
    }

    public ResponseEntity<String> validateToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).get();

        return ResponseEntity.ok(refreshToken.getEmail());
    }

    public boolean checkIfUserIsAdmin(String token) {
        return refreshTokenRepository.findByToken(token.substring(7)).get().getUserRole().equals(UserRole.ADMIN);
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_TIME).toInstant(ZoneOffset.of("-03:00"));
    }

}
