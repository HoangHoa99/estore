package assessment.estore.controller;

import assessment.estore.model.dao.Discount;
import assessment.estore.model.dto.request.CreateDiscountRequest;
import assessment.estore.model.dto.response.BaseResponse;
import assessment.estore.model.dto.response.DiscountDetail;
import assessment.estore.model.dto.response.GetDiscountsResponse;
import assessment.estore.service.DiscountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DiscountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    private ObjectMapper objectMapper;
    private CreateDiscountRequest createDiscountRequest;
    private BaseResponse baseResponse;
    private GetDiscountsResponse getDiscountsResponse;
    private String discountId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(discountController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        discountId = UUID.randomUUID().toString();

        createDiscountRequest = new CreateDiscountRequest();
        createDiscountRequest.setDiscountName("Summer Sale");
        createDiscountRequest.setDiscountType(Discount.DiscountType.PERCENTAGE);
        createDiscountRequest.setDiscountValue(new BigDecimal("20.00"));
        createDiscountRequest.setProductId(UUID.randomUUID().toString());
        createDiscountRequest.setStartDate(LocalDateTime.now());
        createDiscountRequest.setEndDate(LocalDateTime.now().plusDays(30));

        baseResponse = new BaseResponse();
        baseResponse.setMessage("Discount created successfully");
        baseResponse.setError(false);

        DiscountDetail discountDetail = new DiscountDetail();
        discountDetail.setDiscountId(discountId);
        discountDetail.setDiscountName("Summer Sale");
        discountDetail.setDiscountType("PERCENTAGE");
        discountDetail.setDiscountValue(new BigDecimal("20.00"));
        discountDetail.setActive(true);
        discountDetail.setProductId(UUID.randomUUID().toString());

        getDiscountsResponse = new GetDiscountsResponse();
        getDiscountsResponse.setDiscounts(Collections.singletonList(discountDetail));
    }

    @Test
    void createDiscount_Success() throws Exception {
        when(discountService.createDiscount(any(CreateDiscountRequest.class))).thenReturn(baseResponse);

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDiscountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Discount created successfully"))
                .andExpect(jsonPath("$.error").value(false));
    }

    @Test
    void createDiscount_ValidationError() throws Exception {
        BaseResponse errorResponse = new BaseResponse();
        errorResponse.setMessage("Percentage discount must be between 0 and 100");
        errorResponse.setError(true);

        when(discountService.createDiscount(any(CreateDiscountRequest.class))).thenReturn(errorResponse);

        mockMvc.perform(post("/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDiscountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Percentage discount must be between 0 and 100"))
                .andExpect(jsonPath("$.error").value(true));
    }

    @Test
    void getDiscounts_Success() throws Exception {
        when(discountService.getDiscounts()).thenReturn(getDiscountsResponse);

        mockMvc.perform(get("/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discounts").isArray())
                .andExpect(jsonPath("$.discounts[0].discountId").value(discountId))
                .andExpect(jsonPath("$.discounts[0].discountName").value("Summer Sale"))
                .andExpect(jsonPath("$.discounts[0].discountType").value("PERCENTAGE"))
                .andExpect(jsonPath("$.discounts[0].discountValue").value(20.00))
                .andExpect(jsonPath("$.discounts[0].active").value(true));
    }

    @Test
    void deleteDiscount_Success() throws Exception {
        when(discountService.deleteDiscount(eq(discountId))).thenReturn(true);

        mockMvc.perform(delete("/discounts/{discountId}", discountId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDiscount_NotFound() throws Exception {
        when(discountService.deleteDiscount(eq(discountId))).thenReturn(false);

        mockMvc.perform(delete("/discounts/{discountId}", discountId))
                .andExpect(status().isNotFound());
    }
}