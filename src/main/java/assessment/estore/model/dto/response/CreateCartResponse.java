package assessment.estore.model.dto.response;

public class CreateCartResponse extends BaseResponse {
    public String cartId;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
