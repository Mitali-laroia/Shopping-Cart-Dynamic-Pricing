package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartCalculationRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.dto.CartItemResponse;
import com.example.shoppingcart.dto.ProductRequest;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PricingService {

    private static final Logger logger = LoggerFactory.getLogger(PricingService.class);

    @Autowired
    private DiscountService discountService;

    private static final BigDecimal ELECTRONICS_TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal CLOTHING_TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal BOOKS_TAX_RATE = BigDecimal.ZERO;

    public CartCalculationResponse calculateCartPricing(CartCalculationRequest request) {
        logger.debug("Calculating cart pricing for {} items", request.getItems().size());
        
        CartCalculationResponse response = new CartCalculationResponse();
        List<CartItemResponse> itemResponses = new ArrayList<>();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        
        for (ProductRequest product : request.getItems()) {
            CartItemResponse itemResponse = calculateItemPricing(product);
            itemResponses.add(itemResponse);
            
            subtotal = subtotal.add(itemResponse.getSubtotal());
            totalTax = totalTax.add(itemResponse.getTaxAmount());
        }
        
        response.setItems(itemResponses);
        response.setSubtotal(subtotal);
        response.setTotalTax(totalTax);
        
        BigDecimal totalAfterTax = subtotal.add(totalTax);
        response.setTotalAfterTax(totalAfterTax);
        
        applyDiscounts(response, request.getCustomer().getLoyaltyLevel());
        
        logger.info("Calculated cart pricing: subtotal={}, tax={}, finalTotal={}", 
                   subtotal, totalTax, response.getFinalTotal());
        
        return response;
    }

    private CartItemResponse calculateItemPricing(ProductRequest product) {
        CartItemResponse item = new CartItemResponse();
        item.setId(product.getId());
        item.setName(product.getName());
        item.setCategory(product.getCategory());
        item.setQuantity(product.getQuantity());
        item.setUnitPrice(product.getPrice());
        
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity()));
        item.setSubtotal(subtotal);
        
        BigDecimal taxRate = getTaxRateForCategory(product.getCategory());
        BigDecimal taxAmount = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        item.setTaxRate(taxRate);
        item.setTaxAmount(taxAmount);
        
        BigDecimal totalWithTax = subtotal.add(taxAmount);
        item.setTotalWithTax(totalWithTax);
        
        return item;
    }

    private BigDecimal getTaxRateForCategory(ProductCategory category) {
        switch (category) {
            case ELECTRONICS:
                return ELECTRONICS_TAX_RATE;
            case CLOTHING:
                return CLOTHING_TAX_RATE;
            case BOOKS:
                return BOOKS_TAX_RATE;
            default:
                logger.warn("Unknown product category: {}, applying no tax", category);
                return BigDecimal.ZERO;
        }
    }

    private void applyDiscounts(CartCalculationResponse response, LoyaltyLevel loyaltyLevel) {
        logger.debug("Applying discounts for loyalty level: {}", loyaltyLevel);
        
        BigDecimal currentTotal = response.getTotalAfterTax();
        
        BigDecimal itemSpecificDiscounts = discountService.calculateItemSpecificDiscounts(response.getItems());
        response.setItemSpecificDiscounts(itemSpecificDiscounts);
        
        currentTotal = currentTotal.subtract(itemSpecificDiscounts);
        response.setTotalAfterItemDiscounts(currentTotal);
        
        BigDecimal bulkDiscount = discountService.calculateBulkDiscount(currentTotal);
        response.setBulkDiscount(bulkDiscount);
        
        currentTotal = currentTotal.subtract(bulkDiscount);
        response.setTotalAfterBulkDiscount(currentTotal);
        
        response.setCustomerLoyaltyLevel(loyaltyLevel);
        BigDecimal loyaltyDiscount = discountService.calculateLoyaltyDiscount(currentTotal, loyaltyLevel);
        response.setLoyaltyDiscount(loyaltyDiscount);
        
        BigDecimal finalTotal = currentTotal.subtract(loyaltyDiscount);
        response.setFinalTotal(finalTotal.setScale(2, RoundingMode.HALF_UP));
        
        logger.debug("Applied discounts: item-specific={}, bulk={}, loyalty={}", 
                    itemSpecificDiscounts, bulkDiscount, loyaltyDiscount);
    }

    public BigDecimal calculateItemTax(BigDecimal amount, ProductCategory category) {
        BigDecimal taxRate = getTaxRateForCategory(category);
        return amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalWithTax(BigDecimal amount, ProductCategory category) {
        BigDecimal tax = calculateItemTax(amount, category);
        return amount.add(tax);
    }
}