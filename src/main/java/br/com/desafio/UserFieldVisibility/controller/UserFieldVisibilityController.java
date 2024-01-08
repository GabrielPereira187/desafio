package br.com.desafio.UserFieldVisibility.controller;

import br.com.desafio.UserFieldVisibility.DTO.request.UserFieldVisibilityRequest;
import br.com.desafio.UserFieldVisibility.DTO.response.UserFieldVisibilityResponse;
import br.com.desafio.UserFieldVisibility.exception.UserVisibilityNotFoundException;
import br.com.desafio.UserFieldVisibility.service.UserFieldVisibilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/userVisibility")
@Tag(name = "UserVisibility-API")
@Slf4j
public class UserFieldVisibilityController {

    private final UserFieldVisibilityService userFieldVisibilityService;

    @Autowired
    public UserFieldVisibilityController(UserFieldVisibilityService userFieldVisibilityService) {
        this.userFieldVisibilityService = userFieldVisibilityService;
    }

    @Operation(summary = "Realiza a inserção de um campo pertencente a classe de produto para visualização", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campo inserido com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao inserir campo"),
    })
    @PostMapping
    public ResponseEntity<Object> save(UserFieldVisibilityRequest userFieldVisibility) {
        return userFieldVisibilityService.save(userFieldVisibility);
    }
    @Operation(summary = "Realiza a busca de um campo de visualização", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campo encontrado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao encontrar campo"),
    })
    @GetMapping("/{id}")
    public UserFieldVisibilityResponse findById(@PathVariable("id") Long id) throws UserVisibilityNotFoundException {
        return userFieldVisibilityService.findById(id);
    }

    @Operation(summary = "Realiza a deleção de um campo pertencente a classe de produto para visualização", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campo deletado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar campo"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") Long id) throws UserVisibilityNotFoundException {
        return userFieldVisibilityService.deleteById(id);
    }


    @Operation(summary = "Realiza a busca de todos os campos pertencentes a classe de produto para visualização", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campos encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao procurar campos"),
    })
    @GetMapping
    public List<UserFieldVisibilityResponse> findAll() {
        return userFieldVisibilityService.findAll();
    }

    @Operation(summary = "Realiza a atualização de não visualização para estoquista de uma lista de campos", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualização feitas com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar campos"),
    })
    @PutMapping("/invisible")
    public ResponseEntity<Object> setInvisibleFieldsForEstoquista(@RequestParam("fields")List<String> fields) {
        return userFieldVisibilityService.setInvisibleFieldsForEstoquista(fields);
    }

    @Operation(summary = "Realiza a atualização de visualização para estoquista de uma lista de campos", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Atualização feitas com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar campos"),
    })
    @PutMapping("/visible")
    public ResponseEntity<Object> setVisibleFieldsForEstoquista(@RequestParam("fields")List<String> fields) {
        return userFieldVisibilityService.setVisibleFieldsForEstoquista(fields);
    }
}
