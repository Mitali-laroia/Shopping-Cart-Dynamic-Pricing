package com.example.shoppingcart.dto;

public class CreateCartRequest {
    private Long customerId;

    public CreateCartRequest() {}

    public CreateCartRequest(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}