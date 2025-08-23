package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartItemResponse;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscountServiceTest {

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateItemSpecificDiscounts_WithElectronicsDiscount() {
        CartItemResponse electronicsItem = createCartItemResponse(1L, "Laptop", ProductCategory.ELECTRONICS, 
                3, new BigDecimal("1000.00"), new BigDecimal("3000.00"), new BigDecimal("3300.00"));

        CartItemResponse bookItem = createCartItemResponse(2L, "Book", ProductCategory.BOOKS, 
                2, new BigDecimal("20.00"), new BigDecimal("40.00"), new BigDecimal("40.00"));

        List<CartItemResponse> items = Arrays.asList(electronicsItem, bookItem);

        BigDecimal totalDiscounts = discountService.calculateItemSpecificDiscounts(items);

        assertEquals(new BigDecimal("495.00"), totalDiscounts); // 15% of 3300.00
        assertEquals(new BigDecimal("495.00"), electronicsItem.getItemSpecificDiscount());
        assertEquals(BigDecimal.ZERO, bookItem.getItemSpecificDiscount());
        assertEquals(new BigDecimal("2805.00"), electronicsItem.getTotalAfterItemDiscount());
        assertEquals(new BigDecimal("40.00"), bookItem.getTotalAfterItemDiscount());
    }

    @Test
    void testCalculateItemSpecificDiscounts_NoElectronicsDiscount() {
        CartItemResponse electronicsItem = createCartItemResponse(1L, "Smartphone", ProductCategory.ELECTRONICS, 
                2, new BigDecimal("800.00"), new BigDecimal("1600.00"), new BigDecimal("1760.00"));

        CartItemResponse clothingItem = createCartItemResponse(2L, "T-shirt", ProductCategory.CLOTHING, 
                5, new BigDecimal("25.00"), new BigDecimal("125.00"), new BigDecimal("131.25"));

        List<CartItemResponse> items = Arrays.asList(electronicsItem, clothingItem);

        BigDecimal totalDiscounts = discountService.calculateItemSpecificDiscounts(items);

        assertTrue(totalDiscounts.compareTo(BigDecimal.ZERO) == 0);
        assertEquals(BigDecimal.ZERO, electronicsItem.getItemSpecificDiscount());
        assertEquals(BigDecimal.ZERO, clothingItem.getItemSpecificDiscount());
    }

    @Test
    void testCalculateBulkDiscount_AboveThreshold() {
        BigDecimal cartTotal = new BigDecimal("250.00");

        BigDecimal bulkDiscount = discountService.calculateBulkDiscount(cartTotal);

        assertEquals(new BigDecimal("25.00"), bulkDiscount); // 10% of 250.00
    }

    @Test
    void testCalculateBulkDiscount_BelowThreshold() {
        BigDecimal cartTotal = new BigDecimal("150.00");

        BigDecimal bulkDiscount = discountService.calculateBulkDiscount(cartTotal);

        assertEquals(BigDecimal.ZERO, bulkDiscount);
    }

    @Test
    void testCalculateBulkDiscount_AtThreshold() {
        BigDecimal cartTotal = new BigDecimal("200.00");

        BigDecimal bulkDiscount = discountService.calculateBulkDiscount(cartTotal);

        assertEquals(BigDecimal.ZERO, bulkDiscount); // Must be > 200, not >= 200
    }

    @Test
    void testCalculateLoyaltyDiscount_Bronze() {
        BigDecimal cartTotal = new BigDecimal("100.00");

        BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(cartTotal, LoyaltyLevel.BRONZE);

        assertEquals(new BigDecimal("5.00"), loyaltyDiscount); // 5% of 100.00
    }

    @Test
    void testCalculateLoyaltyDiscount_Silver() {
        BigDecimal cartTotal = new BigDecimal("100.00");

        BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(cartTotal, LoyaltyLevel.SILVER);

        assertEquals(new BigDecimal("10.00"), loyaltyDiscount); // 10% of 100.00
    }

    @Test
    void testCalculateLoyaltyDiscount_Gold() {
        BigDecimal cartTotal = new BigDecimal("100.00");

        BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(cartTotal, LoyaltyLevel.GOLD);

        assertEquals(new BigDecimal("15.00"), loyaltyDiscount); // 15% of 100.00
    }

    @Test
    void testCalculateLoyaltyDiscount_NullLoyaltyLevel() {
        BigDecimal cartTotal = new BigDecimal("100.00");

        BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(cartTotal, null);

        assertTrue(loyaltyDiscount.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void testIsEligibleForElectronicsDiscount() {
        assertTrue(discountService.isEligibleForElectronicsDiscount(ProductCategory.ELECTRONICS, 3));
        assertFalse(discountService.isEligibleForElectronicsDiscount(ProductCategory.ELECTRONICS, 2));
        assertFalse(discountService.isEligibleForElectronicsDiscount(ProductCategory.ELECTRONICS, 1));
        assertFalse(discountService.isEligibleForElectronicsDiscount(ProductCategory.BOOKS, 3));
        assertFalse(discountService.isEligibleForElectronicsDiscount(ProductCategory.CLOTHING, 5));
    }

    @Test
    void testIsEligibleForBulkDiscount() {
        assertTrue(discountService.isEligibleForBulkDiscount(new BigDecimal("250.00")));
        assertTrue(discountService.isEligibleForBulkDiscount(new BigDecimal("200.01")));
        assertFalse(discountService.isEligibleForBulkDiscount(new BigDecimal("200.00")));
        assertFalse(discountService.isEligibleForBulkDiscount(new BigDecimal("199.99")));
        assertFalse(discountService.isEligibleForBulkDiscount(new BigDecimal("100.00")));
    }

    @Test
    void testGetElectronicsDiscountRate() {
        assertEquals(new BigDecimal("0.15"), discountService.getElectronicsDiscountRate());
    }

    @Test
    void testGetBulkDiscountRate() {
        assertEquals(new BigDecimal("0.10"), discountService.getBulkDiscountRate());
    }

    @Test
    void testGetBulkDiscountThreshold() {
        assertEquals(new BigDecimal("200.00"), discountService.getBulkDiscountThreshold());
    }

    @Test
    void testGetElectronicsDiscountMinQuantity() {
        assertEquals(2, discountService.getElectronicsDiscountMinQuantity());
    }

    @Test
    void testComplexScenario_MultipleItemsAndDiscounts() {
        CartItemResponse laptopItem = createCartItemResponse(1L, "Laptop", ProductCategory.ELECTRONICS, 
                3, new BigDecimal("1000.00"), new BigDecimal("3000.00"), new BigDecimal("3300.00"));

        CartItemResponse phoneItem = createCartItemResponse(2L, "Phone", ProductCategory.ELECTRONICS, 
                2, new BigDecimal("800.00"), new BigDecimal("1600.00"), new BigDecimal("1760.00"));

        CartItemResponse bookItem = createCartItemResponse(3L, "Book", ProductCategory.BOOKS, 
                1, new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("50.00"));

        List<CartItemResponse> items = Arrays.asList(laptopItem, phoneItem, bookItem);

        BigDecimal totalDiscounts = discountService.calculateItemSpecificDiscounts(items);

        // Only laptop should get electronics discount (quantity > 2)
        assertEquals(new BigDecimal("495.00"), totalDiscounts); // 15% of 3300.00
        assertEquals(new BigDecimal("495.00"), laptopItem.getItemSpecificDiscount());
        assertEquals(BigDecimal.ZERO, phoneItem.getItemSpecificDiscount());
        assertEquals(BigDecimal.ZERO, bookItem.getItemSpecificDiscount());
    }

    private CartItemResponse createCartItemResponse(Long id, String name, ProductCategory category, 
            Integer quantity, BigDecimal unitPrice, BigDecimal subtotal, BigDecimal totalWithTax) {
        CartItemResponse item = new CartItemResponse();
        item.setId(id);
        item.setName(name);
        item.setCategory(category);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setSubtotal(subtotal);
        item.setTotalWithTax(totalWithTax);
        return item;
    }
}