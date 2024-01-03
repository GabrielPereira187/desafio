package br.com.desafio.service;

import br.com.desafio.entity.UserFieldVisibility;
import br.com.desafio.exception.UserFieldVisibility.UserVisibilityNotFoundException;
import br.com.desafio.repository.UserFieldVisibilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFieldVisibilityService {

    private final UserFieldVisibilityRepository userFieldVisibilityRepository;

    @Autowired
    public UserFieldVisibilityService(UserFieldVisibilityRepository userFieldVisibilityRepository) {
        this.userFieldVisibilityRepository = userFieldVisibilityRepository;
    }

    public ResponseEntity<Object> save(UserFieldVisibility userFieldVisibility) {
        userFieldVisibilityRepository.save(userFieldVisibility);
        return ResponseEntity.ok("Inserted user field visibility");
    }

    public UserFieldVisibility findById(Long id) throws UserVisibilityNotFoundException {
        return userFieldVisibilityRepository.findById(id).orElseThrow(() -> new UserVisibilityNotFoundException(String.format("Campo de id: %d", id)));
    }

    public ResponseEntity<Object> deleteById(Long id) throws UserVisibilityNotFoundException {
        findById(id);
        userFieldVisibilityRepository.deleteById(id);
        return ResponseEntity.ok("Deleted user field visibility");
    }

    public List<UserFieldVisibility> findAll() {
        return userFieldVisibilityRepository.findAll();
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
        return findAll().stream().filter(UserFieldVisibility::isVisible).map(UserFieldVisibility::getFieldName).collect(Collectors.toList());
    }

    public String getFieldByName(String field) {
        return userFieldVisibilityRepository.findByFieldName(field);
    }
}
