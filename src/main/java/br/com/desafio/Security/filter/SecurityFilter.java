package br.com.desafio.Security.filter;

import br.com.desafio.RefreshToken.entity.RefreshToken;
import br.com.desafio.RefreshToken.repository.RefreshTokenRepository;
import br.com.desafio.User.service.UserService;
import br.com.desafio.Authorization.service.AuthorizationService;
import br.com.desafio.RefreshToken.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final UserService userService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthorizationService authorizationService;

    @Value("${jwt-time-expiration-in-minutes}")
    private long TOKEN_EXPIRATION_TIME;

    private static final long TIME_IN_SECONDS = 60;

    @Autowired
    public SecurityFilter(TokenService tokenService, UserService userService, RefreshTokenRepository refreshTokenRepository, AuthorizationService authorizationService) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);

        if (token != null) {
            Instant actualInstant = Instant.now();
            RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

            long duration = Duration.between(refreshToken.getExpiryDate(),actualInstant).getSeconds();
            
            if(request.getServletPath().contains("/auth/refreshToken")) {
                authorizationService.refreshToken(refreshToken.getToken()).getStatusCode().value();
                duration = 0;
            }

            if(duration < TOKEN_EXPIRATION_TIME * TIME_IN_SECONDS) {
                String email = tokenService.validateToken(token).getBody();
                UserDetails user = userService.findByEmail(email);

                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken (HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            return token.replace("Bearer ", "");
        }
        return null;
    }
}
