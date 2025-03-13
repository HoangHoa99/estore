package assessment.estore.service;

import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetDiscountsResponse;

public interface DiscountService {
    BaseResponse createDiscount(CreateDiscountRequest createDiscountRequest);
    Boolean deleteDiscount(String discountId);
    GetDiscountsResponse getDiscounts();
}
