package assessment.estore.model.dto.request;

import assessment.estore.model.dao.Discount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateDiscountRequest {
    private String discountName;
    private String discountDescription;
    private Discount.DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
