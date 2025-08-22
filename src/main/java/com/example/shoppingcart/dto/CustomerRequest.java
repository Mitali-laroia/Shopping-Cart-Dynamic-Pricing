package com.example.shoppingcart.dto;

import com.example.shoppingcart.model.LoyaltyLevel;

public class CustomerRequest {
    private LoyaltyLevel loyaltyLevel;

    public CustomerRequest() {}

    public CustomerRequest(LoyaltyLevel loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
    }

    public LoyaltyLevel getLoyaltyLevel() {
        return loyaltyLevel;
    }

    public void setLoyaltyLevel(LoyaltyLevel loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
    }
}