package assessment.estore.model.dto.response;


import java.util.List;

public class GetProductsResponse {
    private List<ProductDetailResponse> products;

    public List<ProductDetailResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDetailResponse> products) {
        this.products = products;
    }
}
