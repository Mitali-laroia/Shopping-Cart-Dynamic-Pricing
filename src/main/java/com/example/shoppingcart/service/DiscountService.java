package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.CartItemResponse;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DiscountService {

    private static final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private static final BigDecimal ELECTRONICS_DISCOUNT_RATE = new BigDecimal("0.15");
    private static final int ELECTRONICS_DISCOUNT_MIN_QUANTITY = 2;
    
    private static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal BULK_DISCOUNT_THRESHOLD = new BigDecimal("200.00");
    
    private static final BigDecimal BRONZE_DISCOUNT_RATE = new BigDecimal("0.05");
    private static final BigDecimal SILVER_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal GOLD_DISCOUNT_RATE = new BigDecimal("0.15");

    public BigDecimal calculateItemSpecificDiscounts(List<CartItemResponse> items) {
        logger.debug("Calculating item-specific discounts for {} items", items.size());
        
        BigDecimal totalItemDiscounts = BigDecimal.ZERO;
        
        for (CartItemResponse item : items) {
            BigDecimal itemDiscount = calculateElectronicsDiscount(item);
            item.setItemSpecificDiscount(itemDiscount);
            
            BigDecimal totalAfterDiscount = item.getTotalWithTax().subtract(itemDiscount);
            item.setTotalAfterItemDiscount(totalAfterDiscount);
            
            totalItemDiscounts = totalItemDiscounts.add(itemDiscount);
            
            if (itemDiscount.compareTo(BigDecimal.ZERO) > 0) {
                logger.debug("Applied electronics discount of {} to item: {}", 
                           itemDiscount, item.getName());
            }
        }
        
        logger.debug("Total item-specific discounts: {}", totalItemDiscounts);
        return totalItemDiscounts.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateElectronicsDiscount(CartItemResponse item) {
        if (item.getCategory() == ProductCategory.ELECTRONICS && 
            item.getQuantity() > ELECTRONICS_DISCOUNT_MIN_QUANTITY) {
            
            BigDecimal discount = item.getTotalWithTax()
                    .multiply(ELECTRONICS_DISCOUNT_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
            
            logger.debug("Electronics discount applied: {} for item {} with quantity {}", 
                        discount, item.getName(), item.getQuantity());
            
            return discount;
        }
        
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateBulkDiscount(BigDecimal cartTotal) {
        logger.debug("Calculating bulk discount for cart total: {}", cartTotal);
        
        if (cartTotal.compareTo(BULK_DISCOUNT_THRESHOLD) > 0) {
            BigDecimal bulkDiscount = cartTotal.multiply(BULK_DISCOUNT_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
            
            logger.debug("Bulk discount applied: {} ({}% of {})", 
                        bulkDiscount, BULK_DISCOUNT_RATE.multiply(BigDecimal.valueOf(100)), cartTotal);
            
            return bulkDiscount;
        }
        
        logger.debug("Cart total {} below bulk discount threshold {}", cartTotal, BULK_DISCOUNT_THRESHOLD);
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateLoyaltyDiscount(BigDecimal cartTotal, LoyaltyLevel loyaltyLevel) {
        logger.debug("Calculating loyalty discount for level: {} on amount: {}", loyaltyLevel, cartTotal);
        
        BigDecimal discountRate = getLoyaltyDiscountRate(loyaltyLevel);
        BigDecimal loyaltyDiscount = cartTotal.multiply(discountRate)
                .setScale(2, RoundingMode.HALF_UP);
        
        logger.debug("Loyalty discount applied: {} ({}% for {})", 
                    loyaltyDiscount, discountRate.multiply(BigDecimal.valueOf(100)), loyaltyLevel);
        
        return loyaltyDiscount;
    }

    private BigDecimal getLoyaltyDiscountRate(LoyaltyLevel loyaltyLevel) {
        if (loyaltyLevel == null) {
            logger.warn("Null loyalty level provided, applying no discount");
            return BigDecimal.ZERO;
        }
        
        switch (loyaltyLevel) {
            case BRONZE:
                return BRONZE_DISCOUNT_RATE;
            case SILVER:
                return SILVER_DISCOUNT_RATE;
            case GOLD:
                return GOLD_DISCOUNT_RATE;
            default:
                logger.warn("Unknown loyalty level: {}, applying no discount", loyaltyLevel);
                return BigDecimal.ZERO;
        }
    }

    public boolean isEligibleForElectronicsDiscount(ProductCategory category, int quantity) {
        return category == ProductCategory.ELECTRONICS && quantity > ELECTRONICS_DISCOUNT_MIN_QUANTITY;
    }

    public boolean isEligibleForBulkDiscount(BigDecimal cartTotal) {
        return cartTotal.compareTo(BULK_DISCOUNT_THRESHOLD) > 0;
    }

    public BigDecimal getElectronicsDiscountRate() {
        return ELECTRONICS_DISCOUNT_RATE;
    }

    public BigDecimal getBulkDiscountRate() {
        return BULK_DISCOUNT_RATE;
    }

    public BigDecimal getBulkDiscountThreshold() {
        return BULK_DISCOUNT_THRESHOLD;
    }

    public int getElectronicsDiscountMinQuantity() {
        return ELECTRONICS_DISCOUNT_MIN_QUANTITY;
    }
}