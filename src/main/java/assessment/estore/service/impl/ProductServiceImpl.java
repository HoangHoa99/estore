package assessment.estore.service.impl;

import assessment.estore.model.dao.Product;
import assessment.estore.model.dto.request.CreateProductRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.GetProductsResponse;
import assessment.estore.model.dto.response.ProductDetailResponse;
import assessment.estore.repository.ProductRepository;
import assessment.estore.service.ProductService;
import assessment.estore.util.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public BaseResponse createProduct(CreateProductRequest createProductRequest) {
        BaseResponse baseResponse = new BaseResponse();

        try {
            Product product = new Product();
            product.setProductName(createProductRequest.getProductName());
            product.setDescription(createProductRequest.getDescription());
            product.setPrice(createProductRequest.getPrice());
            product.setStockQuantity(createProductRequest.getStockQuantity());

            productRepository.save(product);

            if(Strings.isNotBlank(product.getId().toString())) {
                baseResponse.setMessage("Product created successfully");
                return baseResponse;
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occur while create new product {}", e.getMessage());
        }

        baseResponse.setMessage("Product creation failed");
        baseResponse.setError(true);
        return baseResponse;
    }

    @Override
    @Transactional
    public Boolean deleteProduct(String productId) {
        try {
            UUID uuid = StringUtil.safeParseUUID(productId);
            if (uuid == null) {
                return false;
            }

            lock.writeLock().lock();
            try {
                productRepository.deleteById(uuid);
                return true;
            } finally {
                lock.writeLock().unlock();
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occur while deleting product for {} {}", productId, e.getMessage());
            return false;
        }
    }

    @Override
    public ProductDetailResponse getProduct(String productId) {
        UUID uuid = StringUtil.safeParseUUID(productId);
        if (uuid == null) {
            return null;
        }

        lock.readLock().lock();
        try {
            Product product = productRepository.findById(uuid).orElse(null);
            if(product == null) {
                return null;
            }
            return buildDetailItem(product);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public GetProductsResponse getProducts(int page, int size) {
        GetProductsResponse response = new GetProductsResponse();

        lock.readLock().lock();
        try {
            List<Product> products = productRepository.findAll();

            if(products.isEmpty()) {
                response.setProducts(new ArrayList<>());
                return response;
            }

            List<ProductDetailResponse> productResponses = new ArrayList<>();
            for(Product product : products) {
                productResponses.add(buildDetailItem(product));
            }
            response.setProducts(productResponses);

            return response;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ProductDetailResponse buildDetailItem(Product product) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setProductId(product.getId().toString());
        response.setProductName(product.getProductName());
        response.setProductDescription(product.getDescription());
        response.setProductPrice(product.getPrice());
        response.setProductQuantity(product.getStockQuantity());
        return response;
    }
}