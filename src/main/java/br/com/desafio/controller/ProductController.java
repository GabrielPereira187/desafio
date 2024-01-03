package br.com.desafio.controller;


import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.AggregatedValueResponse;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.entity.AuditItem;
import br.com.desafio.entity.Product;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.Product.ProductNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.service.FileGeneratorService;
import br.com.desafio.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileGeneratorService fileGeneratorService;

    @Autowired
    public ProductController(ProductService productService, FileGeneratorService fileGeneratorService) {
        this.productService = productService;
        this.fileGeneratorService = fileGeneratorService;
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
    public ProductResponse getProduct(@PathVariable Long productId, HttpServletRequest request) throws ProductNotFoundException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        return productService.getProduct(productId, getToken(request));
    }

    @Operation(summary = "Realiza a inserção de um produto", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto inserido com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao inserir o produto"),
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Object> saveProduct(@RequestBody ProductRequest product, HttpServletRequest request) throws Exception {
        return productService.saveProduct(product);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Object> saveImageToProduct(@RequestParam("file") MultipartFile file, @RequestParam("productId") Long productId) throws Exception {
        return productService.saveFileToProduct(productId, file);
    }

    @Operation(summary = "Realiza a deleção de um produto", method = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto deletado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao deletar o produto"),
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@Valid @PathVariable Long productId, HttpServletRequest request) throws ProductNotFoundException {
        return productService.deleteProduct(productId, getToken(request));
    }

    @Operation(summary = "Realiza a atualização de um produto", method = "PUT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao atualizar o produto"),
    })
    @PutMapping("/{productId}")
    public ResponseEntity<List<String>> updateProduct(@PathVariable Long productId,
                                                      @RequestBody ProductRequest product,
                                                      HttpServletRequest request) throws UserNotFoundException, CategoryNotFoundException {
        return productService.updateProduct(productId, product, getToken(request));
    }
    @Operation(summary = "Realiza a busca paginada de todos os produtos", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum produto encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar o produto"),
    })
    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> getProducts(Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @Operation(summary = "Realiza a desativação de um produto", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar o produto"),
    })
    @PostMapping("deactivate/{productId}")
    public ResponseEntity<String> deactivateProduct(@Valid @PathVariable Long productId, HttpServletRequest request) throws ProductNotFoundException {
        return productService.deactivateProduct(productId, getToken(request));
    }

    @Operation(summary = "Realiza a busca paginada de produtos por um id de usuario", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar produtos"),
    })
    @GetMapping("/filter/findByUser/{userId}")
    public Page<ProductResponse> getProductsByUser(@PathVariable("userId") Long userId,
                                                   @RequestParam("page") Optional<Integer> page,
                                                   @RequestParam(value = "sortBy") Optional<String> sortBy,
                                                   @RequestParam(value = "sort") Optional<Sort.Direction> sort,
                                                   @RequestParam("size") Optional<Integer> pageSize) throws UserNotFoundException {
        return productService.getProductsByUser(userId, page, sortBy, pageSize, sort);
    }

    @Operation(summary = "Realiza a busca paginada de produtos pelos seus campos", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar produtos"),
    })
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
                                           @RequestParam("size") Optional<Integer> pageSize,
                                           @RequestParam("quantity") Optional<Long> quantity) {
        return productService.getProductByFields(userId, page, sortBy, pageSize, sort, productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, quantity);
    }

    @Operation(summary = "Realiza a busca de dados de auditoria de um determinado produto", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados de auditoria encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar dados de auditoria"),
    })
    @GetMapping("/revisions/{id}")
    public List<AuditItem> getRevisions(@PathVariable Long id, HttpServletRequest request) throws ProductNotFoundException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        return productService.getRevisions(id, getToken(request));
    }

    @Operation(summary = "Realiza a busca paginada de valores agregados de produtos pelos seus campos", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar produtos"),
    })
    @GetMapping("/filter/findByFields/aggregatedValue")
    public Page<AggregatedValueResponse> getAggregatedValueByFields(@RequestParam("userId") Optional<Long> userId,
                                                            @RequestParam("productId") Optional<Long> productId,
                                                            @RequestParam("name") Optional<String> name,
                                                            @RequestParam("entryDate") Optional<LocalDateTime> entryDate,
                                                            @RequestParam("active") Optional<Boolean> active,
                                                            @RequestParam("SKU") Optional<String> sku,
                                                            @RequestParam("categoryId") Optional<Long> categoryId,
                                                            @RequestParam("cost") Optional<BigDecimal> cost,
                                                            @RequestParam("icms") Optional<BigDecimal> icms,
                                                            @RequestParam("revenueValue") Optional<BigDecimal> revenueValue,
                                                            @RequestParam("quantity") Optional<Long> quantity,
                                                            @RequestParam("page") Optional<Integer> page,
                                                            @RequestParam(value = "sortBy") Optional<String> sortBy,
                                                            @RequestParam(value = "sort") Optional<Sort.Direction> sort,
                                                            @RequestParam("size") Optional<Integer> pageSize) {
        return productService.getAggregatedValueByFields(userId, productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, quantity, page, sortBy, sort, pageSize);
    }

    @Operation(summary = "Realiza a busca paginada de valores agregados de produtos pelo id de usuario", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos encontrados com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados de requisição inválida"),
            @ApiResponse(responseCode = "400", description = "Parametros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao buscar produtos"),
    })
    @GetMapping("/filter/findByUser/aggregatedValue/{id}")
    public Page<AggregatedValueResponse> getAggregatedValueByUser(@PathVariable("id") Long userId,
                                                                @RequestParam("page") Optional<Integer> page,
                                                                @RequestParam(value = "sortBy") Optional<String> sortBy,
                                                                @RequestParam(value = "sort") Optional<Sort.Direction> sort,
                                                                @RequestParam("size") Optional<Integer> pageSize) throws UserNotFoundException {
        return productService.getAggregatedValueByUser(userId, page, sortBy, pageSize, sort);
    }

    @GetMapping("revisions/detailed/{id}")
    public Object getDetailedRevisions(@PathVariable("id") Long revisionId) {
        return productService.getDetailedRevisions(revisionId);
    }

    @GetMapping("filter/findByFields/{format}")
    public void generateFileByFields(@PathVariable("format") String format,
                             HttpServletResponse response,
                             @RequestParam("userId") Optional<Long> userId,
                             @RequestParam("productId") Optional<Long> productId,
                             @RequestParam("name") Optional<String> name,
                             @RequestParam("entryDate") Optional<LocalDateTime> entryDate,
                             @RequestParam("active") Optional<Boolean> active,
                             @RequestParam("SKU") Optional<String> sku,
                             @RequestParam("categoryId") Optional<Long> categoryId,
                             @RequestParam("cost") Optional<BigDecimal> cost,
                             @RequestParam("icms") Optional<BigDecimal> icms,
                             @RequestParam("revenueValue") Optional<BigDecimal> revenueValue,
                             @RequestParam("quantity") Optional<Long> quantity,
                             @RequestParam("fields") List<String> fields) throws Exception {
        Page<ProductResponse> productByFields = productService.getProductByFields(userId, Optional.empty(), Optional.of("productId"), Optional.of(2000), Optional.empty(), productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, quantity);
        if(format.equalsIgnoreCase("csv")) {
            fileGeneratorService.generateCSV(response, productByFields, fields);
        } else {
            fileGeneratorService.generateXLS(response, productByFields, fields);
        }
    }

    @GetMapping("filter/findByUser/{format}")
    public void generateFileByUser(@PathVariable("format") String format,
                             HttpServletResponse response,
                             @RequestParam("userId") Long userId,
                             @RequestParam("fields") List<String> fields) throws Exception {
        Page<ProductResponse> productsByUser = productService.getProductsByUser(userId, Optional.empty(), Optional.of("productId"), Optional.of(2000), Optional.empty());
        if(format.equalsIgnoreCase("csv")) {
            fileGeneratorService.generateCSV(response, productsByUser, fields);
        }
        else {
            fileGeneratorService.generateXLS(response, productsByUser, fields);
        }
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
