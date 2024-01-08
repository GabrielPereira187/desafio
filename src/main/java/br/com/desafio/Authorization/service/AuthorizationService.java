package br.com.desafio.Authorization.service;

import br.com.desafio.Authorization.DTO.request.AuthenticationDTO;
import br.com.desafio.Authorization.DTO.response.LoginResponseDTO;
import br.com.desafio.Authorization.DTO.request.RegisterDTO;
import br.com.desafio.Validator.DTO.response.MessagesResponse;
import br.com.desafio.RefreshToken.entity.RefreshToken;
import br.com.desafio.User.entity.User;
import br.com.desafio.RefreshToken.repository.RefreshTokenRepository;
import br.com.desafio.User.repository.UserRepository;
import br.com.desafio.RefreshToken.service.TokenService;
import br.com.desafio.Util.ObjectCreationUtil;
import br.com.desafio.Validator.ObjectsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;
    private AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectsValidator<AuthenticationDTO> authenticationDTOObjectsValidator;
    private final ObjectsValidator<RegisterDTO> registerDTOObjectsValidator;
    @Value("${jwt-time-expiration-in-minutes}")
    private Integer TOKEN_EXPIRATION_TIME;

    @Autowired
    public AuthorizationService(UserRepository userRepository, ApplicationContext applicationContext, TokenService tokenService, RefreshTokenRepository refreshTokenRepository, ObjectsValidator<AuthenticationDTO> authenticationDTOObjectsValidator, ObjectsValidator<RegisterDTO> registerDTOObjectsValidator) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationDTOObjectsValidator = authenticationDTOObjectsValidator;
        this.registerDTOObjectsValidator = registerDTOObjectsValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<Object> login(AuthenticationDTO authentication) {
        List<String> violations = authenticationDTOObjectsValidator.validate(authentication);
        MessagesResponse response = new MessagesResponse();
        if (!violations.isEmpty()) {
            response.setMessages(violations);

            log.info("Erros encontrados para ação de login");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if(!checkIfUserExists(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuario e/ou senha invalidos");
        }

        authenticationManager = applicationContext.getBean(AuthenticationManager.class);

        var userNamePassword = new UsernamePasswordAuthenticationToken(authentication.email(), authentication.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = this.tokenService.generateToken((User) auth.getPrincipal());


        log.info("Logando usuario com email:{}", authentication.email());

        User user = (User)userRepository.findByEmail(authentication.email());

        refreshTokenRepository.save(ObjectCreationUtil.createRefreshToken(TOKEN_EXPIRATION_TIME, user, token));

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public ResponseEntity<Object> register (RegisterDTO registerDto){
        List<String> violations = registerDTOObjectsValidator.validate(registerDto);
        MessagesResponse response = new MessagesResponse();
        if (!violations.isEmpty()) {
            response.setMessages(violations);

            log.info("Erros encontrados para ação de registro");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (this.userRepository.findByEmail(registerDto.email()) != null )
            return ResponseEntity.badRequest().build();

        User newUser = ObjectCreationUtil.createUser(registerDto);

        log.info("Registrando novo usuario com email:{} e role:{}", registerDto.email(), registerDto.role().getRole());

        this.userRepository.save(newUser);
        return ResponseEntity.ok().body("Registrado com sucesso");
    }

    public ResponseEntity<String> refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token);

        if(refreshToken == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token não existente");
        }

        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_TIME).toInstant(ZoneOffset.of("-03:00")));

        refreshTokenRepository.save(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body("Token atualizado");
    }

    private boolean checkPassword(String password, String passwordRequest) {
        return BCrypt.checkpw(password, passwordRequest);
    }

    private boolean checkIfUserExists(AuthenticationDTO authenticationDTO) {
        UserDetails user = userRepository.findByEmail(authenticationDTO.email());
        return user != null && checkPassword(authenticationDTO.password(), user.getPassword());
    }

}
