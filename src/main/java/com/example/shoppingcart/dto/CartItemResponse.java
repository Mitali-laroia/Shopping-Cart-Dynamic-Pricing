package com.example.shoppingcart.dto;

import com.example.shoppingcart.model.ProductCategory;
import java.math.BigDecimal;

public class CartItemResponse {
    private Long id;
    private String name;
    private ProductCategory category;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalWithTax;
    private BigDecimal itemSpecificDiscount;
    private BigDecimal totalAfterItemDiscount;

    public CartItemResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalWithTax() {
        return totalWithTax;
    }

    public void setTotalWithTax(BigDecimal totalWithTax) {
        this.totalWithTax = totalWithTax;
    }

    public BigDecimal getItemSpecificDiscount() {
        return itemSpecificDiscount;
    }

    public void setItemSpecificDiscount(BigDecimal itemSpecificDiscount) {
        this.itemSpecificDiscount = itemSpecificDiscount;
    }

    public BigDecimal getTotalAfterItemDiscount() {
        return totalAfterItemDiscount;
    }

    public void setTotalAfterItemDiscount(BigDecimal totalAfterItemDiscount) {
        this.totalAfterItemDiscount = totalAfterItemDiscount;
    }
}