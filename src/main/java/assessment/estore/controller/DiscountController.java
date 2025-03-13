package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("discount")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    ResponseEntity<?> createDiscount(@Valid @RequestBody CreateDiscountRequest createDiscountRequest) {
        BaseResponse baseResponse = discountService.createDiscount(createDiscountRequest);

        return ResponseEntity.ok(baseResponse);
    }

    @GetMapping
    ResponseEntity<?> getAllDiscounts() {
        GetDiscountsResponse response = discountService.getDiscounts();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{discountId}")
    ResponseEntity<?> deleteDiscount(@PathVariable String discountId) {
        Boolean response = discountService.deleteDiscount(discountId);
        return ResponseEntity.ok(response);
    }
}
