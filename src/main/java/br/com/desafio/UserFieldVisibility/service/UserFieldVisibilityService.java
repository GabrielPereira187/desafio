package br.com.desafio.UserFieldVisibility.service;

import br.com.desafio.UserFieldVisibility.DTO.request.UserFieldVisibilityRequest;
import br.com.desafio.UserFieldVisibility.DTO.response.UserFieldVisibilityResponse;
import br.com.desafio.UserFieldVisibility.converter.UserFieldVisibilityConverter;
import br.com.desafio.UserFieldVisibility.exception.UserVisibilityNotFoundException;
import br.com.desafio.UserFieldVisibility.repository.UserFieldVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFieldVisibilityService {

    private final UserFieldVisibilityRepository userFieldVisibilityRepository;
    private final UserFieldVisibilityConverter userFieldVisibilityConverter;

    @Autowired
    public UserFieldVisibilityService(UserFieldVisibilityRepository userFieldVisibilityRepository, UserFieldVisibilityConverter userFieldVisibilityConverter) {
        this.userFieldVisibilityRepository = userFieldVisibilityRepository;
        this.userFieldVisibilityConverter = userFieldVisibilityConverter;
    }

    public ResponseEntity<Object> save(UserFieldVisibilityRequest userFieldVisibility) {
        userFieldVisibilityRepository.save(userFieldVisibilityConverter.convertUserFieldVisibilityRequestToUserFieldVisibility(userFieldVisibility));
        return ResponseEntity.ok("Inserted user field visibility");
    }

    public UserFieldVisibilityResponse findById(Long id) throws UserVisibilityNotFoundException {
        return userFieldVisibilityConverter.convertUserFieldVisibilityToUserFieldVisibilityResponse(
                userFieldVisibilityRepository.findById(id).orElseThrow(() -> new UserVisibilityNotFoundException(String.format("Campo de id: %d", id))));
    }

    public ResponseEntity<Object> deleteById(Long id) throws UserVisibilityNotFoundException {
        findById(id);
        userFieldVisibilityRepository.deleteById(id);
        return ResponseEntity.ok("Deleted user field visibility");
    }

    public List<UserFieldVisibilityResponse> findAll() {
        return userFieldVisibilityConverter.
                convertUserFieldVisibilityListToUserFieldVisibilityResponseList(userFieldVisibilityRepository.findAll());
    }

    public ResponseEntity<Object> setInvisibleFieldsForEstoquista(List<String> fields) {
        List<String> fieldsNotFound = new ArrayList<>();

        for(String field : fields) {
            if(getFieldByName(field) == null) {
                fieldsNotFound.add(field);
            }
            userFieldVisibilityRepository.changeVisibilityFieldForUser(field, false);
        }

        return ResponseEntity.ok(String.format("Campos atualizados para não visiveis para estoquista, campos não encontrados:%s", fieldsNotFound));
    }

    public ResponseEntity<Object> setVisibleFieldsForEstoquista(List<String> fields) {
        List<String> fieldsNotFound = new ArrayList<>();

        for(String field : fields) {
            if(getFieldByName(field) == null) {
                fieldsNotFound.add(field);
            }
            userFieldVisibilityRepository.changeVisibilityFieldForUser(field, true);
        }

        return ResponseEntity.ok(String.format("Campos atualizados para visiveis para estoquista, campos não encontrados:%s", fieldsNotFound));
    }

    public List<String> getVisibleFieldsForEstoquista() {
        return findAll().stream().filter(UserFieldVisibilityResponse::isVisible).map(UserFieldVisibilityResponse::name).collect(Collectors.toList());
    }

    public String getFieldByName(String field) {
        return userFieldVisibilityRepository.findByFieldName(field);
    }
}
