package br.com.desafio.Category.exception;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(Long id) {
        super("Categoria não encontrada com ID:" + id);
    }

}
