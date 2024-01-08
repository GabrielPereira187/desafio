package br.com.desafio.File.exception;

public class FieldNotExistException extends Exception {
    public FieldNotExistException(String field) {
        super("Campo: " + field +" não existente");
    }
}
