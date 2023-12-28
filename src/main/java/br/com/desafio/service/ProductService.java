package br.com.desafio.service;

import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.*;
import br.com.desafio.converter.ProductConverter;
import br.com.desafio.entity.AuditItem;
import br.com.desafio.entity.Product;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.Product.ProductNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.ProductRepository;
import br.com.desafio.security.TokenService;
import br.com.desafio.validator.ObjectsValidator;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.history.Revision;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ObjectsValidator<ProductRequest> productRequestObjectsValidator;
    private final TokenService tokenService;
    private static final Integer PAGE_DEFAULT_SIZE = 5;
    private static final Integer INITIAL_PAGE_DEFAULT = 0;
    private static final String UPDATE = "Atualização";
    private static final String INSERT = "Inserção";
    private static final String DELETE = "Deleção";


    @Autowired
    public ProductService(ProductRepository productRepository, ProductConverter productConverter, UserService userService, CategoryService categoryService, ObjectsValidator<ProductRequest> productRequestObjectsValidator, TokenService tokenService) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
        this.userService = userService;
        this.categoryService = categoryService;
        this.productRequestObjectsValidator = productRequestObjectsValidator;
        this.tokenService = tokenService;
    }

    public ProductResponse getProduct(Long productId) throws ProductNotFoundException {
        return productConverter.convertProductToProductResponse(
                productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString())));
    }

    public ResponseEntity<Object> saveProduct(ProductRequest product) throws Exception {
        List<String> violations = productRequestObjectsValidator.validate(product);
        MessagesResponse response = new MessagesResponse();
        if (!violations.isEmpty()) {
            response.setMessages(violations);
            return ResponseEntity.status(400).body(response);
        }
        if(checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId())) {
            try {
                productConverter.convertProductToProductResponse
                        (productRepository.save(productConverter.convertProductRequestToProduct(product)));
                return ResponseEntity.ok("Product added successfully");
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

    public ResponseEntity<List<String>> updateProduct(Long productId, ProductRequest product, String token) throws UserNotFoundException, CategoryNotFoundException {
        List<String> violations = productRequestObjectsValidator.validate(product);
        if (!violations.isEmpty()) {
            return ResponseEntity.status(400).body(violations);
        }
        if(checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId())) {
            return productRepository.findById(productId)
                    .map(productToUpdate -> {
                        productToUpdate.setCategoryId(product.getCategoryId());
                        productToUpdate.setImage(product.getImage());
                        productToUpdate.setUpdatedAt(LocalDateTime.now());
                        productToUpdate.setRevenueValue(product.getRevenueValue());
                        productToUpdate.setSKU(product.getSKU());
                        productToUpdate.setUserId(product.getUserId());
                        productToUpdate.setName(product.getName());
                        if(tokenService.checkIfUserIsAdmin(token)) {
                            productToUpdate.setCost(product.getCost());
                            productToUpdate.setICMS(product.getICMS());
                        }
                        productConverter.convertProductToProductResponse(productRepository.save(productToUpdate));
                        return ResponseEntity.ok(Collections.singletonList("Updated successfully"));
                    }).orElseGet(() ->{
                        productConverter.convertProductToProductResponse(productRepository.save(productConverter.convertProductRequestToProduct(product)));
                        return ResponseEntity.ok(Collections.singletonList("Updated successfully"));
                    });
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

    public Page<ProductResponse> getProductByUser(Long userId, Optional<Integer> page, Optional<String> sortBy, Optional<Integer> pageSize, Optional<Sort.Direction> sort) throws UserNotFoundException {
        if(userService.getUser(userId) != null) {
            return productConverter.convertProductPageToProductResponsePage(productRepository.findByUserId(userId,
                    getPageable(page, sortBy, pageSize, sort)));
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
        String revisionName = getRevisionName(revision.getMetadata().getRevisionType().toString());
        auditItem.setAuditId(revision.getMetadata().getRevisionNumber().hashCode());
        auditItem.setProductId(revision.getEntity().getProductId());
        auditItem.setAction(revisionName);
        auditItem.setDate(!revisionName.equals(UPDATE) ? revision.getEntity().getEntryDate() : revision.getEntity().getUpdatedAt());
        auditItem.setUsername(userService.getUserName(revision.getEntity().getUserId()));
        return auditItem;
    }

    private String getRevisionName(String revisionName) {
        return switch (revisionName) {
            case "INSERT" -> INSERT;
            case "UPDATE" -> UPDATE;
            case "DELETE" -> DELETE;
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
                                            Optional<BigDecimal> revenueValue,
                                            Optional<Long> quantity) {
        Example<Product> productExample = Example.of(buildProduct(productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, userId, quantity));

        return productConverter.convertProductPageToProductResponsePage(productRepository.findAll(productExample, getPageable(page, sortBy, pageSize, sort)));
    }

    private Pageable getPageable(Optional<Integer> page,
                                 Optional<String> sortBy,
                                 Optional<Integer> pageSize,
                                 Optional<Sort.Direction> sort) {
        return PageRequest.of(page.orElse(INITIAL_PAGE_DEFAULT), pageSize.orElse(PAGE_DEFAULT_SIZE), sort.orElse(Sort.Direction.ASC), sortBy.orElse("productId"));
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
                                 Optional<Long> userId,
                                 Optional<Long> quantity) {
        return Product.builder()
                .userId(userId.orElse(null))
                .activeProduct(active.orElse(true))
                .entryDate(entryDate.orElse(null))
                .ICMS(icms.orElse(null))
                .productId(productId.orElse(null))
                .name(name.orElse(null))
                .categoryId(categoryId.orElse(null))
                .revenueValue(revenueValue.orElse(null))
                .cost(cost.orElse(null))
                .SKU(sku.orElse(null))
                .quantity(quantity.orElse(null))
                .build();
    }

    public Page<AggregatedValueResponse> getAggregatedValueByUser(Long userId,
                                                                  Optional<Integer> page,
                                                                  Optional<String> sortBy,
                                                                  Optional<Integer> pageSize,
                                                                  Optional<Sort.Direction> sort) throws UserNotFoundException {
        Page<ProductResponse> productByFields = getProductByUser(userId, page, sortBy, pageSize, sort);
        List<AggregatedValueResponse> valueResponseList = new ArrayList<>();
        for(ProductResponse productResponse: productByFields.getContent()) {
            valueResponseList.add(createAggregatedValueResponse(productResponse));
        }
        return createPage(valueResponseList, page, pageSize, sortBy, sort);
    }

    public Page<AggregatedValueResponse> getAggregatedValueByFields(Optional<Long> userId, Optional<Long> productId,
                                                                    Optional<String> name,
                                                                    Optional<LocalDateTime> entryDate,
                                                                    Optional<Boolean> active,
                                                                    Optional<String> sku,
                                                                    Optional<Long> categoryId,
                                                                    Optional<BigDecimal> cost,
                                                                    Optional<BigDecimal> icms,
                                                                    Optional<BigDecimal> revenueValue,
                                                                    Optional<Long> quantity,
                                                                    Optional<Integer> page,
                                                                    Optional<String> sortBy,
                                                                    Optional<Sort.Direction> sort,
                                                                    Optional<Integer> pageSize) {
        Page<ProductResponse> productByFields = getProductByFields(userId, page, sortBy, pageSize, sort, productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, quantity);
        List<AggregatedValueResponse> valueResponseList = new ArrayList<>();
        for(ProductResponse productResponse: productByFields.getContent()) {
            valueResponseList.add(createAggregatedValueResponse(productResponse));
        }

        return createPage(valueResponseList, page, pageSize, sortBy, sort);
    }

    private AggregatedValueResponse createAggregatedValueResponse(ProductResponse productResponse) {
        return AggregatedValueResponse
                .builder()
                .productId(productResponse.getProductId())
                .cost(productResponse.getCost())
                .totalCost(getTotalCost(productResponse.getCost(), productResponse.getQuantity()))
                .forecast(getTotalReturn(productResponse.getQuantity(), productResponse.getRevenueValue()))
                .build();
    }

    private BigDecimal getTotalReturn(Long quantity, BigDecimal revenueValue) {
        return BigDecimal.valueOf(quantity).multiply(revenueValue);
    }

    private BigDecimal getTotalCost(BigDecimal cost, Long quantity) {
        return BigDecimal.valueOf(quantity).multiply(cost);
    }

    private Page<AggregatedValueResponse> createPage(List<AggregatedValueResponse> lista, Optional<Integer> page, Optional<Integer> pageSize, Optional<String> sortBy,
                                                     Optional<Sort.Direction> sort) {
        int pageBegin = page.orElse(INITIAL_PAGE_DEFAULT) * pageSize.orElse(PAGE_DEFAULT_SIZE);
        int pageEnd = Math.min(pageBegin + pageSize.orElse(PAGE_DEFAULT_SIZE), lista.size());

        List<AggregatedValueResponse> pageElements = lista.subList(pageBegin, pageEnd);

        return new PageImpl<>(pageElements, getPageable(page, sortBy, pageSize, sort), lista.size());
    }

    public AuditChanges getDetailedRevisions(Long revisionId) {
        Tuple tuple = productRepository.findRevisionByRevType(revisionId);
        Javers javers = JaversBuilder.javers().build();
        if(tuple != null) {
            ProductResponse productResponse = productConverter.tupleToProductResponse(tuple);
            ProductResponse productResponseLastRev = productConverter
                    .tupleToProductResponse(productRepository.findLastRevisionByProductIdAndRev(productResponse.getProductId(), revisionId));
            List<ChangesResponse> changesResponse = new ArrayList<>();

            Diff diff = javers.compare(productResponseLastRev, productResponse);
            for (Change change : diff.getChanges()) {
                ValueChange valueChange = (ValueChange) change;
                String property = valueChange.getPropertyName();
                String oldValue = valueChange.getLeft() != null ? valueChange.getLeft().toString() : "-";
                String newValue = valueChange.getRight() != null ? valueChange.getRight().toString() : "-";
                changesResponse.add(new ChangesResponse(property, oldValue, newValue));
            }

            return AuditChanges.builder()
                    .changes(changesResponse)
                    .description("Detalhamento auditoria de ID:" + revisionId)
                    .build();
        }

        return AuditChanges.builder()
                .description("Auditoria de ID:" + revisionId + " não é do tipo atualização ou não existe")
                .build();

    }

    public ResponseEntity<Object> saveFileToProduct(Long productId, MultipartFile file) throws IOException {
        Product product = productRepository.findById(productId).get();

        product.setImage(file.getName());

        productRepository.save(product);

        return ResponseEntity.ok("Sucesso");
    }
}
