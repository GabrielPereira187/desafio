package br.com.desafio.service;

import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.converter.ProductConverter;
import br.com.desafio.entity.AuditItem;
import br.com.desafio.entity.Product;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.Product.ProductNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.history.Revision;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;
    private final UserService userService;
    private final CategoryService categoryService;


    @Autowired
    public ProductService(ProductRepository productRepository, ProductConverter productConverter, UserService userService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public ProductResponse getProduct(Long productId) throws ProductNotFoundException {
        return productConverter.convertProductToProductResponse(
                productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString())));
    }

    public ResponseEntity<ProductResponse> saveProduct(@Valid ProductRequest product) throws Exception {
        if(checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId())) {
            try {
                return ResponseEntity.ok(
                        productConverter.convertProductToProductResponse
                                (productRepository.save(productConverter.convertProductRequestToProduct(product))));
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return null;
    }

    private boolean checkIfUserAndCategoryExists(Long userId, Long categoryId) throws CategoryNotFoundException, UserNotFoundException {
        return userService.getUser(userId) != null && categoryService.getCategory(categoryId) != null;
    }

    public ResponseEntity<String> deleteProduct(Long productId) throws ProductNotFoundException {
        try {
            getProduct(productId);
            productRepository.deleteById(productId);
            return ResponseEntity.ok("deletado com sucesso");
        } catch (Exception e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }

    public ProductResponse updateProduct(Long productId, @Valid ProductRequest product) throws UserNotFoundException, CategoryNotFoundException {
        if(checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId())) {
            return productRepository.findById(productId)
                    .map(product1 -> {
                        product1.setCategoryId(product.getCategoryId());
                        product1.setActiveProduct(product.isActiveProduct());
                        product1.setICMS(product.getICMS());
                        product1.setCost(product.getCost());
                        product1.setImage(product.getImage());
                        product1.setEntryDate(LocalDateTime.now());
                        product1.setRevenueValue(product.getRevenueValue());
                        product1.setSKU(product.getSKU());
                        product1.setUserId(product.getUserId());
                        product1.setName(product.getName());
                        return productConverter.convertProductToProductResponse(productRepository.save(product1));
                    }).orElseGet(() -> productConverter.convertProductToProductResponse(productRepository.save(productConverter.convertProductRequestToProduct(product))));
        }
        return null;
    }


    public ResponseEntity<Page<Product>> getProducts(Pageable pageable) {
        return ResponseEntity.ok(productRepository.findAll(pageable));
    }

    public ResponseEntity<String> deactivateProduct(Long productId) throws ProductNotFoundException {
        try {
            getProduct(productId);
            productRepository.deactivateProduct(productId);
            return ResponseEntity.ok("desativado com sucesso");
        } catch (Exception e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }

    public Page<Product> getProductByUser(Long userId, Optional<Integer> page, Optional<String> sortBy, Optional<Integer> pageSize, Optional<Sort.Direction> sort) throws UserNotFoundException {
        if(userService.getUser(userId) != null) {
            return productRepository.findByUserId(userId,
                    getPageable(page, sortBy, pageSize, sort));
        }
        return null;
    }

    public List<AuditItem> getRevisions(Long id) {
        List<Revision<Long, Product>> revisions = productRepository.findRevisions(id).getContent();
        List<AuditItem> auditItems = new ArrayList<>();

        for(Revision<Long, Product> revision: revisions) {
            auditItems.add(createAuditItem(revision));
        }

        return auditItems;
    }

    private AuditItem createAuditItem(Revision<Long, Product> revision) {
        AuditItem auditItem = new AuditItem();
        auditItem.setProductId(revision.getEntity().getProductId());
        auditItem.setEntityName(revision.getEntity().getClass().getSimpleName());
        auditItem.setAction(getRevisionName(revision.getMetadata().getRevisionType().toString()));
        auditItem.setDate(revision.getEntity().getEntryDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        auditItem.setUsername(userService.getUserName(revision.getEntity().getUserId()));
        return auditItem;
    }

    private String getRevisionName(String revisionName) {
        return switch (revisionName) {
            case "INSERT" -> "Inserção";
            case "UPDATE" -> "Atualização";
            case "DELETE" -> "Deleção";
            default -> revisionName;
        };
    }

    public Page<ProductResponse> getProductByFields(Optional<Long> userId,
                                            Optional<Integer> page,
                                            Optional<String> sortBy,
                                            Optional<Integer> pageSize,
                                            Optional<Sort.Direction> sort,
                                            Optional<Long> productId,
                                            Optional<String> name,
                                            Optional<LocalDateTime> entryDate,
                                            Optional<Boolean> active,
                                            Optional<String> sku,
                                            Optional<Long> categoryId,
                                            Optional<BigDecimal> cost,
                                            Optional<BigDecimal> icms,
                                            Optional<BigDecimal> revenueValue) {
        Example<Product> productExample = Example.of(buildProduct(productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, userId));

        return productConverter.convertProductPageToProductResponsePage(productRepository.findAll(productExample, getPageable(page, sortBy, pageSize, sort)));
    }

    private Pageable getPageable(Optional<Integer> page,
                                 Optional<String> sortBy,
                                 Optional<Integer> pageSize,
                                 Optional<Sort.Direction> sort) {
        return PageRequest.of(page.orElse(0), pageSize.orElse(5), sort.orElse(Sort.Direction.ASC), sortBy.orElse("productId"));
    }

    private Product buildProduct(Optional<Long> productId,
                                 Optional<String> name,
                                 Optional<LocalDateTime> entryDate,
                                 Optional<Boolean> active,
                                 Optional<String> sku,
                                 Optional<Long> categoryId,
                                 Optional<BigDecimal> cost,
                                 Optional<BigDecimal> icms,
                                 Optional<BigDecimal> revenueValue,
                                 Optional<Long> userId) {
        Product product = new Product();

        product.setUserId(userId.orElse(null));
        product.setActiveProduct(active.orElse(true));
        product.setEntryDate(entryDate.orElse(null));
        product.setProductId(productId.orElse(null));
        product.setName(name.orElse(null));
        product.setSKU(sku.orElse(null));
        product.setCategoryId(categoryId.orElse(null));
        product.setRevenueValue(revenueValue.orElse(null));
        product.setCost(cost.orElse(null));
        product.setICMS(icms.orElse(null));

        return product;

    }
}
