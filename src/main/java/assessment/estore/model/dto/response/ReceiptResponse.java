package assessment.estore.model.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceiptResponse extends BaseResponse {
    private String cartId;
    private List<ReceiptItem> items = new ArrayList<>();
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal finalTotal;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public void setItems(List<ReceiptItem> items) {
        this.items = items;
    }

    public List<AppliedDiscount> getAppliedDiscounts() {
        return appliedDiscounts;
    }

    public void setAppliedDiscounts(List<AppliedDiscount> appliedDiscounts) {
        this.appliedDiscounts = appliedDiscounts;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }
}
