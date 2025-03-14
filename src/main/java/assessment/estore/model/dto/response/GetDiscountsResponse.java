package assessment.estore.model.dto.response;

import java.util.List;

public class GetDiscountsResponse {
    private List<DiscountDetail> discounts;

    public GetDiscountsResponse(List<DiscountDetail> discounts) {
        this.discounts = discounts;
    }

    public GetDiscountsResponse() {
    }

    public List<DiscountDetail> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountDetail> discounts) {
        this.discounts = discounts;
    }
}
