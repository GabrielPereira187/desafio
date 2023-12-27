package br.com.desafio.exception.File;

public class FieldNotExistException extends Exception {
    public FieldNotExistException(String field) {
        super("Campo: " + field +" não existente");
    }
}
