package com.example.shoppingcart.dto;

import com.example.shoppingcart.model.LoyaltyLevel;
import java.math.BigDecimal;
import java.util.List;

public class CartCalculationResponse {
    private List<CartItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal totalTax;
    private BigDecimal totalAfterTax;
    private BigDecimal itemSpecificDiscounts;
    private BigDecimal totalAfterItemDiscounts;
    private BigDecimal bulkDiscount;
    private BigDecimal totalAfterBulkDiscount;
    private LoyaltyLevel customerLoyaltyLevel;
    private BigDecimal loyaltyDiscount;
    private BigDecimal finalTotal;

    public CartCalculationResponse() {}

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalAfterTax() {
        return totalAfterTax;
    }

    public void setTotalAfterTax(BigDecimal totalAfterTax) {
        this.totalAfterTax = totalAfterTax;
    }

    public BigDecimal getItemSpecificDiscounts() {
        return itemSpecificDiscounts;
    }

    public void setItemSpecificDiscounts(BigDecimal itemSpecificDiscounts) {
        this.itemSpecificDiscounts = itemSpecificDiscounts;
    }

    public BigDecimal getTotalAfterItemDiscounts() {
        return totalAfterItemDiscounts;
    }

    public void setTotalAfterItemDiscounts(BigDecimal totalAfterItemDiscounts) {
        this.totalAfterItemDiscounts = totalAfterItemDiscounts;
    }

    public BigDecimal getBulkDiscount() {
        return bulkDiscount;
    }

    public void setBulkDiscount(BigDecimal bulkDiscount) {
        this.bulkDiscount = bulkDiscount;
    }

    public BigDecimal getTotalAfterBulkDiscount() {
        return totalAfterBulkDiscount;
    }

    public void setTotalAfterBulkDiscount(BigDecimal totalAfterBulkDiscount) {
        this.totalAfterBulkDiscount = totalAfterBulkDiscount;
    }

    public LoyaltyLevel getCustomerLoyaltyLevel() {
        return customerLoyaltyLevel;
    }

    public void setCustomerLoyaltyLevel(LoyaltyLevel customerLoyaltyLevel) {
        this.customerLoyaltyLevel = customerLoyaltyLevel;
    }

    public BigDecimal getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public void setLoyaltyDiscount(BigDecimal loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }
}