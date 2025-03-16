package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateCartRequest;
import assessment.estore.model.dto.request.ModifyCartRequest;
import assessment.estore.model.dto.response.*;
import assessment.estore.service.CartService;
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
public class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private ObjectMapper objectMapper;
    private String cartId;
    private String productId;
    private String userId;
    private CreateCartRequest createCartRequest;
    private CreateCartResponse createCartResponse;
    private GetCartResponse getCartResponse;
    private ModifyCartRequest modifyCartRequest;
    private BaseResponse baseResponse;
    private ReceiptResponse receiptResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();

        cartId = UUID.randomUUID().toString();
        productId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();

        createCartRequest = new CreateCartRequest();
        createCartRequest.setUserId(userId);

        createCartResponse = new CreateCartResponse();
        createCartResponse.setCartId(cartId);
        createCartResponse.setMessage("Successfully created cart.");
        createCartResponse.setError(false);

        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setItemId(UUID.randomUUID().toString());
        cartItemResponse.setProductId(productId);
        cartItemResponse.setQuantity(2);

        getCartResponse = new GetCartResponse();
        getCartResponse.setCartId(cartId);
        getCartResponse.setUserId(userId);
        getCartResponse.setCartStatus("ACTIVE");
        getCartResponse.setCartItems(Collections.singletonList(cartItemResponse));
        getCartResponse.setMessage("Successfully retrieved cart.");
        getCartResponse.setError(false);

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setProductId(productId);
        modifyCartRequest.setQuantity(3);

        baseResponse = new BaseResponse();
        baseResponse.setMessage("Item added to cart");
        baseResponse.setError(false);

        ReceiptItem receiptItem = new ReceiptItem();
        receiptItem.setProductId(productId);
        receiptItem.setProductName("Test Product");
        receiptItem.setQuantity(2);
        receiptItem.setPrice(new BigDecimal("99.99"));
        receiptItem.setFinalPrice(new BigDecimal("199.98"));
        receiptItem.setDiscountName("Test Discount");
        receiptItem.setDiscountAmount(new BigDecimal("0.00"));

        AppliedDiscount appliedDiscount = new AppliedDiscount();
        appliedDiscount.setDiscountName("Test Discount");
        appliedDiscount.setDiscountAmount(new BigDecimal("0.00"));

        receiptResponse = new ReceiptResponse();
        receiptResponse.setCartId(cartId);
        receiptResponse.setItems(Collections.singletonList(receiptItem));
        receiptResponse.setSubtotal(new BigDecimal("199.98"));
        receiptResponse.setTotalDiscount(new BigDecimal("0.00"));
        receiptResponse.setFinalTotal(new BigDecimal("199.98"));
        receiptResponse.setAppliedDiscounts(Collections.singletonList(appliedDiscount));
        receiptResponse.setMessage("Receipt generated successfully");
        receiptResponse.setError(false);
    }

    @Test
    void createCart_Success() throws Exception {
        when(cartService.createCart(any(CreateCartRequest.class))).thenReturn(createCartResponse);

        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId").value(cartId))
                .andExpect(jsonPath("$.message").value("Successfully created cart."))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void createCart_Error() throws Exception {
        CreateCartResponse errorResponse = new CreateCartResponse();
        errorResponse.setMessage("Invalid user id");
        errorResponse.setError(true);

        when(cartService.createCart(any(CreateCartRequest.class))).thenReturn(errorResponse);

        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid user id"))
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void getCart_Success() throws Exception {
        when(cartService.getCart(eq(cartId))).thenReturn(getCartResponse);

        mockMvc.perform(get("/carts/{cartId}", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.cartStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems[0].productId").value(productId))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2))
                .andExpect(jsonPath("$.message").value("Successfully retrieved cart."))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void addCartItem_Success() throws Exception {
        when(cartService.addItemToCart(eq(cartId), any(ModifyCartRequest.class))).thenReturn(baseResponse);

        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item added to cart"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void addCartItem_Error() throws Exception {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setMessage("Product not found");
        errorResponse.setError(true);

        when(cartService.addItemToCart(eq(cartId), any(ModifyCartRequest.class))).thenReturn(errorResponse);

        mockMvc.perform(post("/carts/{cartId}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void removeCartItem_Success() throws Exception {
        when(cartService.removeItemFromCart(eq(cartId), eq(productId))).thenReturn(baseResponse);

        mockMvc.perform(delete("/carts/{cartId}/items/{productId}", cartId, productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item added to cart"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void generateReceipt_Success() throws Exception {
        when(cartService.getReceipt(eq(cartId))).thenReturn(receiptResponse);

        mockMvc.perform(get("/carts/{cartId}/receipt", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productId").value(productId))
                .andExpect(jsonPath("$.items[0].productName").value("Test Product"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].price").value(99.99))
                .andExpect(jsonPath("$.subtotal").value(199.98))
                .andExpect(jsonPath("$.totalDiscount").value(0.00))
                .andExpect(jsonPath("$.finalTotal").value(199.98))
                .andExpect(jsonPath("$.appliedDiscounts").isArray())
                .andExpect(jsonPath("$.message").value("Receipt generated successfully"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void generateReceipt_Error() throws Exception {
        ReceiptResponse errorResponse = new ReceiptResponse();
        errorResponse.setMessage("Cart not found");
        errorResponse.setError(true);

        when(cartService.getReceipt(eq(cartId))).thenReturn(errorResponse);

        mockMvc.perform(get("/carts/{cartId}/receipt", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart not found"))
                .andExpect(jsonPath("$.error").value(true));
    }
}