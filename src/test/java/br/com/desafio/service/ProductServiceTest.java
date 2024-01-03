package br.com.desafio.service;

import br.com.desafio.DTO.Request.ProductRequest;
import br.com.desafio.DTO.Response.AggregatedValueResponse;
import br.com.desafio.DTO.Response.ProductResponse;
import br.com.desafio.converter.ProductConverter;
import br.com.desafio.entity.Category;
import br.com.desafio.entity.Product;
import br.com.desafio.entity.User;
import br.com.desafio.exception.Category.CategoryNotFoundException;
import br.com.desafio.exception.Product.ProductNotFoundException;
import br.com.desafio.exception.User.UserNotFoundException;
import br.com.desafio.repository.ProductRepository;
import br.com.desafio.security.TokenService;
import br.com.desafio.validator.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private TokenService tokenService;
    @Mock
    private ProductConverter productConverter;
    ProductRequest productRequest;
    ProductRequest productRequestWithNullValues;
    ProductResponse productResponse;
    Product product;
    User user;
    Category category;
    List<String> errorList = new ArrayList<>();
    List<String> fieldsList = new ArrayList<>();
    @Mock
    private ObjectsValidator<ProductRequest> objectsValidator;
    @Mock
    private UserFieldVisibilityService userFieldVisibilityService;
    private static final String TOKEN_EXAMPLE = "secret_token";
    Pageable pageable;
    Page<Product> productPage;
    AggregatedValueResponse aggregatedValueResponse;

    @BeforeEach
    public void setUp(){
        productRequest = new ProductRequest(
                "Teste",
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(40),
                "Imagem-Teste.png",
                1L,
                10L);
        productRequestWithNullValues = new ProductRequest(
                null,
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                BigDecimal.valueOf(40),
                "Imagem-Teste.png",
                1L,
                10L);
        product = new Product(1L,
                "Teste",
                true,
                "Teste SKU",
                1L,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(1.5),
                "Imagem-Teste.png",
                LocalDateTime.now(),
                null,
                1L,
                10L);

        user = User.builder().id(1L).email("teste@gmail.com").build();
        category = Category.builder().categoryId(1L).categoryName("Teste").build();
        productResponse = new ProductResponse(1L,
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
        errorList.add("Erro por nome ser nulo");
        fieldsList.add("name");
        fieldsList.add("productId");
        pageable = PageRequest.of(0, 5, Sort.Direction.ASC, "productId");
        productPage = new PageImpl<>(Collections.singletonList(product));
        aggregatedValueResponse = AggregatedValueResponse.builder().
                cost(BigDecimal.TEN)
                .productId(1L)
                .totalCost(productService.getTotalCost(BigDecimal.TEN, product.getQuantity()))
                .forecast(productService.getTotalReturn(product.getQuantity(), product.getRevenueValue()))
                .build();
    }

    @Test
    void shouldSaveProduct() throws Exception {
        when(productRepository.save(productConverter.convertProductRequestToProduct(productRequest))).thenReturn(product);
        when(productConverter.convertProductToProductResponse(product)).thenReturn(productResponse);
        when(userService.getUser(1L)).thenReturn(user);
        when(categoryService.getCategory(1L)).thenReturn(category);
        ResponseEntity<Object> result = productService.saveProduct(productRequest);

        assertEquals(Objects.requireNonNull(result.getBody()).toString(), "Product added successfully");
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void shouldNotSaveProductBecauseUserOrCategoryNotExist() throws Exception {
        boolean result = productService.checkIfUserAndCategoryExists(product.getUserId(), product.getCategoryId());

        assertFalse(result);
    }

    @Test
    void shouldNotSaveProductBecauseSomeAttributeIsEmpty() throws Exception {
        when(objectsValidator.validate(productRequestWithNullValues)).thenReturn(errorList);

        productService.saveProduct(productRequestWithNullValues);

        assertThat(errorList, notNullValue());
    }

    @Test
    void shouldGetProductForAdmin() throws ProductNotFoundException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productConverter.convertProductToProductResponse(product)).thenReturn(productResponse);
        when(tokenService.checkIfUserIsAdmin(TOKEN_EXAMPLE)).thenReturn(true);
        ProductResponse result = productService.getProduct(1L, TOKEN_EXAMPLE);

        assertNotNull(result);
    }

    @Test
    void shouldGetProductForEstoquista() throws ProductNotFoundException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(tokenService.checkIfUserIsAdmin(TOKEN_EXAMPLE)).thenReturn(false);
        when(userFieldVisibilityService.getVisibleFieldsForEstoquista()).thenReturn(fieldsList);
        when(productConverter.productToProductResponseEstoquista(product, fieldsList)).thenReturn(productResponse);
        ProductResponse result = productService.getProduct(1L, TOKEN_EXAMPLE);

        assertNotNull(result);
    }

    @Test
    void shouldNotGetProductBecauseNotExist(){
        final ProductNotFoundException e = assertThrows(ProductNotFoundException.class, () -> {
            productService.getProduct(2L, TOKEN_EXAMPLE);
        });

        assertThat(e, notNullValue());
    }

    @Test
    public void shouldDeleteProduct() throws Exception {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(tokenService.checkIfUserIsAdmin(TOKEN_EXAMPLE)).thenReturn(true);

        ResponseEntity<String> response = productService.deleteProduct(1L, TOKEN_EXAMPLE);

        verify(productRepository, times(1)).deleteById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deletado com sucesso", response.getBody());
    }

    @Test
    public void shouldNotDeleteProductBecauseNotExist() {
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L, TOKEN_EXAMPLE));
    }

    @Test
    void shouldDeactivateProduct() throws ProductNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ResponseEntity<String> response = productService.deactivateProduct(1L, TOKEN_EXAMPLE);

        verify(productRepository, times(1)).deactivateProduct(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldNotDeactivateProductBecauseNotExist() {
        when(productRepository.findById(1L)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.deactivateProduct(1L, TOKEN_EXAMPLE));
    }

    @Test
    void shouldGetProductsByUser() throws UserNotFoundException {
        when(productRepository.findByUserId(1L, pageable)).thenReturn(productPage);
        when(userService.getUser(1L)).thenReturn(user);
        when(productConverter.convertProductPageToProductResponsePage(productPage)).thenReturn(new PageImpl<>(Collections.singletonList(productResponse)));
        Page<ProductResponse> response = productService.getProductsByUser(1L,
                Optional.of(0),
                Optional.of("productId"),
                Optional.of(5),
                Optional.of(Sort.Direction.ASC));

        assertNotNull(response);
        assertEquals(response.getSize(), 1);
    }

    @Test
    void shouldGetProductsByFields() {
        when(productRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(productPage);
        when(productConverter.convertProductPageToProductResponsePage(productPage)).thenReturn(new PageImpl<>(Collections.singletonList(productResponse)));
        Page<ProductResponse> response = productService.getProductByFields(Optional.of(product.getUserId()),
                Optional.of(0),
                Optional.of("productId"),
                Optional.of(5),
                Optional.of(Sort.Direction.ASC),
                Optional.of(1L),
                Optional.of(product.getName()),
                Optional.of(product.getEntryDate()),
                Optional.of(product.isActiveProduct()),
                Optional.of(product.getSKU()),
                Optional.of(product.getCategoryId()),
                Optional.of(product.getCost()),
                Optional.of(product.getICMS()),
                Optional.of(product.getRevenueValue()),
                Optional.of(product.getQuantity()));

        assertNotNull(response);
        assertEquals(response.getSize(), 1);
    }

    @Test
    void shouldGetAggregatedValueByUser() throws UserNotFoundException {
        when(productRepository.findByUserId(1L, pageable)).thenReturn(productPage);
        when(userService.getUser(1L)).thenReturn(user);
        when(productService.getProductsByUser(1L, Optional.of(0),
                Optional.of("productId"),
                Optional.of(5),
                Optional.of(Sort.Direction.ASC))).thenReturn(new PageImpl<>(Collections.singletonList(productResponse)));
        Page<AggregatedValueResponse> productAggregatedResponse = productService.getAggregatedValueByUser(1L, Optional.of(0),
                Optional.of("productId"),
                Optional.of(5),
                Optional.of(Sort.Direction.ASC));

        assertNotNull(productAggregatedResponse);
        assertEquals(productAggregatedResponse.getTotalElements(), 1);
    }

    @Test
    void shouldGetAggregatedValueByFields() throws UserNotFoundException {
        when(productRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(productPage);
        when(productService.getProductByFields(Optional.of(product.getUserId()),
                Optional.of(0),
                Optional.of("productId"),
                Optional.of(5),
                Optional.of(Sort.Direction.ASC),
                Optional.of(1L),
                Optional.of(product.getName()),
                Optional.of(product.getEntryDate()),
                Optional.of(product.isActiveProduct()),
                Optional.of(product.getSKU()),
                Optional.of(product.getCategoryId()),
                Optional.of(product.getCost()),
                Optional.of(product.getICMS()),
                Optional.of(product.getRevenueValue()),
                Optional.of(product.getQuantity()))).thenReturn(new PageImpl<>(Collections.singletonList(productResponse)));
        Page<AggregatedValueResponse> productAggregatedResponse = productService.getAggregatedValueByFields(Optional.of(product.getUserId()),
                Optional.of(product.getProductId()),
                Optional.of(product.getName()),
                Optional.of(product.getEntryDate()),
                Optional.of(product.isActiveProduct()),
                Optional.of(product.getSKU()),
                Optional.of(product.getCategoryId()),
                Optional.of(product.getCost()),
                Optional.of(product.getICMS()),
                Optional.of(product.getRevenueValue()),
                Optional.of(product.getQuantity()),
                Optional.of(0),
                Optional.of("productId"),
                Optional.of(Sort.Direction.ASC),
                Optional.of(5));

        assertNotNull(productAggregatedResponse);
        assertEquals(productAggregatedResponse.getTotalElements(), 1);
    }

    @Test
    public void shouldSaveImage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        ResponseEntity<Object> response = productService.saveFileToProduct(1L, file);

        verify(productRepository).save(product);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    void shouldUpdateProduct() throws UserNotFoundException, CategoryNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        when(userService.getUser(1L)).thenReturn(user);
        when(categoryService.getCategory(1L)).thenReturn(category);
        when(tokenService.checkIfUserIsAdmin(TOKEN_EXAMPLE)).thenReturn(true);

        ResponseEntity<List<String>> response = productService.updateProduct(1L, productRequest, TOKEN_EXAMPLE);

        verify(productRepository, times(1)).save(product);
        assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    void shouldInsertIfProductNotExist() throws UserNotFoundException, CategoryNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(product);
        when(userService.getUser(1L)).thenReturn(user);
        when(categoryService.getCategory(1L)).thenReturn(category);

        ResponseEntity<List<String>> response = productService.updateProduct(1L, productRequest, TOKEN_EXAMPLE);

        verify(productRepository, times(1)).save(any());
        assertEquals(response.getStatusCode().value(), 200);
    }

}
