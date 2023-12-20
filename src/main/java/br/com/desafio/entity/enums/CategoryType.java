package br.com.desafio.entity.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryType {

    NORMAL("normal"), ESPECIAL("especial"), PERSONALIZADO("personalizado");

    private final String value;
}
