package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;
import assessment.estore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private CreateProductRequest createProductRequest;
    private BaseResponse baseResponse;
    private ProductDetailResponse productDetailResponse;
    private GetProductsResponse getProductsResponse;
    private String productId;
    private ProductDetailResponse productNotFound;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
        productId = UUID.randomUUID().toString();

        createProductRequest = new CreateProductRequest();
        createProductRequest.setProductName("Test Product");
        createProductRequest.setDescription("Test Description");
        createProductRequest.setPrice(new BigDecimal("99.99"));
        createProductRequest.setStockQuantity(10);

        baseResponse = new BaseResponse();
        baseResponse.setMessage("Product created successfully");
        baseResponse.setError(false);

        productDetailResponse = new ProductDetailResponse();
        productDetailResponse.setProductId(productId);
        productDetailResponse.setProductName("Test Product");
        productDetailResponse.setProductDescription("Test Description");
        productDetailResponse.setProductPrice(new BigDecimal("99.99"));
        productDetailResponse.setProductQuantity(10);

        getProductsResponse = new GetProductsResponse();
        getProductsResponse.setProducts(Collections.singletonList(productDetailResponse));

        productNotFound = new ProductDetailResponse();
        productNotFound.setError(true);
        productNotFound.setMessage("Product not found");
    }

    @Test
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(baseResponse);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void getProducts_Success() throws Exception {
        when(productService.getProducts(eq(0), eq(10))).thenReturn(getProductsResponse);

        mockMvc.perform(get("/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products").isArray())
                .andExpect(jsonPath("$.products[0].productId").value(productId))
                .andExpect(jsonPath("$.products[0].productName").value("Test Product"));
    }

    @Test
    void getProductDetail_Success() throws Exception {
        when(productService.getProduct(eq(productId))).thenReturn(productDetailResponse);

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.productDescription").value("Test Description"))
                .andExpect(jsonPath("$.productPrice").value(99.99))
                .andExpect(jsonPath("$.productQuantity").value(10));
    }

    @Test
    void getProductDetail_NotFound() throws Exception {
        when(productService.getProduct(eq(productId))).thenReturn(productNotFound);

        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        when(productService.deleteProduct(eq(productId))).thenReturn(true);

        mockMvc.perform(delete("/products/{productId}", productId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.error").value(false))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

    @Test
    void deleteProduct_NotFound() throws Exception {
        when(productService.deleteProduct(eq(productId))).thenReturn(false);

        mockMvc.perform(delete("/products/{productId}", productId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(true))
                .andExpect(jsonPath("$.message").value("Failed to delete product"));
    }

    @Test
    void createProduct_ValidationFailure() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest();
        invalidRequest.setPrice(new BigDecimal("99.99"));
        invalidRequest.setStockQuantity(10);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProducts_InvalidParameters() throws Exception {
        mockMvc.perform(get("/products")
                        .param("page", "invalid")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}