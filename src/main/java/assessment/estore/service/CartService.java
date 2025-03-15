package assessment.estore.service;

import assessment.estore.model.dao.Cart;
import assessment.estore.model.dto.request.CreateCartRequest;
import assessment.estore.model.dto.request.ModifyCartRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.CreateCartResponse;
import assessment.estore.model.dto.response.GetCartResponse;
import assessment.estore.model.dto.response.ReceiptResponse;

public interface CartService {
    CreateCartResponse createCart(CreateCartRequest createCartRequest);
    GetCartResponse getCart(String cartId);
    BaseResponse addItemToCart(String cartId, ModifyCartRequest modifyCartRequest);
    BaseResponse removeItemFromCart(String cartId, String productId);
    ReceiptResponse getReceipt(String cartId);
}
