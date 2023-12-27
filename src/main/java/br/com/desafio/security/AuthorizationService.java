package br.com.desafio.security;

import br.com.desafio.DTO.Auth.AuthenticationDTO;
import br.com.desafio.DTO.Auth.LoginResponseDTO;
import br.com.desafio.DTO.Auth.RegisterDTO;
import br.com.desafio.entity.User;
import br.com.desafio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;
    private AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public AuthorizationService(UserRepository userRepository, ApplicationContext applicationContext, TokenService tokenService) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
        this.tokenService = tokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<Object> login(AuthenticationDTO authentication) {
        authenticationManager = applicationContext.getBean(AuthenticationManager.class);

        var userNamePassword = new UsernamePasswordAuthenticationToken(authentication.email(), authentication.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = this.tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<Object> register (RegisterDTO registerDto){
        if (this.userRepository.findByEmail(registerDto.email()) != null )
            return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());

        User newUser = User.builder()
                .email(registerDto.email())
                .password(encryptedPassword)
                .userRole(registerDto.role())
                .build();
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }
}
