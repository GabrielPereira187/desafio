package br.com.desafio.util;

import br.com.desafio.entity.UserFieldVisibility;

public class UserFieldVisibilityCreator {

    public static UserFieldVisibility createUserFieldVisibility() {
        return UserFieldVisibility
                .builder()
                .id(1L)
                .fieldName("name")
                .isVisible(true)
                .build();
    }
}
