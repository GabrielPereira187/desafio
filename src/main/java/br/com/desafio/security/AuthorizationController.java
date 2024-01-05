package br.com.desafio.security;

import br.com.desafio.DTO.Auth.AuthenticationDTO;
import br.com.desafio.DTO.Auth.RegisterDTO;
import br.com.desafio.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Authorization-API")
public class AuthorizationController {

    @Autowired
    AuthorizationService authorizationService;

    @Operation(summary = "Realiza o login de um usuario", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario logado"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado"),
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthenticationDTO authenticationDto){
        return authorizationService.login(authenticationDto);
    }


    @Operation(summary = "Realiza o registro de um usuario", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao registrar usuario"),
    })
    @PostMapping("/register")
    public ResponseEntity<Object> register (@RequestBody RegisterDTO registerDto){
        return authorizationService.register(registerDto);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok("atualizado");
    }
}
