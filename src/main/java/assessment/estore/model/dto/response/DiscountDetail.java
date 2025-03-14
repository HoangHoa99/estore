package assessment.estore.model.dto.response;

import java.math.BigDecimal;

public class DiscountDetail {
    private String discountId;
    private String discountName;
    private BigDecimal discountValue;
    private String discountType;
    private Boolean isActive;
    private String productId;

    public DiscountDetail(String discountId, String discountName, BigDecimal discountValue, String discountType, Boolean isActive, String productId) {
        this.discountId = discountId;
        this.discountName = discountName;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.isActive = isActive;
        this.productId = productId;
    }

    public DiscountDetail() {
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }
}
