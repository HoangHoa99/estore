package assessment.estore.service.impl;

import assessment.estore.model.dao.Discount;
import assessment.estore.model.dao.Product;
import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.DiscountDetail;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.repository.DiscountRepository;
import assessment.estore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private UUID validDiscountId;
    private UUID validProductId;
    private Discount testDiscount;
    private Product testProduct;
    private CreateDiscountRequest createDiscountRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        validDiscountId = UUID.randomUUID();
        validProductId = UUID.randomUUID();
        now = LocalDateTime.now();

        testProduct = new Product();
        testProduct.setId(validProductId);
        testProduct.setProductName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(10);

        testDiscount = new Discount();
        testDiscount.setId(validDiscountId);
        testDiscount.setDiscountName("Test Discount");
        testDiscount.setDiscountType(Discount.DiscountType.PERCENTAGE);
        testDiscount.setDiscountValue(new BigDecimal("10.00"));
        testDiscount.setProduct(testProduct);

        createDiscountRequest = new CreateDiscountRequest();
        createDiscountRequest.setDiscountName("New Discount");
        createDiscountRequest.setDiscountType(Discount.DiscountType.PERCENTAGE);
        createDiscountRequest.setDiscountValue(new BigDecimal("20.00"));
        createDiscountRequest.setProductId(validProductId.toString());
        createDiscountRequest.setStartDate(now);
        createDiscountRequest.setEndDate(now.plusDays(30));
    }

    @Test
    void createDiscount_Success() {
        when(productRepository.findById(validProductId)).thenReturn(Optional.of(testProduct));
        when(discountRepository.save(any(Discount.class))).thenAnswer(invocation -> {
            Discount savedDiscount = invocation.getArgument(0);
            savedDiscount.setId(UUID.randomUUID());
            return savedDiscount;
        });

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertFalse(response.getError());
        assertEquals("Discount created successfully", response.getMessage());
        verify(productRepository, times(1)).findById(validProductId);
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    void createDiscount_InvalidPercentage_TooHigh() {
        createDiscountRequest.setDiscountValue(new BigDecimal("101.00"));

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertTrue(response.getError());
        assertEquals("Percentage discount must be between 0 and 100", response.getMessage());
        verify(productRepository, never()).findById(any());
        verify(discountRepository, never()).save(any());
    }

    @Test
    void createDiscount_InvalidPercentage_Negative() {
        createDiscountRequest.setDiscountValue(new BigDecimal("-10.00"));

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertTrue(response.getError());
        assertEquals("Percentage discount must be between 0 and 100", response.getMessage());
        verify(productRepository, never()).findById(any());
        verify(discountRepository, never()).save(any());
    }

    @Test
    void createDiscount_FixedAmountDiscount_NoPercentageValidation() {
        createDiscountRequest.setDiscountType(Discount.DiscountType.FIXED_AMOUNT);
        createDiscountRequest.setDiscountValue(new BigDecimal("200.00")); // Over 100, but should be fine for FIXED_AMOUNT

        when(productRepository.findById(validProductId)).thenReturn(Optional.of(testProduct));
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertFalse(response.getError());
        assertEquals("Discount created successfully", response.getMessage());
    }

    @Test
    void createDiscount_InvalidDateRange() {
        createDiscountRequest.setStartDate(now.plusDays(10));
        createDiscountRequest.setEndDate(now); // End date before start date

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertTrue(response.getError());
        assertEquals("End date must be after start date", response.getMessage());
        verify(productRepository, never()).findById(any());
        verify(discountRepository, never()).save(any());
    }

    @Test
    void createDiscount_ProductNotFound() {
        when(productRepository.findById(validProductId)).thenReturn(Optional.empty());
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertFalse(response.getError()); // Should still succeed, just without product association
        assertEquals("Discount created successfully", response.getMessage());
        verify(productRepository, times(1)).findById(validProductId);
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    void createDiscount_NoProductId() {
        createDiscountRequest.setProductId(null);
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertFalse(response.getError());
        assertEquals("Discount created successfully", response.getMessage());
        verify(productRepository, never()).findById(any());
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    void createDiscount_RepositoryException() {
        when(productRepository.findById(validProductId)).thenReturn(Optional.of(testProduct));
        when(discountRepository.save(any(Discount.class))).thenThrow(new RuntimeException("DB Error"));

        BaseResponse response = discountService.createDiscount(createDiscountRequest);

        assertTrue(response.getError());
        assertEquals("Discount creation failed", response.getMessage());
        verify(productRepository, times(1)).findById(validProductId);
        verify(discountRepository, times(1)).save(any(Discount.class));
    }

    @Test
    void deleteDiscount_Success() {
        when(discountRepository.deleteDiscount(validDiscountId)).thenReturn(1);

        Boolean result = discountService.deleteDiscount(validDiscountId.toString());

        assertTrue(result);
        verify(discountRepository, times(1)).deleteDiscount(validDiscountId);
    }

    @Test
    void deleteDiscount_NotFound() {
        when(discountRepository.deleteDiscount(validDiscountId)).thenReturn(0);

        Boolean result = discountService.deleteDiscount(validDiscountId.toString());

        assertFalse(result);
        verify(discountRepository, times(1)).deleteDiscount(validDiscountId);
    }

    @Test
    void deleteDiscount_InvalidUUID() {
        Boolean result = discountService.deleteDiscount("invalid-uuid");

        assertFalse(result);
        verify(discountRepository, never()).deleteDiscount(any());
    }

    @Test
    void deleteDiscount_RepositoryException() {
        when(discountRepository.deleteDiscount(validDiscountId)).thenThrow(new RuntimeException("DB Error"));

        Boolean result = discountService.deleteDiscount(validDiscountId.toString());

        assertFalse(result);
        verify(discountRepository, times(1)).deleteDiscount(validDiscountId);
    }

    @Test
    void getDiscounts_Success() {
        LocalDateTime now = LocalDateTime.now();

        Discount discount1 = new Discount();
        discount1.setId(UUID.randomUUID());
        discount1.setDiscountName("Discount 1");
        discount1.setDiscountType(Discount.DiscountType.PERCENTAGE);
        discount1.setDiscountValue(new BigDecimal("10.00"));
        discount1.setStartDate(now.minusDays(1));
        discount1.setEndDate(now.plusDays(1));

        Discount discount2 = new Discount();
        discount2.setId(UUID.randomUUID());
        discount2.setDiscountName("Discount 2");
        discount2.setDiscountType(Discount.DiscountType.FIXED_AMOUNT);
        discount2.setDiscountValue(new BigDecimal("20.00"));
        discount2.setStartDate(now.plusDays(1));
        discount2.setEndDate(now.plusDays(2));
        discount2.setProduct(testProduct);

        when(discountRepository.findAll()).thenReturn(Arrays.asList(discount1, discount2));

        GetDiscountsResponse response = discountService.getDiscounts();

        assertNotNull(response);
        assertEquals(2, response.getDiscounts().size());

        DiscountDetail detail1 = response.getDiscounts().get(0);
        assertEquals(discount1.getId().toString(), detail1.getDiscountId());
        assertEquals(discount1.getDiscountName(), detail1.getDiscountName());
        assertEquals(discount1.getDiscountType().toString(), detail1.getDiscountType());
        assertEquals(discount1.getDiscountValue(), detail1.getDiscountValue());
        assertEquals(discount1.isActive(), detail1.isActive());
        assertNull(detail1.getProductId());

        DiscountDetail detail2 = response.getDiscounts().get(1);
        assertEquals(discount2.getId().toString(), detail2.getDiscountId());
        assertEquals(testProduct.getId().toString(), detail2.getProductId());

        verify(discountRepository, times(1)).findAll();
    }

    @Test
    void getDiscounts_EmptyList() {
        when(discountRepository.findAll()).thenReturn(Collections.emptyList());

        GetDiscountsResponse response = discountService.getDiscounts();

        assertNotNull(response);
        assertTrue(response.getDiscounts().isEmpty());
        verify(discountRepository, times(1)).findAll();
    }

    @Test
    void getDiscounts_RepositoryException() {
        when(discountRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        GetDiscountsResponse response = discountService.getDiscounts();

        assertNotNull(response);
        assertTrue(response.getDiscounts().isEmpty());
        verify(discountRepository, times(1)).findAll();
    }
}