package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateCartRequest;
import assessment.estore.model.dto.request.ModifyCartRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.CreateCartResponse;
import assessment.estore.model.dto.response.GetCartResponse;
import assessment.estore.model.dto.response.ReceiptResponse;
import assessment.estore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CreateCartResponse> createCart(@Valid @RequestBody CreateCartRequest createCartRequest) {
        CreateCartResponse response = cartService.createCart(createCartRequest);

        if (response.getError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<GetCartResponse> getCart(@PathVariable String cartId) {
        GetCartResponse response = cartService.getCart(cartId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<BaseResponse> addCartItem(
            @PathVariable String cartId,
            @Valid @RequestBody ModifyCartRequest modifyCartRequest) {
        BaseResponse response = cartService.addItemToCart(cartId, modifyCartRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<BaseResponse> removeCartItem(@PathVariable String cartId, @PathVariable String productId) {
        BaseResponse response = cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cartId}/receipt")
    public ResponseEntity<ReceiptResponse> generateReceipt(@PathVariable String cartId) {
        ReceiptResponse receipt = cartService.getReceipt(cartId);
        return ResponseEntity.ok(receipt);
    }
}
