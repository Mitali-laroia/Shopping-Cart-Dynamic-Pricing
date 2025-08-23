package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartCalculationRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.dto.CartItemResponse;
import com.example.shoppingcart.dto.CustomerRequest;
import com.example.shoppingcart.dto.ProductRequest;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateCartPricing_Electronics() {
        ProductRequest electronicsProduct = createProductRequest(1L, "Laptop", ProductCategory.ELECTRONICS, 
                new BigDecimal("1000.00"), 1);

        CustomerRequest customer = new CustomerRequest();
        customer.setLoyaltyLevel(LoyaltyLevel.BRONZE);

        CartCalculationRequest request = new CartCalculationRequest();
        request.setItems(Arrays.asList(electronicsProduct));
        request.setCustomer(customer);

        when(discountService.calculateItemSpecificDiscounts(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateBulkDiscount(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateLoyaltyDiscount(any(), eq(LoyaltyLevel.BRONZE)))
                .thenReturn(new BigDecimal("55.00")); // 5% of 1100

        CartCalculationResponse response = pricingService.calculateCartPricing(request);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("1000.00"), response.getSubtotal());
        assertEquals(new BigDecimal("100.00"), response.getTotalTax()); // 10% of 1000
        assertEquals(new BigDecimal("1100.00"), response.getTotalAfterTax());
        assertEquals(BigDecimal.ZERO, response.getItemSpecificDiscounts());
        assertEquals(BigDecimal.ZERO, response.getBulkDiscount());
        assertEquals(new BigDecimal("55.00"), response.getLoyaltyDiscount());
        assertEquals(new BigDecimal("1045.00"), response.getFinalTotal()); // 1100 - 55

        CartItemResponse item = response.getItems().get(0);
        assertEquals(ProductCategory.ELECTRONICS, item.getCategory());
        assertEquals(new BigDecimal("1000.00"), item.getSubtotal());
        assertEquals(new BigDecimal("0.10"), item.getTaxRate());
        assertEquals(new BigDecimal("100.00"), item.getTaxAmount());
        assertEquals(new BigDecimal("1100.00"), item.getTotalWithTax());
    }

    @Test
    void testCalculateCartPricing_Books() {
        ProductRequest bookProduct = createProductRequest(1L, "Java Programming", ProductCategory.BOOKS, 
                new BigDecimal("50.00"), 2);

        CustomerRequest customer = new CustomerRequest();
        customer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        CartCalculationRequest request = new CartCalculationRequest();
        request.setItems(Arrays.asList(bookProduct));
        request.setCustomer(customer);

        when(discountService.calculateItemSpecificDiscounts(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateBulkDiscount(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateLoyaltyDiscount(any(), eq(LoyaltyLevel.SILVER)))
                .thenReturn(new BigDecimal("10.00")); // 10% of 100

        CartCalculationResponse response = pricingService.calculateCartPricing(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getSubtotal()); // 50 * 2
        assertTrue(response.getTotalTax().compareTo(BigDecimal.ZERO) == 0); // Books are tax-free
        assertEquals(new BigDecimal("100.00"), response.getTotalAfterTax());
        assertEquals(new BigDecimal("10.00"), response.getLoyaltyDiscount());
        assertEquals(new BigDecimal("90.00"), response.getFinalTotal());

        CartItemResponse item = response.getItems().get(0);
        assertEquals(ProductCategory.BOOKS, item.getCategory());
        assertTrue(item.getTaxRate().compareTo(BigDecimal.ZERO) == 0);
        assertTrue(item.getTaxAmount().compareTo(BigDecimal.ZERO) == 0);
        assertEquals(new BigDecimal("100.00"), item.getTotalWithTax());
    }

    @Test
    void testCalculateCartPricing_Clothing() {
        ProductRequest clothingProduct = createProductRequest(1L, "T-shirt", ProductCategory.CLOTHING, 
                new BigDecimal("25.00"), 4);

        CustomerRequest customer = new CustomerRequest();
        customer.setLoyaltyLevel(LoyaltyLevel.GOLD);

        CartCalculationRequest request = new CartCalculationRequest();
        request.setItems(Arrays.asList(clothingProduct));
        request.setCustomer(customer);

        when(discountService.calculateItemSpecificDiscounts(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateBulkDiscount(any())).thenReturn(BigDecimal.ZERO);
        when(discountService.calculateLoyaltyDiscount(any(), eq(LoyaltyLevel.GOLD)))
                .thenReturn(new BigDecimal("15.75")); // 15% of 105

        CartCalculationResponse response = pricingService.calculateCartPricing(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getSubtotal()); // 25 * 4
        assertEquals(new BigDecimal("5.00"), response.getTotalTax()); // 5% of 100
        assertEquals(new BigDecimal("105.00"), response.getTotalAfterTax());
        assertEquals(new BigDecimal("15.75"), response.getLoyaltyDiscount());
        assertEquals(new BigDecimal("89.25"), response.getFinalTotal());

        CartItemResponse item = response.getItems().get(0);
        assertEquals(ProductCategory.CLOTHING, item.getCategory());
        assertEquals(new BigDecimal("0.05"), item.getTaxRate());
        assertEquals(new BigDecimal("5.00"), item.getTaxAmount());
        assertEquals(new BigDecimal("105.00"), item.getTotalWithTax());
    }

    @Test
    void testCalculateCartPricing_MultipleItems() {
        ProductRequest laptop = createProductRequest(1L, "Laptop", ProductCategory.ELECTRONICS, 
                new BigDecimal("1000.00"), 1);
        ProductRequest book = createProductRequest(2L, "Book", ProductCategory.BOOKS, 
                new BigDecimal("20.00"), 3);
        ProductRequest tshirt = createProductRequest(3L, "T-shirt", ProductCategory.CLOTHING, 
                new BigDecimal("25.00"), 2);

        CustomerRequest customer = new CustomerRequest();
        customer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        CartCalculationRequest request = new CartCalculationRequest();
        request.setItems(Arrays.asList(laptop, book, tshirt));
        request.setCustomer(customer);

        when(discountService.calculateItemSpecificDiscounts(any())).thenReturn(new BigDecimal("20.00"));
        when(discountService.calculateBulkDiscount(any())).thenReturn(new BigDecimal("115.25")); // 10% of 1152.50
        when(discountService.calculateLoyaltyDiscount(any(), eq(LoyaltyLevel.SILVER)))
                .thenReturn(new BigDecimal("101.73")); // 10% of 1017.25

        CartCalculationResponse response = pricingService.calculateCartPricing(request);

        assertNotNull(response);
        assertEquals(3, response.getItems().size());
        assertEquals(new BigDecimal("1110.00"), response.getSubtotal()); // 1000 + 60 + 50
        assertEquals(new BigDecimal("102.50"), response.getTotalTax()); // 100 + 0 + 2.50
        assertEquals(new BigDecimal("1212.50"), response.getTotalAfterTax());
        assertEquals(new BigDecimal("20.00"), response.getItemSpecificDiscounts());
        assertEquals(new BigDecimal("115.25"), response.getBulkDiscount());
        assertEquals(new BigDecimal("101.73"), response.getLoyaltyDiscount());
        assertEquals(new BigDecimal("975.52"), response.getFinalTotal()); // 1212.50 - 20 - 115.25 - 101.73
    }

    @Test
    void testCalculateItemTax_Electronics() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal tax = pricingService.calculateItemTax(amount, ProductCategory.ELECTRONICS);
        
        assertEquals(new BigDecimal("10.00"), tax);
    }

    @Test
    void testCalculateItemTax_Books() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal tax = pricingService.calculateItemTax(amount, ProductCategory.BOOKS);
        
        assertTrue(tax.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void testCalculateItemTax_Clothing() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal tax = pricingService.calculateItemTax(amount, ProductCategory.CLOTHING);
        
        assertEquals(new BigDecimal("5.00"), tax);
    }

    @Test
    void testCalculateTotalWithTax_Electronics() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal totalWithTax = pricingService.calculateTotalWithTax(amount, ProductCategory.ELECTRONICS);
        
        assertEquals(new BigDecimal("110.00"), totalWithTax);
    }

    @Test
    void testCalculateTotalWithTax_Books() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal totalWithTax = pricingService.calculateTotalWithTax(amount, ProductCategory.BOOKS);
        
        assertEquals(new BigDecimal("100.00"), totalWithTax);
    }

    @Test
    void testCalculateTotalWithTax_Clothing() {
        BigDecimal amount = new BigDecimal("100.00");
        
        BigDecimal totalWithTax = pricingService.calculateTotalWithTax(amount, ProductCategory.CLOTHING);
        
        assertEquals(new BigDecimal("105.00"), totalWithTax);
    }

    private ProductRequest createProductRequest(Long id, String name, ProductCategory category, 
            BigDecimal price, Integer quantity) {
        ProductRequest product = new ProductRequest();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setQuantity(quantity);
        return product;
    }
}