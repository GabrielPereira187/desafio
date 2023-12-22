package br.com.desafio.exception.Product;

public class ProductNotFoundException extends Exception{

    public ProductNotFoundException(String id) {
        super("Produto não encontrado com ID:" + id);
    }
}
