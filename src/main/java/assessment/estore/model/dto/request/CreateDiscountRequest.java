package assessment.estore.model.dto.request;

import assessment.estore.model.dao.Discount;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateDiscountRequest {
    @NotEmpty
    private String discountName;
    private String discountDescription;
    @NotNull
    private Discount.DiscountType discountType;
    @NotNull
    @Positive
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String productId;

    public CreateDiscountRequest(String discountName, String discountDescription, Discount.DiscountType discountType, BigDecimal discountValue, LocalDateTime startDate, LocalDateTime endDate, String productId) {
        this.discountName = discountName;
        this.discountDescription = discountDescription;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productId = productId;
    }

    public CreateDiscountRequest() {
    }

    public @NotNull String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(@NotNull String discountName) {
        this.discountName = discountName;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    public @NotNull Discount.DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(@NotNull Discount.DiscountType discountType) {
        this.discountType = discountType;
    }

    public @NotNull @Positive BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(@NotNull @Positive BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
