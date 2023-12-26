package br.com.desafio.security;

import br.com.desafio.entity.User;
import br.com.desafio.entity.enums.UserRole;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


@Service
public class TokenService {

    private final String secret = "secret";
    
    @Value("${jwt-time-expiration-in-minutes}")
    private Integer TOKEN_EXPIRATION_TIME;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            List<String> roles = new ArrayList<>();

            if(user.getUserRole().equals(UserRole.ADMIN)) {
                roles.add(UserRole.ADMIN.getRole());
                roles.add(UserRole.ESTOQUISTA.getRole());
            }
            else {
                roles.add(UserRole.ESTOQUISTA.getRole());
            }

            return JWT.create()
                    .withIssuer("auth")
                    .withSubject(user.getEmail())
                    .withClaim("role", roles)
                    .withExpiresAt(this.getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("ERROR WHILE GENERATING TOKEN", e);
        }
    }

    public ResponseEntity<String> validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return ResponseEntity.ok(JWT.require(algorithm).withIssuer("auth").build().verify(token).getSubject());
    }

    private Instant getExpirationDate(){
        return LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_TIME).toInstant(ZoneOffset.of("-03:00"));
    }

}
