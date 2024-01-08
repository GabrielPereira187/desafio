package br.com.desafio.User.entity.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    ESTOQUISTA("ESTOQUISTA");

    private final String role;

    UserRole (String role){
        this.role = role;
    }

}
