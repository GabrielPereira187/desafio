package br.com.desafio.converter;

import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

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




}
