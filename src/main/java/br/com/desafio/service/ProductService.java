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
import br.com.desafio.util.ObjectCreationUtil;
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
    private final UserFieldVisibilityService userFieldVisibilityService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ObjectsValidator<ProductRequest> productRequestObjectsValidator;
    private final TokenService tokenService;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductConverter productConverter, UserFieldVisibilityService userFieldVisibilityService, UserService userService, CategoryService categoryService, ObjectsValidator<ProductRequest> productRequestObjectsValidator, TokenService tokenService) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
        this.userFieldVisibilityService = userFieldVisibilityService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.productRequestObjectsValidator = productRequestObjectsValidator;
        this.tokenService = tokenService;
    }

    public ProductResponse getProduct(Long productId, String token) throws ProductNotFoundException, IllegalAccessException {

        log.info("Buscando produto de id:{}", productId);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        return checkIfUserIsAdmin(token) ? productConverter.convertProductToProductResponse(product)
                : productConverter.productToProductResponseEstoquista(product, userFieldVisibilityService.getVisibleFieldsForEstoquista());
    }

    public ResponseEntity<Object> saveProduct(ProductRequest product) throws Exception {
        List<String> violations = productRequestObjectsValidator.validate(product);
        MessagesResponse response = new MessagesResponse();
        if (!violations.isEmpty()) {
            response.setMessages(violations);

            log.info("Erros encontrados para inserção de produto");

            return ResponseEntity.status(400).body(response);
        }
        try {
            if(checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId())) {
                Product productInserted = productRepository.save(productConverter.convertProductRequestToProduct(product));

                ProductResponse productResponse = productConverter.convertProductToProductResponse(productInserted);

                log.info("Produto com id:{} salvo com suceso", productResponse.getProductId());

                return ResponseEntity.ok("Produto adicionado com sucesso");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public boolean checkIfUserAndCategoryExists(Long userId, Long categoryId) throws CategoryNotFoundException, UserNotFoundException {

        log.info("Checando se usuario:{} e categoria:{} existem no sistema", userId, categoryId);

        return userService.getUser(userId) != null && categoryService.getCategory(categoryId) != null;
    }

    public ResponseEntity<String> deleteProduct(Long productId) throws ProductNotFoundException {
        try {
            if(checkIfProductExist(productId)) {
                productRepository.deleteById(productId);

                log.info("Deletando produto de id:{}", productId);

                return ResponseEntity.ok("deletado com sucesso");
            }
            return ResponseEntity.status(404).body("Produto não encontrado");
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
                        if(checkIfUserIsAdmin(token)) {
                            productToUpdate.setCost(product.getCost());
                            productToUpdate.setICMS(product.getICMS());
                        }
                        productConverter.convertProductToProductResponse(productRepository.save(productToUpdate));

                        log.info("Produto com id:{} atualizado com suceso", productId);

                        return ResponseEntity.ok(Collections.singletonList("Inserted successfully"));
                    }).orElseGet(() ->{
                        productConverter.convertProductToProductResponse(productRepository.save(productConverter.convertProductRequestToProduct(product)));

                        log.info("Produto com id:{} salvo com suceso", productId);

                        return ResponseEntity.ok(Collections.singletonList("Atualizado com sucesso"));
                    });
        }
        return null;
    }


    public ResponseEntity<Page<Product>> getProducts(Pageable pageable) {

        log.info("Buscando produtos");

        return ResponseEntity.ok(productRepository.findAll(pageable));
    }

    public ResponseEntity<String> deactivateProduct(Long productId) throws ProductNotFoundException {
        try {
            if(checkIfProductExist(productId)) {
                productRepository.deactivateProduct(productId);

                log.info("Desativando produto com id:{}", productId);

                return ResponseEntity.ok("desativado com sucesso");
            }
            return null;
        } catch (Exception e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }

    public Page<ProductResponse> getProductsByUser(Long userId, Optional<Integer> page, Optional<String> sortBy, Optional<Integer> pageSize, Optional<Sort.Direction> sort) throws UserNotFoundException {
        if(userService.getUser(userId) != null) {

            log.info("Buscando produtos inseridos pelo usuario:{}", userId);

            return productConverter.convertProductPageToProductResponsePage(productRepository.findByUserId(userId,
                    ObjectCreationUtil.getPageable(page, sortBy, pageSize, sort)));
        }
        return null;
    }

    public List<AuditItem> getRevisions(Long id) throws ProductNotFoundException {
        checkIfProductExist(id);
        List<Revision<Long, Product>> revisions = productRepository.findRevisions(id).getContent();
        List<AuditItem> auditItems = new ArrayList<>();

        for(Revision<Long, Product> revision: revisions) {
            auditItems.add(ObjectCreationUtil.createAuditItem(revision, userService.getUserName(revision.getEntity().getUserId())));
        }

        log.info("Buscando dados de auditoria do produto:{}", id);

        return auditItems;
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
        Example<Product> productExample = Example.of(ObjectCreationUtil.buildProductExample(productId, name, entryDate, active, sku, categoryId, cost, icms, revenueValue, userId, quantity));

        log.info("Buscando produtos filtrando por campos escolhidos pelo usuário");

        return productConverter.convertProductPageToProductResponsePage(productRepository.findAll(productExample, ObjectCreationUtil.getPageable(page, sortBy, pageSize, sort)));
    }

    public Page<AggregatedValueResponse> getAggregatedValueByUser(Long userId,
                                                                  Optional<Integer> page,
                                                                  Optional<String> sortBy,
                                                                  Optional<Integer> pageSize,
                                                                  Optional<Sort.Direction> sort) throws UserNotFoundException {
        Page<ProductResponse> productByFields = getProductsByUser(userId, page, sortBy, pageSize, sort);
        List<AggregatedValueResponse> valueResponseList = ObjectCreationUtil.createAggregatedValueResponses(productByFields);

        log.info("Buscando valores agregados de produto inseridos pelo usuario:{}", userId);

        return ObjectCreationUtil.createPage(valueResponseList, page, pageSize, sortBy, sort);
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
        List<AggregatedValueResponse> valueResponseList = ObjectCreationUtil.createAggregatedValueResponses(productByFields);

        log.info("Buscando valores agregados de produto filtrando por múltiplos campos");

        return ObjectCreationUtil.createPage(valueResponseList, page, pageSize, sortBy, sort);
    }

    public AuditChanges getDetailedRevisions(Long revisionId) {
        Tuple tuple = productRepository.findRevisionByRevType(revisionId);
        Javers javers = JaversBuilder.javers().build();

        log.info("Buscando auditoria com id:{}", revisionId);

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

            log.info("Buscando dados detalhados da última movimentação antes de auditoria:{}", revisionId);

            return AuditChanges.builder()
                    .changes(changesResponse)
                    .description("Detalhamento auditoria de ID:" + revisionId)
                    .build();
        }


        log.info("Sem movimento de atualização ou inserção anteriormente a id:{}", revisionId);

        return AuditChanges.builder()
                .description("Auditoria de ID:" + revisionId + " não é do tipo atualização ou não existe")
                .build();

    }

    public ResponseEntity<Object> saveFileToProduct(Long productId, MultipartFile file) throws ProductNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        product.setImage(file.getName());

        productRepository.save(product);

        log.info("Salvando imagem para o produto com id:{}", productId);

        return ResponseEntity.ok("Sucesso");
    }

    private boolean checkIfUserIsAdmin(String token) {
        return tokenService.checkIfUserIsAdmin(token);
    }

    private boolean checkIfProductExist(Long productId) throws ProductNotFoundException {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId.toString())).isActiveProduct();
    }

}
