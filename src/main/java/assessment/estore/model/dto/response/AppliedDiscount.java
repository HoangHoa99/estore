package assessment.estore.model.dto.response;

import java.math.BigDecimal;

public class AppliedDiscount {
    private String discountName;
    private BigDecimal discountAmount;

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
