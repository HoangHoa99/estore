package assessment.estore.service.impl;

import assessment.estore.model.dao.Cart;
import assessment.estore.model.dao.CartItem;
import assessment.estore.model.dao.Discount;
import assessment.estore.model.dao.Product;
import assessment.estore.model.dto.request.CreateCartRequest;
import assessment.estore.model.dto.request.ModifyCartRequest;
import assessment.estore.model.dto.response.*;
import assessment.estore.repository.CartItemRepository;
import assessment.estore.repository.CartRepository;
import assessment.estore.repository.DiscountRepository;
import assessment.estore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID userId;
    private UUID cartId;
    private UUID productId;
    private UUID cartItemId;
    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;
    private Discount testDiscount;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cartItemId = UUID.randomUUID();

        // Setup test product
        testProduct = new Product();
        testProduct.setId(productId);
        testProduct.setProductName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);

        // Setup test cart
        testCart = new Cart();
        testCart.setId(cartId);
        testCart.setUserId(userId);
        testCart.setStatus(Cart.CartStatus.ACTIVE);

        // Setup test cart item
        testCartItem = new CartItem();
        testCartItem.setId(cartItemId);
        testCartItem.setCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);

        List<CartItem> items = new ArrayList<>();
        items.add(testCartItem);
        testCart.setItems(items);

        // Setup test discount
        testDiscount = new Discount();
        testDiscount.setId(UUID.randomUUID());
        testDiscount.setDiscountName("Buy 1 Get 50% Off Second");
        testDiscount.setDiscountType(Discount.DiscountType.PERCENTAGE);
        testDiscount.setDiscountValue(new BigDecimal("50.0"));
        testDiscount.setStartDate(LocalDateTime.now().minusDays(1));
        testDiscount.setEndDate(LocalDateTime.now().plusDays(1));
        testDiscount.setProduct(testProduct);
    }

    @Test
    void createCart_Success() {
        // Arrange
        CreateCartRequest request = new CreateCartRequest();
        request.setUserId(userId.toString());

        when(cartRepository.findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(cartId);
            return savedCart;
        });

        // Act
        CreateCartResponse response = cartService.createCart(request);

        // Assert
        assertFalse(response.getError());
        assertEquals("Successfully created cart.", response.getMessage());
        assertEquals(cartId.toString(), response.getCartId());
        verify(cartRepository, times(1)).findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void createCart_InvalidUserId() {
        // Arrange
        CreateCartRequest request = new CreateCartRequest();
        request.setUserId("invalid-uuid");

        // Act
        CreateCartResponse response = cartService.createCart(request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid user id", response.getMessage());
        verify(cartRepository, never()).findCartByUserIdAndStatus(any(), any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void createCart_ActiveCartExists() {
        // Arrange
        CreateCartRequest request = new CreateCartRequest();
        request.setUserId(userId.toString());

        when(cartRepository.findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE))
                .thenReturn(Optional.of(testCart));

        // Act
        CreateCartResponse response = cartService.createCart(request);

        // Assert
        assertFalse(response.getError());
        assertEquals("Active cart already exists.", response.getMessage());
        assertEquals(cartId.toString(), response.getCartId());
        verify(cartRepository, times(1)).findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void createCart_RepositoryException() {
        // Arrange
        CreateCartRequest request = new CreateCartRequest();
        request.setUserId(userId.toString());

        when(cartRepository.findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        CreateCartResponse response = cartService.createCart(request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Error occurred while creating the cart", response.getMessage());
        verify(cartRepository, times(1)).findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCart_Success() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

        // Act
        GetCartResponse response = cartService.getCart(cartId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Successfully retrieved cart.", response.getMessage());
        assertEquals(cartId.toString(), response.getCartId());
        assertEquals(userId.toString(), response.getUserId());
        assertEquals(Cart.CartStatus.ACTIVE.toString(), response.getCartStatus());
        assertEquals(1, response.getCartItems().size());

        CartItemResponse itemResponse = response.getCartItems().get(0);
        assertEquals(cartItemId.toString(), itemResponse.getItemId());
        assertEquals(productId.toString(), itemResponse.getProductId());
        assertEquals(2, itemResponse.getQuantity());

        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void getCart_InvalidCartId() {
        // Act
        GetCartResponse response = cartService.getCart("invalid-uuid");

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid cart ID format", response.getMessage());
        verify(cartRepository, never()).findById(any());
    }

    @Test
    void getCart_CartNotFound() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act
        GetCartResponse response = cartService.getCart(cartId.toString());

        // Assert
        assertTrue(response.getError());
        assertEquals("Cart not found", response.getMessage());
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void getCart_RepositoryException() {
        // Arrange
        when(cartRepository.findById(cartId)).thenThrow(new RuntimeException("Database error"));

        // Act
        GetCartResponse response = cartService.getCart(cartId.toString());

        // Assert
        assertTrue(response.getError());
        assertEquals("Error occurred while retrieving the cart", response.getMessage());
        verify(cartRepository, times(1)).findById(cartId);
    }

    @Test
    void addItemToCart_AddNewItem() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(3);

        // Use a cart with no items initially
        Cart emptyCart = new Cart();
        emptyCart.setId(cartId);
        emptyCart.setUserId(userId);
        emptyCart.setStatus(Cart.CartStatus.ACTIVE);
        emptyCart.setItems(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(emptyCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertFalse(response.getError());
        assertEquals("Item added to cart", response.getMessage());
        assertEquals(1, emptyCart.getItems().size());
        assertEquals(productId, emptyCart.getItems().get(0).getProduct().getId());
        assertEquals(3, emptyCart.getItems().get(0).getQuantity());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).save(emptyCart);
    }

    @Test
    void addItemToCart_UpdateExistingItem() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(5); // Updating from 2 to 5

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertFalse(response.getError());
        assertEquals("Cart item quantity updated", response.getMessage());
        assertEquals(1, testCart.getItems().size());
        assertEquals(5, testCart.getItems().get(0).getQuantity());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void addItemToCart_RemoveItemWithZeroQuantity() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(0); // Setting to 0 should remove the item

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertFalse(response.getError());
        assertEquals("Item removed from cart", response.getMessage());
        assertTrue(testCart.getItems().isEmpty());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).save(testCart);
    }

    @Test
    void addItemToCart_InvalidCartId() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(3);

        // Act
        BaseResponse response = cartService.addItemToCart("invalid-uuid", request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid cart ID or product ID format", response.getMessage());

        verify(cartRepository, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemToCart_InvalidProductId() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId("invalid-uuid");
        request.setQuantity(3);

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid cart ID or product ID format", response.getMessage());

        verify(cartRepository, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemToCart_CartNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(3);

        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Cart not found", response.getMessage());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemToCart_ProductNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(3);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Product not found", response.getMessage());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemToCart_NonActiveCart() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setProductId(productId.toString());
        request.setQuantity(3);

        // Create a completed cart
        Cart completedCart = new Cart();
        completedCart.setId(cartId);
        completedCart.setUserId(userId);
        completedCart.setStatus(Cart.CartStatus.COMPLETED);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(completedCart));

        // Act
        BaseResponse response = cartService.addItemToCart(cartId.toString(), request);

        // Assert
        assertTrue(response.getError());
        assertEquals("Cannot modify items in a non-active cart", response.getMessage());

        verify(cartRepository, times(1)).findById(cartId);
        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeItemFromCart_Success() {
        // Arrange
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.deleteCartItem(cartItemId)).thenReturn(1);

        // Act
        BaseResponse response = cartService.removeItemFromCart(cartId.toString(), productId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Item successfully removed from cart", response.getMessage());

        verify(cartItemRepository, times(1)).findByCartIdAndProductId(cartId, productId);
        verify(cartItemRepository, times(1)).deleteCartItem(cartItemId);
    }

    @Test
    void removeItemFromCart_ItemNotFound() {
        // Arrange
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.empty());

        // Act
        BaseResponse response = cartService.removeItemFromCart(cartId.toString(), productId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Item not found in cart", response.getMessage());

        verify(cartItemRepository, times(1)).findByCartIdAndProductId(cartId, productId);
        verify(cartItemRepository, never()).deleteCartItem(any());
    }

    @Test
    void removeItemFromCart_InvalidIds() {
        // Act
        BaseResponse response = cartService.removeItemFromCart("invalid-uuid", productId.toString());

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid cart ID or product ID format", response.getMessage());

        verify(cartItemRepository, never()).findByCartIdAndProductId(any(), any());
        verify(cartItemRepository, never()).deleteCartItem(any());
    }

    @Test
    void getReceipt_Success() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(discountRepository.findActiveDiscountsByProductId(productId))
                .thenReturn(Collections.singletonList(testDiscount));

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Receipt generated successfully", response.getMessage());
        assertEquals(cartId.toString(), response.getCartId());

        // Verify items
        assertEquals(1, response.getItems().size());
        ReceiptItem item = response.getItems().get(0);
        assertEquals(productId.toString(), item.getProductId());
        assertEquals(testProduct.getProductName(), item.getProductName());
        assertEquals(2, item.getQuantity());
        assertEquals(testProduct.getPrice(), item.getPrice());

        // Verify subtotal calculation (99.99 * 2)
        assertEquals(new BigDecimal("199.98"), response.getSubtotal());

        // Verify discount application (50% off second item = 49.995)
        BigDecimal expectedDiscount = new BigDecimal("49.995").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscount.doubleValue(), response.getTotalDiscount().doubleValue(), 0.01);

        // Verify final total
        BigDecimal expectedTotal = new BigDecimal("149.98").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedTotal.doubleValue(), response.getFinalTotal().doubleValue(), 0.01);

        // Verify applied discounts
        assertEquals(1, response.getAppliedDiscounts().size());
        AppliedDiscount appliedDiscount = response.getAppliedDiscounts().get(0);
        assertEquals("Buy 1 Get 50% Off Second", appliedDiscount.getDiscountName());
        assertEquals(expectedDiscount.doubleValue(), appliedDiscount.getDiscountAmount().doubleValue(), 0.01);

        verify(cartRepository, times(1)).findById(cartId);
        verify(discountRepository, times(1)).findActiveDiscountsByProductId(productId);
    }

    @Test
    void getReceipt_NoDiscounts() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(discountRepository.findActiveDiscountsByProductId(productId))
                .thenReturn(Collections.emptyList());

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Receipt generated successfully", response.getMessage());

        // Verify subtotal calculation (99.99 * 2)
        assertEquals(new BigDecimal("199.98"), response.getSubtotal());

        // No discounts should be applied
        assertEquals(BigDecimal.ZERO, response.getTotalDiscount());

        // Final total should equal subtotal
        assertEquals(response.getSubtotal(), response.getFinalTotal());

        // No applied discounts
        assertTrue(response.getAppliedDiscounts().isEmpty());

        verify(cartRepository, times(1)).findById(cartId);
        verify(discountRepository, times(1)).findActiveDiscountsByProductId(productId);
    }

    @Test
    void getReceipt_InvalidCartId() {
        // Act
        ReceiptResponse response = cartService.getReceipt("invalid-uuid");

        // Assert
        assertTrue(response.getError());
        assertEquals("Invalid cart ID format", response.getMessage());

        verify(cartRepository, never()).findById(any());
        verify(discountRepository, never()).findActiveDiscountsByProductId(any());
    }

    @Test
    void getReceipt_CartNotFound() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertTrue(response.getError());
        assertEquals("Cart not found", response.getMessage());

        verify(cartRepository, times(1)).findById(cartId);
        verify(discountRepository, never()).findActiveDiscountsByProductId(any());
    }

    @Test
    void getReceipt_EmptyCart() {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(cartId);
        emptyCart.setUserId(userId);
        emptyCart.setStatus(Cart.CartStatus.ACTIVE);
        emptyCart.setItems(new ArrayList<>());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(emptyCart));

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Receipt generated successfully", response.getMessage());
        assertEquals(cartId.toString(), response.getCartId());
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getSubtotal());
        assertEquals(BigDecimal.ZERO, response.getTotalDiscount());
        assertEquals(BigDecimal.ZERO, response.getFinalTotal());

        verify(cartRepository, times(1)).findById(cartId);
        verify(discountRepository, never()).findActiveDiscountsByProductId(any());
    }

    @Test
    void getReceipt_MultipleItems() {
        // Arrange
        // Create a second product
        UUID product2Id = UUID.randomUUID();
        Product product2 = new Product();
        product2.setId(product2Id);
        product2.setProductName("Second Product");
        product2.setDescription("Another product");
        product2.setPrice(new BigDecimal("49.99"));
        product2.setStockQuantity(5);

        // Create a second cart item
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(UUID.randomUUID());
        cartItem2.setCart(testCart);
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(3);

        // Add the second item to the cart
        testCart.getItems().add(cartItem2);

        // Create a discount for the second product
        Discount discount2 = new Discount();
        discount2.setId(UUID.randomUUID());
        discount2.setDiscountName("10% Off");
        discount2.setDiscountType(Discount.DiscountType.PERCENTAGE);
        discount2.setDiscountValue(new BigDecimal("10.0"));
        discount2.setStartDate(LocalDateTime.now().minusDays(1));
        discount2.setEndDate(LocalDateTime.now().plusDays(1));
        discount2.setProduct(product2);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(discountRepository.findActiveDiscountsByProductId(productId))
                .thenReturn(Collections.singletonList(testDiscount));
        when(discountRepository.findActiveDiscountsByProductId(product2Id))
                .thenReturn(Collections.singletonList(discount2));

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertFalse(response.getError());
        assertEquals("Receipt generated successfully", response.getMessage());

        // Verify items count
        assertEquals(2, response.getItems().size());

        // Calculate expected values:
        // First item: 2 x $99.99 = $199.98, with 50% off second = $49.995 discount
        // Second item: 3 x $49.99 = $149.97, with 10% off = $14.997 discount
        // Total subtotal: $349.95
        // Total discount: $64.992
        // Final total: $284.958

        BigDecimal expectedSubtotal = new BigDecimal("349.95");
        BigDecimal expectedDiscount = new BigDecimal("65.0").setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedTotal = new BigDecimal("284.958").setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedSubtotal.doubleValue(), response.getSubtotal().doubleValue(), 0.01);
        assertEquals(expectedDiscount.doubleValue(), response.getTotalDiscount().doubleValue(), 0.01);
        assertEquals(expectedTotal.doubleValue(), response.getFinalTotal().doubleValue(), 0.01);

        // Verify 2 applied discounts
        assertEquals(2, response.getAppliedDiscounts().size());

        verify(cartRepository, times(1)).findById(cartId);
        verify(discountRepository, times(1)).findActiveDiscountsByProductId(productId);
        verify(discountRepository, times(1)).findActiveDiscountsByProductId(product2Id);
    }

    @Test
    void getReceipt_RepositoryException() {
        // Arrange
        when(cartRepository.findById(cartId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ReceiptResponse response = cartService.getReceipt(cartId.toString());

        // Assert
        assertTrue(response.getError());
        assertEquals("Error occurred while generating receipt", response.getMessage());

        verify(cartRepository, times(1)).findById(cartId);
    }
}