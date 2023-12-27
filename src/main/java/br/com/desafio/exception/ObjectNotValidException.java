package br.com.desafio.exception;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ObjectNotValidException extends RuntimeException{

    private final Set<String> errorMessages;
}
