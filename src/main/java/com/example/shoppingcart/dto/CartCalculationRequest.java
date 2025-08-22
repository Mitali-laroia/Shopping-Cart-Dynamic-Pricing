package com.example.shoppingcart.dto;

import java.util.List;

public class CartCalculationRequest {
    private List<ProductRequest> items;
    private CustomerRequest customer;

    public CartCalculationRequest() {}

    public CartCalculationRequest(List<ProductRequest> items, CustomerRequest customer) {
        this.items = items;
        this.customer = customer;
    }

    public List<ProductRequest> getItems() {
        return items;
    }

    public void setItems(List<ProductRequest> items) {
        this.items = items;
    }

    public CustomerRequest getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerRequest customer) {
        this.customer = customer;
    }
}