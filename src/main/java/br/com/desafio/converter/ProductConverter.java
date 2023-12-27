package br.com.desafio.converter;

import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.Product;
import jakarta.persistence.Tuple;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public ProductConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product convertProductRequestToProduct(ProductRequest productRequest) {
        return modelMapper.map(productRequest, Product.class);
    }

    public ProductRequest convertProductToProductRequest(Product product) {
        return modelMapper.map(product, ProductRequest.class);
    }

    public ProductResponse convertProductToProductResponse(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }

    public Page<ProductResponse> convertProductPageToProductResponsePage(Page<Product> products) {
        return products.map(this::convertProductToProductResponse);
    }

    public ProductResponse tupleToProductResponse(Tuple tuple) {
        LocalDateTime entryDate = tuple.get("entryDate") == null
                ? null : timeStampToLocalDateTime("entryDate", tuple);
        LocalDateTime updatedDate = tuple.get("updatedDate") == null
                ? null : timeStampToLocalDateTime("updatedDate", tuple);
        return ProductResponse.builder()
                .productId(tuple.get("productId", Long.class))
                .name(tuple.get("name", String.class))
                .categoryId(tuple.get("categoryId", Long.class))
                .activeProduct(tuple.get("activeProduct", Boolean.class))
                .SKU(tuple.get("SKU", String.class))
                .cost(tuple.get("cost", BigDecimal.class))
                .ICMS(tuple.get("ICMS", BigDecimal.class))
                .revenueValue(tuple.get("revenueValue", BigDecimal.class))
                .image(tuple.get("image", String.class))
                .entryDate(entryDate)
                .updatedDate(updatedDate)
                .userId(tuple.get("userId", Long.class))
                .quantity(tuple.get("quantity", Long.class))
                .build();
    }

    private LocalDateTime timeStampToLocalDateTime(String key, Tuple tuple) {
        Object timestamp = tuple.get(key);
        java.sql.Timestamp timestamp1 = (java.sql.Timestamp) timestamp;
        return LocalDateTime.ofInstant(timestamp1.toInstant(), ZoneId.systemDefault());
    }

}
