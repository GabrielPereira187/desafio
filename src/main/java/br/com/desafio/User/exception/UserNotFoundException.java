package br.com.desafio.User.exception;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(Long id) {
        super("Usuario não encontrado com ID:" + id);
    }

}
