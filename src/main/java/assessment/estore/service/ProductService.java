package assessment.estore.service;

import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;

public interface ProductService {
    BaseResponse createProduct(CreateProductRequest createProductRequest);
    Boolean deleteProduct(String productId);
    ProductDetailResponse getProduct(String productId);
    GetProductsResponse getProducts(int page, int size);
}
