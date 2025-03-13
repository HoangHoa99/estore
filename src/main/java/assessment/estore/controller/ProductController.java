package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;
import assessment.estore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        BaseResponse res = productService.createProduct(createProductRequest);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    ResponseEntity<?> getProducts(@RequestParam("page") int page, @RequestParam("size") int size) {
        GetProductsResponse response = productService.getProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    ResponseEntity<?> getProductDetail(@PathVariable("productId") String productId) {
        ProductDetailResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<?> deleteProduct(@PathVariable("productId") String productId) {
        Boolean response = productService.deleteProduct(productId);
        return ResponseEntity.ok(response);
    }
}
