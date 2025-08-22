package com.example.shoppingcart.model;

public enum LoyaltyLevel {
    BRONZE("Bronze"),
    SILVER("Silver"),
    GOLD("Gold");

    private final String displayName;

    LoyaltyLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}