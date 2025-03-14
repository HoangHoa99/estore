package assessment.estore.service.impl;

import assessment.estore.model.dao.Discount;
import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.DiscountDetail;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.repository.DiscountRepository;
import assessment.estore.repository.ProductRepository;
import assessment.estore.service.DiscountService;
import assessment.estore.util.StringUtil;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class DiscountServiceImpl implements DiscountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscountServiceImpl.class);

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public DiscountServiceImpl(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }
    
    @Override
    @Transactional
    public BaseResponse createDiscount(CreateDiscountRequest createDiscountRequest) {
        BaseResponse baseResponse = new BaseResponse();

        if (createDiscountRequest.getDiscountType() == Discount.DiscountType.PERCENTAGE) {
            BigDecimal value = createDiscountRequest.getDiscountValue();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(new BigDecimal("100")) > 0) {
                baseResponse.setMessage("Percentage discount must be between 0 and 100");
                baseResponse.setError(true);
                return baseResponse;
            }
        }

        if (createDiscountRequest.getEndDate().isBefore(createDiscountRequest.getStartDate())) {
            baseResponse.setMessage("End date must be after start date");
            baseResponse.setError(true);
            return baseResponse;
        }
        
        try {
            Discount discount = new Discount();
            discount.setDiscountName(createDiscountRequest.getDiscountName());
            discount.setDiscountType(createDiscountRequest.getDiscountType());
            discount.setDiscountValue(createDiscountRequest.getDiscountValue());

            if (Strings.isNotBlank(createDiscountRequest.getProductId())) {
                UUID productId = StringUtil.safeParseUUID(createDiscountRequest.getProductId());
                if (productId != null) {
                    productRepository.findById(productId).ifPresent(discount::setProduct);
                }

            }

            discountRepository.save(discount);

            baseResponse.setMessage("Discount created successfully");
            return baseResponse;
        }
        catch (Exception e) {
            LOGGER.error("Error occur while create new discount {}", e.getMessage());
        }

        baseResponse.setMessage("Discount creation failed");
        baseResponse.setError(true);
        return baseResponse;
    }

    @Override
    @Transactional
    public Boolean deleteDiscount(String discountId) {
        try {
            UUID uuid = StringUtil.safeParseUUID(discountId);
            if (uuid == null) {
                return false;
            }

            lock.writeLock().lock();
            try {
                int deletedCount = discountRepository.deleteDiscount(uuid);
                return deletedCount > 0;
            } finally {
                lock.writeLock().unlock();
            }
        }
        catch (Exception e) {
            LOGGER.error("Error occur while deleting discount for {} {}", discountId, e.getMessage());
            return false;
        }
    }

    @Override
    public GetDiscountsResponse getDiscounts() {
        GetDiscountsResponse response = new GetDiscountsResponse();

        lock.readLock().lock();
        try {
            List<Discount> discounts = discountRepository.findAll();

            if(discounts.isEmpty()) {
                response.setDiscounts(new ArrayList<>());
                return response;
            }

            List<DiscountDetail> discountDetails = getDiscountDetails(discounts);
            response.setDiscounts(discountDetails);

            return response;
        }
        catch (Exception e) {
            LOGGER.error("Error occur while getting discounts {}", e.getMessage());
            response.setDiscounts(new ArrayList<>());
            return response;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    private static List<DiscountDetail> getDiscountDetails(List<Discount> discounts) {
        List<DiscountDetail> discountDetails = new ArrayList<>();
        for (Discount discount : discounts) {
            DiscountDetail discountDetail = new DiscountDetail();
            discountDetail.setDiscountId(discount.getId().toString());
            discountDetail.setDiscountName(discount.getDiscountName());
            discountDetail.setDiscountType(discount.getDiscountType().toString());
            discountDetail.setDiscountValue(discount.getDiscountValue());
            discountDetail.setActive(discount.isActive());
            if (discount.getProduct() != null) {
                discountDetail.setProductId(discount.getProduct().getId().toString());
            }

            discountDetails.add(discountDetail);
        }
        return discountDetails;
    }
}
