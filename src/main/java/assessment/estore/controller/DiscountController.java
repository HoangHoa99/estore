package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PostMapping
    ResponseEntity<?> createDiscount(@Valid @RequestBody CreateDiscountRequest createDiscountRequest) {
        BaseResponse baseResponse = discountService.createDiscount(createDiscountRequest);
        if (baseResponse.getError()) {
            return ResponseEntity.badRequest().body(baseResponse);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(baseResponse);
    }

    @GetMapping
    ResponseEntity<?> getDiscounts() {
        GetDiscountsResponse response = discountService.getDiscounts();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{discountId}")
    ResponseEntity<?> deleteDiscount(@PathVariable String discountId) {
        boolean deleted  = discountService.deleteDiscount(discountId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
