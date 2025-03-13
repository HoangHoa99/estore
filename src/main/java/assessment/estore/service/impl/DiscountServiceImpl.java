package assessment.estore.service.impl;

import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.service.DiscountService;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Override
    public BaseResponse createDiscount(CreateDiscountRequest createDiscountRequest) {
        return null;
    }

    @Override
    public Boolean deleteDiscount(String discountId) {
        return null;
    }

    @Override
    public GetDiscountsResponse getDiscounts() {
        return null;
    }
}
