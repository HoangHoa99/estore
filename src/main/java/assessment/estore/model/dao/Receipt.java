package assessment.estore.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    private UUID cartId;
    private UUID userId;
    private LocalDateTime generatedAt;
    private List<ReceiptItem> items = new ArrayList<>();
    private List<AppliedDiscount> appliedDiscounts = new ArrayList<>();
    private BigDecimal subtotal;
    private BigDecimal discountTotal;
    private BigDecimal total;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReceiptItem {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppliedDiscount {
        private UUID discountId;
        private String discountName;
        private String discountType;
        private UUID productId;
        private String productName;
        private BigDecimal amountSaved;
    }
}
