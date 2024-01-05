package br.com.desafio.security;


import br.com.desafio.entity.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    private static final String ADMIN = UserRole.ADMIN.getRole();

    private static final String ESTOQUISTA = UserRole.ESTOQUISTA.getRole();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests( authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.POST, "auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "auth/register").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "auth/refreshToken").hasAnyRole(ADMIN, ESTOQUISTA)
                        .requestMatchers(HttpMethod.POST, "api/v1/product/**").hasAnyRole(ADMIN, ESTOQUISTA)
                        .requestMatchers(HttpMethod.PUT, "api/v1/product/**").hasAnyRole(ADMIN, ESTOQUISTA)
                        .requestMatchers(HttpMethod.GET, "api/v1/product/**").hasAnyRole(ADMIN, ESTOQUISTA)
                        .requestMatchers(HttpMethod.DELETE, "api/v1/product/**").hasAnyRole(ADMIN, ESTOQUISTA)
                        .requestMatchers(HttpMethod.POST, "api/v1/userVisibility/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "api/v1/userVisibility/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "api/v1/userVisibility/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "api/v1/userVisibility/**").hasRole(ADMIN)
                        .anyRequest().permitAll())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
