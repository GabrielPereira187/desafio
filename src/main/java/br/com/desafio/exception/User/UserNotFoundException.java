package br.com.desafio.exception.User;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(Long id) {
        super("Usuario não encontrado com ID:" + id);
    }

}
