package assessment.estore.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {
    @NotEmpty
    private String productId;
    @NotNull
    @Min(0)
    private Integer quantity;
}
