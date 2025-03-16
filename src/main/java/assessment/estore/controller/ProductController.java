package assessment.estore.controller;

import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;
import assessment.estore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    ResponseEntity<BaseResponse> createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        BaseResponse res = productService.createProduct(createProductRequest);

        if (res.getError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    ResponseEntity<GetProductsResponse> getProducts(@RequestParam("page") int page, @RequestParam("size") int size) {
        GetProductsResponse response = productService.getProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable("productId") String productId) {
        ProductDetailResponse response = productService.getProduct(productId);

        if (response.getError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    ResponseEntity<BaseResponse> deleteProduct(@PathVariable("productId") String productId) {
        Boolean response = productService.deleteProduct(productId);

        if (!response) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse("Failed to delete product", true));
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new BaseResponse("Product deleted successfully"));
    }
}
