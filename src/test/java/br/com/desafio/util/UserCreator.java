package br.com.desafio.util;

import br.com.desafio.User.entity.User;
import br.com.desafio.User.entity.enums.UserRole;

public class UserCreator {

    public static User createUserAdmin() {
        return User.builder().id(1L).email("teste@gmail.com").password("$2a$12$Z9PeC1i6XFi5IHj3ooq8YuEDwnsoCz3mxIiqa1sxoFFMTdhdGbo5O").userRole(UserRole.ADMIN).build();
    }

    public static User createUser() {
        return User.builder().email("teste2@gmail.com").password("senha").userRole(UserRole.ESTOQUISTA).build();
    }

}
