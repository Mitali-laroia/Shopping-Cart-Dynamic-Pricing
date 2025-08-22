package com.example.shoppingcart.dto;

public class UpdateItemRequest {
    private Integer quantity;

    public UpdateItemRequest() {}

    public UpdateItemRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}