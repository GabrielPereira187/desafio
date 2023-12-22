package br.com.desafio.controller;


import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.AuditItem;
import br.com.desafio.entity.Product;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.Product.ProductNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/product", produces = {"application/json"})
@Tag(name = "Product-API")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Realiza a busca de um produto", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar o produto"),
    })
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) throws ProductNotFoundException {
        return productService.getProduct(productId);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest product) throws Exception {
        return productService.saveProduct(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@Valid @PathVariable Long productId) throws ProductNotFoundException {
        return productService.deleteProduct(productId);
    }

    @PutMapping("/{productId}")
    public ProductResponse updateProduct(@Valid @PathVariable Long productId, @RequestBody @Valid ProductRequest product) throws UserNotFoundException, CategoryNotFoundException {
        return productService.updateProduct(productId, product);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> getProducts(Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @PostMapping("deactivate/{productId}")
    public ResponseEntity<String> deactivateProduct(@Valid @PathVariable Long productId) throws ProductNotFoundException {
        return productService.deactivateProduct(productId);
    }

    @GetMapping("/filter/findByUser")
    public Page<Product> getProductsByUser(@RequestParam("userId") Long userId,
                                                   @RequestParam("page") Optional<Integer> page,
                                                   @RequestParam(value = "sortBy") Optional<String> sortBy,
                                                   @RequestParam(value = "sort") Optional<Sort.Direction> sort,
                                                   @RequestParam("size") Optional<Integer> pageSize) throws UserNotFoundException {
        return productService.getProductByUser(userId, page, sortBy, pageSize, sort);
    }

    @GetMapping("/filter/findByFields")
    public Page<ProductResponse> getProductsByFields(@RequestParam("userId") Optional<Long> userId,
                                           @RequestParam("productId") Optional<Long> productId,
                                           @RequestParam("name") Optional<String> name,
                                           @RequestParam("entryDate") Optional<LocalDateTime> entryDate,
                                           @RequestParam("active") Optional<Boolean> active,
                                           @RequestParam("SKU") Optional<String> sku,
                                           @RequestParam("categoryId") Optional<Long> categoryId,
                                           @RequestParam("cost") Optional<BigDecimal> cost,
                                           @RequestParam("icms") Optional<BigDecimal> icms,
                                           @RequestParam("revenueValue") Optional<BigDecimal> revenueValue,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam(value = "sortBy") Optional<String> sortBy,
                                           @RequestParam(value = "sort") Optional<Sort.Direction> sort,
                                           @RequestParam("size") Optional<Integer> pageSize) throws UserNotFoundException {
        return productService.getProductByFields(userId, page, sortBy, pageSize, sort, productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue);
    }

    @GetMapping("/revisions/{id}")
    public List<AuditItem> getRevisions(@PathVariable Long id) {
        return productService.getRevisions(id);
    }
}
