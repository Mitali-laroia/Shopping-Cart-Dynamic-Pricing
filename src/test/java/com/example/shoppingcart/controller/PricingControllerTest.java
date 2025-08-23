package com.example.shoppingcart.controller;

import com.example.shoppingcart.controller.config.TestConfiguration;
import com.example.shoppingcart.dto.CartCalculationRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.dto.CustomerRequest;
import com.example.shoppingcart.dto.ProductRequest;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import com.example.shoppingcart.service.PricingService;
import com.example.shoppingcart.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PricingController.class)
@ContextConfiguration(classes = {TestConfiguration.class, PricingController.class})
class PricingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PricingService pricingService;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartCalculationRequest sampleRequest;
    private CartCalculationResponse sampleResponse;
    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setLoyaltyLevel(LoyaltyLevel.SILVER);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setId(1L);
        productRequest.setName("Test Product");
        productRequest.setCategory(ProductCategory.ELECTRONICS);
        productRequest.setPrice(BigDecimal.valueOf(100.00));
        productRequest.setQuantity(1);

        sampleRequest = new CartCalculationRequest();
        sampleRequest.setItems(Arrays.asList(productRequest));
        sampleRequest.setCustomer(customerRequest);

        sampleResponse = new CartCalculationResponse();
        sampleResponse.setSubtotal(BigDecimal.valueOf(100.00));
        sampleResponse.setTotalTax(BigDecimal.valueOf(5.00));
        sampleResponse.setLoyaltyDiscount(BigDecimal.valueOf(10.00));
        sampleResponse.setFinalTotal(BigDecimal.valueOf(95.00));
    }

    @Test
    void calculateCartPricing_ValidRequest_ShouldReturnCalculationResponse() throws Exception {
        when(pricingService.calculateCartPricing(any(CartCalculationRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subtotal").value(100.00))
                .andExpect(jsonPath("$.totalTax").value(5.00))
                .andExpect(jsonPath("$.loyaltyDiscount").value(10.00))
                .andExpect(jsonPath("$.finalTotal").value(95.00));

        verify(pricingService, times(1)).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_NullItems_ShouldReturnBadRequest() throws Exception {
        sampleRequest.setItems(null);

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());

        verify(pricingService, never()).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_EmptyItems_ShouldReturnBadRequest() throws Exception {
        sampleRequest.setItems(new ArrayList<>());

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());

        verify(pricingService, never()).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_NullCustomer_ShouldReturnBadRequest() throws Exception {
        sampleRequest.setCustomer(null);

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());

        verify(pricingService, never()).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_CustomerWithoutLoyaltyLevel_ShouldReturnBadRequest() throws Exception {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setLoyaltyLevel(null);
        sampleRequest.setCustomer(customerRequest);

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());

        verify(pricingService, never()).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_InvalidArgumentException_ShouldReturnBadRequest() throws Exception {
        when(pricingService.calculateCartPricing(any(CartCalculationRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid request data"));

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isBadRequest());

        verify(pricingService, times(1)).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricing_UnexpectedException_ShouldReturnInternalServerError() throws Exception {
        when(pricingService.calculateCartPricing(any(CartCalculationRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isInternalServerError());

        verify(pricingService, times(1)).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void calculateCartPricingById_ExistingCart_ShouldReturnCalculationResponse() throws Exception {
        when(shoppingCartService.calculateCartPricing(1L)).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/pricing/cart/1/calculate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subtotal").value(100.00))
                .andExpect(jsonPath("$.totalTax").value(5.00))
                .andExpect(jsonPath("$.loyaltyDiscount").value(10.00))
                .andExpect(jsonPath("$.finalTotal").value(95.00));

        verify(shoppingCartService, times(1)).calculateCartPricing(1L);
    }

    @Test
    void calculateCartPricingById_NonExistingCart_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.calculateCartPricing(999L))
                .thenThrow(new RuntimeException("Cart not found"));

        mockMvc.perform(post("/api/v1/pricing/cart/999/calculate"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).calculateCartPricing(999L);
    }

    @Test
    void calculateCartPricingById_ServiceError_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.calculateCartPricing(1L))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(post("/api/v1/pricing/cart/1/calculate"))
                .andExpect(status().isInternalServerError());

        verify(shoppingCartService, times(1)).calculateCartPricing(1L);
    }

    @Test
    void handleException_ShouldReturnInternalServerError() throws Exception {
        when(pricingService.calculateCartPricing(any(CartCalculationRequest.class)))
                .thenThrow(new OutOfMemoryError("System error"));

        mockMvc.perform(post("/api/v1/pricing/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));

        verify(pricingService, times(1)).calculateCartPricing(any(CartCalculationRequest.class));
    }
}