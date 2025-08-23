package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductMapperTest extends BaseMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void testFindAll() {
        List<Product> products = productMapper.findAll();
        
        assertNotNull(products);
        assertEquals(12, products.size());
        
        // Verify first product
        Product firstProduct = products.get(0);
        assertEquals(1L, firstProduct.getId());
        assertEquals("Laptop", firstProduct.getName());
        assertEquals(ProductCategory.ELECTRONICS, firstProduct.getCategory());
        assertEquals(new BigDecimal("1000.00"), firstProduct.getPrice());
        assertEquals(50, firstProduct.getStockQuantity());
    }

    @Test
    void testFindById() {
        Product product = productMapper.findById(2L);
        
        assertNotNull(product);
        assertEquals(2L, product.getId());
        assertEquals("Smartphone", product.getName());
        assertEquals("Latest smartphone model", product.getDescription());
        assertEquals(ProductCategory.ELECTRONICS, product.getCategory());
        assertEquals(new BigDecimal("800.00"), product.getPrice());
        assertEquals(100, product.getStockQuantity());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    void testFindByIdNotFound() {
        Product product = productMapper.findById(999L);
        assertNull(product);
    }

    @Test
    void testFindByCategory() {
        List<Product> electronics = productMapper.findByCategory(ProductCategory.ELECTRONICS);
        
        assertNotNull(electronics);
        assertEquals(4, electronics.size());
        assertTrue(electronics.stream().allMatch(p -> ProductCategory.ELECTRONICS.equals(p.getCategory())));
        
        List<Product> books = productMapper.findByCategory(ProductCategory.BOOKS);
        assertEquals(4, books.size());
        assertTrue(books.stream().allMatch(p -> ProductCategory.BOOKS.equals(p.getCategory())));
        
        List<Product> clothing = productMapper.findByCategory(ProductCategory.CLOTHING);
        assertEquals(4, clothing.size());
        assertTrue(clothing.stream().allMatch(p -> ProductCategory.CLOTHING.equals(p.getCategory())));
    }

    @Test
    void testInsert() {
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setDescription("Test description");
        newProduct.setCategory(ProductCategory.ELECTRONICS);
        newProduct.setPrice(new BigDecimal("299.99"));
        newProduct.setStockQuantity(25);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());

        productMapper.insert(newProduct);
        
        assertNotNull(newProduct.getId());
        assertTrue(newProduct.getId() > 0);
        
        // Verify the product was inserted
        Product insertedProduct = productMapper.findById(newProduct.getId());
        assertNotNull(insertedProduct);
        assertEquals("Test Product", insertedProduct.getName());
        assertEquals("Test description", insertedProduct.getDescription());
        assertEquals(ProductCategory.ELECTRONICS, insertedProduct.getCategory());
        assertEquals(new BigDecimal("299.99"), insertedProduct.getPrice());
        assertEquals(25, insertedProduct.getStockQuantity());
    }

    @Test
    void testUpdate() {
        Product product = productMapper.findById(1L);
        assertNotNull(product);
        
        product.setName("Updated Laptop");
        product.setDescription("Updated description");
        product.setPrice(new BigDecimal("1200.00"));
        product.setStockQuantity(75);
        product.setUpdatedAt(LocalDateTime.now());
        
        productMapper.update(product);
        
        // Verify the update
        Product updatedProduct = productMapper.findById(1L);
        assertNotNull(updatedProduct);
        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals("Updated description", updatedProduct.getDescription());
        assertEquals(new BigDecimal("1200.00"), updatedProduct.getPrice());
        assertEquals(75, updatedProduct.getStockQuantity());
        assertEquals(ProductCategory.ELECTRONICS, updatedProduct.getCategory()); // Should remain unchanged
    }

    @Test
    void testUpdateStock() {
        Product product = productMapper.findById(1L);
        assertNotNull(product);
        int originalStock = product.getStockQuantity();
        
        productMapper.updateStock(1L, originalStock - 10);
        
        Product updatedProduct = productMapper.findById(1L);
        assertNotNull(updatedProduct);
        assertEquals(originalStock - 10, updatedProduct.getStockQuantity());
    }

    @Test
    void testDeleteById() {
        // Verify product exists
        Product product = productMapper.findById(12L);
        assertNotNull(product);
        
        // Delete the product
        productMapper.deleteById(12L);
        
        // Verify product is deleted
        Product deletedProduct = productMapper.findById(12L);
        assertNull(deletedProduct);
        
        // Verify total count is reduced by checking findAll size
        List<Product> products = productMapper.findAll();
        assertEquals(11, products.size());
    }

    @Test
    void testProductCategoryMapping() {
        // Test that enum mapping works correctly
        List<Product> allProducts = productMapper.findAll();
        
        // Verify that each category has expected products
        long electronicsCount = allProducts.stream()
            .filter(p -> ProductCategory.ELECTRONICS.equals(p.getCategory()))
            .count();
        assertEquals(4, electronicsCount);
        
        long booksCount = allProducts.stream()
            .filter(p -> ProductCategory.BOOKS.equals(p.getCategory()))
            .count();
        assertEquals(4, booksCount);
        
        long clothingCount = allProducts.stream()
            .filter(p -> ProductCategory.CLOTHING.equals(p.getCategory()))
            .count();
        assertEquals(4, clothingCount);
    }

    @Test
    void testPriceHandling() {
        // Test BigDecimal price handling
        Product product = productMapper.findById(5L); // Java Programming book
        assertNotNull(product);
        assertEquals(new BigDecimal("45.99"), product.getPrice());
        
        // Update with new price
        product.setPrice(new BigDecimal("49.99"));
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        
        Product updatedProduct = productMapper.findById(5L);
        assertEquals(new BigDecimal("49.99"), updatedProduct.getPrice());
    }

    @Test
    void testStockQuantityOperations() {
        Product product = productMapper.findById(1L);
        assertNotNull(product);
        int originalStock = product.getStockQuantity();
        
        // Test stock reduction
        productMapper.updateStock(1L, originalStock - 5);
        Product afterReduction = productMapper.findById(1L);
        assertEquals(originalStock - 5, afterReduction.getStockQuantity());
        
        // Test stock increase
        productMapper.updateStock(1L, originalStock + 10);
        Product afterIncrease = productMapper.findById(1L);
        assertEquals(originalStock + 10, afterIncrease.getStockQuantity());
    }

    @Test
    void testFindProductsByPriceRange() {
        List<Product> allProducts = productMapper.findAll();
        
        // Find expensive products (>= 500)
        List<Product> expensiveProducts = allProducts.stream()
            .filter(p -> p.getPrice().compareTo(new BigDecimal("500.00")) >= 0)
            .toList();
        
        assertTrue(expensiveProducts.size() >= 3); // Laptop, Smartphone, Tablet
        
        // Find affordable products (<= 100)
        List<Product> affordableProducts = allProducts.stream()
            .filter(p -> p.getPrice().compareTo(new BigDecimal("100.00")) <= 0)
            .toList();
        
        assertTrue(affordableProducts.size() > 0);
    }
}