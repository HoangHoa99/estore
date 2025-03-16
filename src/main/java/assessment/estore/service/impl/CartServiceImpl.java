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
import assessment.estore.service.CartService;
import assessment.estore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, DiscountRepository discountRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public CreateCartResponse createCart(CreateCartRequest createCartRequest) {
        CreateCartResponse createCartResponse = new CreateCartResponse();

        try {
            UUID userId = StringUtil.safeParseUUID(createCartRequest.getUserId());

            if (userId == null) {
                createCartResponse.setError(true);
                createCartResponse.setMessage("Invalid user id");

                return createCartResponse;
            }

            Optional<Cart> existedCart = cartRepository.findCartByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);

            if (existedCart.isPresent()) {
                createCartResponse.setCartId(existedCart.get().getId().toString());
                createCartResponse.setMessage("Active cart already exists.");

                return createCartResponse;
            }

            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setStatus(Cart.CartStatus.ACTIVE);

            Cart savedCart = cartRepository.save(cart);

            createCartResponse.setCartId(savedCart.getId().toString());
            createCartResponse.setMessage("Successfully created cart.");
        }
        catch (Exception e) {
            LOGGER.error("Error occurred while creating the cart", e);
            createCartResponse.setMessage("Error occurred while creating the cart");
            createCartResponse.setError(true);
        }
        return createCartResponse;
    }

    @Override
    public GetCartResponse getCart(String cartId) {
        GetCartResponse getCartResponse = new GetCartResponse();

        try {
            UUID id = StringUtil.safeParseUUID(cartId);

            if (id == null) {
                getCartResponse.setError(true);
                getCartResponse.setMessage("Invalid cart ID format");
                return getCartResponse;
            }

            Optional<Cart> cartOptional = cartRepository.findById(id);

            if (cartOptional.isEmpty()) {
                getCartResponse.setError(true);
                getCartResponse.setMessage("Cart not found");
                return getCartResponse;
            }

            Cart cart = cartOptional.get();

            getCartResponse.setCartId(cart.getId().toString());
            getCartResponse.setUserId(cart.getUserId().toString());
            getCartResponse.setCartStatus(cart.getStatus().toString());

            List<CartItemResponse> cartItems = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                CartItemResponse cartItemResponse = new CartItemResponse();
                cartItemResponse.setItemId(item.getId().toString());
                cartItemResponse.setProductId(item.getProduct().getId().toString());
                cartItemResponse.setQuantity(item.getQuantity());

                cartItems.add(cartItemResponse);
            }

            getCartResponse.setCartItems(cartItems);
            getCartResponse.setMessage("Successfully retrieved cart.");
        } catch (Exception e) {
            LOGGER.error("Error retrieving cart with ID: {}", cartId, e);
            getCartResponse.setError(true);
            getCartResponse.setMessage("Error occurred while retrieving the cart");
        }

        return getCartResponse;
    }

    @Override
    @Transactional
    public BaseResponse addItemToCart(String cartId, ModifyCartRequest modifyCartRequest) {
        BaseResponse response = new BaseResponse();

        try {
            UUID cartUuid = StringUtil.safeParseUUID(cartId);
            UUID productUuid = StringUtil.safeParseUUID(modifyCartRequest.getProductId());

            if (cartUuid == null || productUuid == null) {
                response.setError(true);
                response.setMessage("Invalid cart ID or product ID format");
                return response;
            }

            Optional<Cart> cartOptional = cartRepository.findById(cartUuid);
            if (cartOptional.isEmpty()) {
                response.setError(true);
                response.setMessage("Cart not found");
                return response;
            }

            Cart cart = cartOptional.get();

            if (cart.getStatus() != Cart.CartStatus.ACTIVE) {
                response.setError(true);
                response.setMessage("Cannot modify items in a non-active cart");
                return response;
            }

            Optional<Product> productOptional = productRepository.findById(productUuid);
            if (productOptional.isEmpty()) {
                response.setError(true);
                response.setMessage("Product not found");
                return response;
            }

            Product product = productOptional.get();

            CartItem cartItem = null;
            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId().equals(productUuid)) {
                    cartItem = item;
                    break;
                }
            }

            if (cartItem != null) {
                cartItem.setQuantity(modifyCartRequest.getQuantity());
                if (cartItem.getQuantity() == 0) {
                    cart.getItems().remove(cartItem);
                    response.setMessage("Item removed from cart");
                } else {
                    response.setMessage("Cart item quantity updated");
                }
            } else {
                if (modifyCartRequest.getQuantity() > 0) {
                    cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setProduct(product);
                    cartItem.setQuantity(modifyCartRequest.getQuantity());
                    cart.getItems().add(cartItem);
                    response.setMessage("Item added to cart");
                } else {
                    response.setMessage("No changes made (quantity was 0)");
                }
            }

            cartRepository.save(cart);

        } catch (Exception e) {
            LOGGER.error("Error modifying cart item", e);
            response.setError(true);
            response.setMessage("Error occurred while modifying cart item");
        }

        return response;
    }

    @Override
    @Transactional
    public BaseResponse removeItemFromCart(String cartId, String productId) {
        BaseResponse response = new BaseResponse();

        try {
            UUID cartUuid = StringUtil.safeParseUUID(cartId);
            UUID productUuid = StringUtil.safeParseUUID(productId);

            if (cartUuid == null || productUuid == null) {
                response.setError(true);
                response.setMessage("Invalid cart ID or product ID format");
                return response;
            }

            Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProductId(cartUuid, productUuid);

            if (cartItemOptional.isEmpty()) {
                response.setMessage("Item not found in cart");
            } else {
                cartItemRepository.deleteCartItem(cartItemOptional.get().getId());
                response.setMessage("Item successfully removed from cart");
            }

        } catch (Exception e) {
            LOGGER.error("Error removing cart item", e);
            response.setError(true);
            response.setMessage("Error occurred while removing cart item");
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceipt(String cartId) {
        ReceiptResponse receiptResponse = new ReceiptResponse();

        try {
            UUID cartUuid = StringUtil.safeParseUUID(cartId);

            if (cartUuid == null) {
                receiptResponse.setError(true);
                receiptResponse.setMessage("Invalid cart ID format");
                return receiptResponse;
            }

            Optional<Cart> cartOptional = cartRepository.findById(cartUuid);
            if (cartOptional.isEmpty()) {
                receiptResponse.setError(true);
                receiptResponse.setMessage("Cart not found");
                return receiptResponse;
            }

            Cart cart = cartOptional.get();

            List<ReceiptItem> receiptItems = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;

            for (CartItem cartItem : cart.getItems()) {
                Product product = cartItem.getProduct();
                int quantity = cartItem.getQuantity();
                BigDecimal price = product.getPrice();

                BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(quantity));
                subtotal = subtotal.add(itemSubtotal);

                ReceiptItem receiptItem = new ReceiptItem();
                receiptItem.setProductId(product.getId().toString());
                receiptItem.setProductName(product.getProductName());
                receiptItem.setQuantity(quantity);
                receiptItem.setPrice(price);

                List<Discount> applicableDiscounts = discountRepository.findActiveDiscountsByProductId(product.getId());

                if (!applicableDiscounts.isEmpty()) {
                    Discount bestDiscount = applicableDiscounts.get(0);

                    if (bestDiscount != null) {
                        BigDecimal discountAmount = calculateDiscountAmount(bestDiscount, quantity, price);

                        receiptItem.setDiscountName(bestDiscount.getDiscountName());
                        receiptItem.setDiscountAmount(discountAmount);
                        receiptItem.setFinalPrice(itemSubtotal.subtract(discountAmount));

                        AppliedDiscount appliedDiscount = new AppliedDiscount();
                        appliedDiscount.setDiscountName(bestDiscount.getDiscountName());
                        appliedDiscount.setDiscountAmount(discountAmount);

                        receiptResponse.getAppliedDiscounts().add(appliedDiscount);
                    } else {
                        receiptItem.setFinalPrice(itemSubtotal);
                    }
                } else {
                    receiptItem.setFinalPrice(itemSubtotal);
                }

                receiptItems.add(receiptItem);
            }

            BigDecimal totalDiscount = receiptItems.stream()
                    .map(item -> item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal finalTotal = subtotal.subtract(totalDiscount);

            receiptResponse.setCartId(cart.getId().toString());
            receiptResponse.setItems(receiptItems);
            receiptResponse.setSubtotal(subtotal);
            receiptResponse.setTotalDiscount(totalDiscount);
            receiptResponse.setFinalTotal(finalTotal);
            receiptResponse.setMessage("Receipt generated successfully");

        } catch (Exception e) {
            LOGGER.error("Error generating receipt for cart ID: {}", cartId, e);
            receiptResponse.setError(true);
            receiptResponse.setMessage("Error occurred while generating receipt");
        }

        return receiptResponse;
    }

    private BigDecimal calculateDiscountAmount(Discount discount, int quantity, BigDecimal price) {
        if (discount.getDiscountName().contains("Buy 1 Get 50% Off Second")) {
            if (quantity >= 2) {
                return price.multiply(discount.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.ZERO;
            }
        }

        else if (discount.getDiscountType() == Discount.DiscountType.PERCENTAGE) {
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
            return itemTotal.multiply(discount.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        else if (discount.getDiscountType() == Discount.DiscountType.FIXED_AMOUNT) {
            BigDecimal totalDiscount = discount.getDiscountValue().multiply(BigDecimal.valueOf(quantity));
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
            return totalDiscount.min(itemTotal);
        }

        return BigDecimal.ZERO;
    }
}
