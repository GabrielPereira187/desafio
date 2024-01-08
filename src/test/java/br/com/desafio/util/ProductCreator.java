package br.com.desafio.util;

import br.com.desafio.Product.DTO.Request.ProductRequest;
import br.com.desafio.Product.DTO.Response.ProductResponse;
import br.com.desafio.Product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductCreator {

    public static ProductRequest createProductRequest() {
        return new ProductRequest(
                "Teste de Produto",
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(40),
                "Imagem-Teste.png",
                1L,
                10L);
    }

    public static Product createProduct() {
        return new Product(1L,
                "Teste de Produto",
                true,
                "Teste SKU1",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                "Imagem-Teste.png",
                LocalDateTime.now(),
                null,
                1L,
                10L);
    }

    public static ProductResponse createProductResponse() {
        return new ProductResponse(1L,
                "Teste",
                true,
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                "Imagem-Teste",
                LocalDateTime.now(),
                null,
                1L,
                10L);
    }

    public static ProductRequest createProductRequestWithNullValue() {
        return new ProductRequest(
                null,
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(40),
                "Imagem-Teste.png",
                1L,
                10L);
    }


}
