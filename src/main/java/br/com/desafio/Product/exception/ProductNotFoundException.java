package br.com.desafio.Product.exception;

public class ProductNotFoundException extends Exception{

    public ProductNotFoundException(String id) {
        super("Produto não encontrado com ID:" + id);
    }
}
