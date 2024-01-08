package br.com.desafio.util;

import br.com.desafio.UserFieldVisibility.DTO.request.UserFieldVisibilityRequest;
import br.com.desafio.UserFieldVisibility.DTO.response.UserFieldVisibilityResponse;
import br.com.desafio.UserFieldVisibility.entity.UserFieldVisibility;

public class UserFieldVisibilityCreator {

    public static UserFieldVisibility createUserFieldVisibility() {
        return UserFieldVisibility
                .builder()
                .id(1L)
                .fieldName("name")
                .isVisible(true)
                .build();
    }

    public static UserFieldVisibilityRequest createUserFieldVisibilityRequest() {
        return UserFieldVisibilityRequest
                .builder()
                .fieldName("name")
                .build();
    }

    public static UserFieldVisibilityResponse createUserFieldVisibilityResponse() {
        return UserFieldVisibilityResponse
                .builder()
                .id(1L)
                .name("name")
                .isVisible(true)
                .build();
    }
}
