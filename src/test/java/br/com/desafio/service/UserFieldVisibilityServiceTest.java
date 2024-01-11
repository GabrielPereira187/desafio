package br.com.desafio.service;

import br.com.desafio.UserFieldVisibility.DTO.request.UserFieldVisibilityRequest;
import br.com.desafio.UserFieldVisibility.DTO.response.UserFieldVisibilityResponse;
import br.com.desafio.UserFieldVisibility.converter.UserFieldVisibilityConverter;
import br.com.desafio.UserFieldVisibility.entity.UserFieldVisibility;
import br.com.desafio.UserFieldVisibility.exception.UserVisibilityNotFoundException;
import br.com.desafio.UserFieldVisibility.repository.UserFieldVisibilityRepository;
import br.com.desafio.UserFieldVisibility.service.UserFieldVisibilityService;
import br.com.desafio.Validator.ObjectsValidator;
import br.com.desafio.util.UserFieldVisibilityCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserFieldVisibilityServiceTest {

    @InjectMocks
    private UserFieldVisibilityService userFieldVisibilityService;

    @Mock
    private UserFieldVisibilityRepository userFieldVisibilityRepository;

    UserFieldVisibility userFieldVisibility;

    UserFieldVisibilityRequest userFieldVisibilityRequest;

    UserFieldVisibilityResponse userFieldVisibilityResponse;

    @Mock
    UserFieldVisibilityConverter userFieldVisibilityConverter;

    @Mock
    private ObjectsValidator<UserFieldVisibilityRequest> objectsValidator;

    @BeforeEach
    public void setUp() {
        userFieldVisibility = UserFieldVisibilityCreator.createUserFieldVisibility();
        userFieldVisibilityRequest = UserFieldVisibilityCreator.createUserFieldVisibilityRequest();
        userFieldVisibilityResponse = UserFieldVisibilityCreator.createUserFieldVisibilityResponse();
    }

    @Test
    public void shouldSaveUserField() {
        when(userFieldVisibilityRepository.
                save(userFieldVisibilityConverter.convertUserFieldVisibilityRequestToUserFieldVisibility(userFieldVisibilityRequest)))
                .thenReturn(userFieldVisibility);

        ResponseEntity<Object> response = userFieldVisibilityService
                .save(userFieldVisibilityRequest);

        assertNotNull(response);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void shouldFindUserFieldById() throws UserVisibilityNotFoundException {
        when(userFieldVisibilityRepository.findById(1L)).thenReturn(Optional.ofNullable(userFieldVisibility));
        when(userFieldVisibilityConverter.convertUserFieldVisibilityToUserFieldVisibilityResponse(userFieldVisibility)).thenReturn(userFieldVisibilityResponse);

        UserFieldVisibilityResponse userFieldVisibilityServiceById = userFieldVisibilityService.findById(1L);

        assertNotNull(userFieldVisibilityServiceById);
    }

    @Test
    public void shouldNotFindUserFieldByIdBecauseNotExist() {
        final UserVisibilityNotFoundException e = assertThrows(UserVisibilityNotFoundException.class, () -> {
            userFieldVisibilityService.findById(2L);
        });

        assertThat(e, notNullValue());
    }

    @Test
    public void shouldDeleteUserFieldById() throws UserVisibilityNotFoundException {
        when(userFieldVisibilityRepository.findById(1L)).thenReturn(Optional.ofNullable(userFieldVisibility));

        ResponseEntity<Object> response = userFieldVisibilityService.deleteById(1L);

        assertNotNull(response);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void shouldFindAllUserFields() {
        when(userFieldVisibilityRepository.findAll()).thenReturn(
                Collections.singletonList(userFieldVisibility));
        when(userFieldVisibilityConverter.convertUserFieldVisibilityListToUserFieldVisibilityResponseList
                (Collections.singletonList(userFieldVisibility))).thenReturn(Collections.singletonList(userFieldVisibilityResponse));

        List<UserFieldVisibilityResponse> response = userFieldVisibilityService.findAll();

        assertNotNull(response);
        assertEquals(response.size(), 1);
    }

    @Test
    public void shouldSetInvisibleFieldsForEstoquista() {
        when(userFieldVisibilityRepository.findByFieldName(userFieldVisibility.getFieldName())).thenReturn(userFieldVisibility.getFieldName());

        ResponseEntity<Object> response = userFieldVisibilityService
                .setInvisibleFieldsForEstoquista(Collections.singletonList(userFieldVisibility.getFieldName()));

        assertNotNull(response);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void shouldSetVisibleFieldsForEstoquista() {
        when(userFieldVisibilityRepository.findByFieldName(userFieldVisibility.getFieldName())).thenReturn(userFieldVisibility.getFieldName());

        ResponseEntity<Object> response = userFieldVisibilityService
              .setVisibleFieldsForEstoquista(Collections.singletonList(userFieldVisibility.getFieldName()));

        assertNotNull(response);
        assertEquals(response.getStatusCode().value(), 200);
    }


}
