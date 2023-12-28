package br.com.desafio.security;

import br.com.desafio.entity.User;
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
    
    @Value("${jwt-time-expiration-in-minutes}")
    private Integer TOKEN_EXPIRATION_TIME;

    public String generateToken(User user) {
        try {

            log.info("Gerando token para usuario com id:{}", user.getId());

            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("auth")
                    .withSubject(user.getEmail())
                    .withClaim("role", user.getUserRole().toString())
                    .withExpiresAt(this.getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("ERROR WHILE GENERATING TOKEN", e);
        }
    }

    public ResponseEntity<String> validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        log.info("Validando token:{}", token);

        return ResponseEntity.ok(JWT.require(algorithm).withIssuer("auth").build().verify(token).getSubject());
    }

    public boolean checkIfUserIsAdmin(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        DecodedJWT decodedJWT = JWT.require(algorithm)
                .withIssuer("auth")
                .build()
                .verify(token.substring(7));

        log.info("Checando se usuario com token:{} é administrador", token);

        return decodedJWT.getClaim("role").asString().equals("ADMIN");
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_TIME).toInstant(ZoneOffset.of("-03:00"));
    }

}
