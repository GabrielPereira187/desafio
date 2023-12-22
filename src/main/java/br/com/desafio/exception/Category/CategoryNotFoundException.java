package br.com.desafio.exception.Category;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(Long id) {
        super("Categoria não encontrada com ID:" + id);
    }

}
