package com.example.shoppingcart.model;

public enum ProductCategory {
    ELECTRONICS("Electronics"),
    BOOKS("Books"),
    CLOTHING("Clothing");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}