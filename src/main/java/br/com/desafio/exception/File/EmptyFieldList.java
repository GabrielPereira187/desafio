package br.com.desafio.exception.File;

public class EmptyFieldList extends Exception{
    public EmptyFieldList() {
        super("Lista de campos vazia");
    }
}
