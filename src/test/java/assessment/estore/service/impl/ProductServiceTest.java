package assessment.estore.service.impl;

import assessment.estore.model.dao.Product;
import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;
import assessment.estore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID validProductId;
    private Product testProduct;
    private CreateProductRequest createProductRequest;

    @BeforeEach
    void setUp() {
        validProductId = UUID.randomUUID();

        testProduct = new Product();
        testProduct.setId(validProductId);
        testProduct.setProductName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);

        createProductRequest = new CreateProductRequest();
        createProductRequest.setProductName("New Product");
        createProductRequest.setDescription("New Description");
        createProductRequest.setPrice(new BigDecimal("149.99"));
        createProductRequest.setStockQuantity(20);
    }

    @Test
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(UUID.randomUUID());
            return savedProduct;
        });

        BaseResponse response = productService.createProduct(createProductRequest);

        assertFalse(response.getError());
        assertEquals("Product created successfully", response.getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_RepositoryException() {
        when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException("DB Error"));

        BaseResponse response = productService.createProduct(createProductRequest);

        assertTrue(response.getError());
        assertEquals("Product creation failed", response.getMessage());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        doNothing().when(productRepository).deleteById(any(UUID.class));

        Boolean result = productService.deleteProduct(validProductId.toString());

        assertTrue(result);
        verify(productRepository, times(1)).deleteById(validProductId);
    }

    @Test
    void deleteProduct_InvalidUUID() {
        Boolean result = productService.deleteProduct("invalid-uuid");

        assertFalse(result);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void deleteProduct_RepositoryException() {
        doThrow(new RuntimeException("DB Error")).when(productRepository).deleteById(any(UUID.class));

        Boolean result = productService.deleteProduct(validProductId.toString());

        assertFalse(result);
        verify(productRepository, times(1)).deleteById(validProductId);
    }

    @Test
    void getProduct_Success() {
        when(productRepository.findById(validProductId)).thenReturn(Optional.of(testProduct));

        ProductDetailResponse response = productService.getProduct(validProductId.toString());

        assertNotNull(response);
        assertEquals(validProductId.toString(), response.getProductId());
        assertEquals(testProduct.getProductName(), response.getProductName());
        assertEquals(testProduct.getDescription(), response.getProductDescription());
        assertEquals(testProduct.getPrice(), response.getProductPrice());
        assertEquals(testProduct.getStockQuantity(), response.getProductQuantity());
        verify(productRepository, times(1)).findById(validProductId);
    }

    @Test
    void getProduct_InvalidUUID() {
        ProductDetailResponse response = productService.getProduct("invalid-uuid");

        assertNull(response);
        verify(productRepository, never()).findById(any());
    }

    @Test
    void getProduct_NotFound() {
        when(productRepository.findById(validProductId)).thenReturn(Optional.empty());

        ProductDetailResponse response = productService.getProduct(validProductId.toString());

        assertNull(response);
        verify(productRepository, times(1)).findById(validProductId);
    }

    @Test
    void getProducts_Success() {
        Product product1 = new Product();
        product1.setId(UUID.randomUUID());
        product1.setProductName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStockQuantity(10);

        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setProductName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setStockQuantity(20);

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        GetProductsResponse response = productService.getProducts(0, 10);

        assertNotNull(response);
        assertEquals(2, response.getProducts().size());

        assertEquals(product1.getId().toString(), response.getProducts().get(0).getProductId());
        assertEquals(product1.getProductName(), response.getProducts().get(0).getProductName());

        assertEquals(product2.getId().toString(), response.getProducts().get(1).getProductId());
        assertEquals(product2.getProductName(), response.getProducts().get(1).getProductName());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProducts_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        GetProductsResponse response = productService.getProducts(0, 10);

        assertNotNull(response);
        assertTrue(response.getProducts().isEmpty());
        verify(productRepository, times(1)).findAll();
    }
}