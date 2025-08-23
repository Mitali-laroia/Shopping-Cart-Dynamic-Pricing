package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.ProductMapper;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("High-performance laptop");
        testProduct.setCategory(ProductCategory.ELECTRONICS);
        testProduct.setPrice(new BigDecimal("1000.00"));
        testProduct.setStockQuantity(50);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllProducts() {
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productMapper.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.getAllProducts();

        assertEquals(expectedProducts, actualProducts);
        verify(productMapper, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        when(productMapper.findById(1L)).thenReturn(testProduct);

        Product actualProduct = productService.getProductById(1L);

        assertEquals(testProduct, actualProduct);
        verify(productMapper, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.getProductById(999L));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productMapper, times(1)).findById(999L);
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productMapper.findByCategory(ProductCategory.ELECTRONICS)).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.getProductsByCategory(ProductCategory.ELECTRONICS);

        assertEquals(expectedProducts, actualProducts);
        verify(productMapper, times(1)).findByCategory(ProductCategory.ELECTRONICS);
    }

    @Test
    void testCreateProduct_Success() {
        Product newProduct = new Product();
        newProduct.setName("Smartphone");
        newProduct.setDescription("Latest smartphone");
        newProduct.setCategory(ProductCategory.ELECTRONICS);
        newProduct.setPrice(new BigDecimal("800.00"));
        newProduct.setStockQuantity(100);

        doNothing().when(productMapper).insert(any(Product.class));

        Product createdProduct = productService.createProduct(newProduct);

        assertNotNull(createdProduct);
        assertEquals("Smartphone", createdProduct.getName());
        assertEquals("Latest smartphone", createdProduct.getDescription());
        assertEquals(ProductCategory.ELECTRONICS, createdProduct.getCategory());
        assertEquals(new BigDecimal("800.00"), createdProduct.getPrice());
        assertEquals(100, createdProduct.getStockQuantity());
        assertNotNull(createdProduct.getCreatedAt());
        assertNotNull(createdProduct.getUpdatedAt());
        verify(productMapper, times(1)).insert(any(Product.class));
    }

    @Test
    void testCreateProduct_MissingName() {
        Product newProduct = new Product();
        newProduct.setDescription("Test product");
        newProduct.setCategory(ProductCategory.ELECTRONICS);
        newProduct.setPrice(new BigDecimal("100.00"));
        newProduct.setStockQuantity(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.createProduct(newProduct));

        assertEquals("Product name is required", exception.getMessage());
        verify(productMapper, times(0)).insert(any(Product.class));
    }

    @Test
    void testCreateProduct_MissingCategory() {
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setDescription("Test product");
        newProduct.setPrice(new BigDecimal("100.00"));
        newProduct.setStockQuantity(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.createProduct(newProduct));

        assertEquals("Product category is required", exception.getMessage());
        verify(productMapper, times(0)).insert(any(Product.class));
    }

    @Test
    void testCreateProduct_NegativePrice() {
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setDescription("Test product");
        newProduct.setCategory(ProductCategory.ELECTRONICS);
        newProduct.setPrice(new BigDecimal("-100.00"));
        newProduct.setStockQuantity(10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.createProduct(newProduct));

        assertEquals("Product price must be non-negative", exception.getMessage());
        verify(productMapper, times(0)).insert(any(Product.class));
    }

    @Test
    void testCreateProduct_NegativeStock() {
        Product newProduct = new Product();
        newProduct.setName("Test Product");
        newProduct.setDescription("Test product");
        newProduct.setCategory(ProductCategory.ELECTRONICS);
        newProduct.setPrice(new BigDecimal("100.00"));
        newProduct.setStockQuantity(-5);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.createProduct(newProduct));

        assertEquals("Stock quantity must be non-negative", exception.getMessage());
        verify(productMapper, times(0)).insert(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        Product updateData = new Product();
        updateData.setName("Updated Laptop");
        updateData.setDescription("Updated high-performance laptop");
        updateData.setCategory(ProductCategory.ELECTRONICS);
        updateData.setPrice(new BigDecimal("1200.00"));
        updateData.setStockQuantity(75);

        when(productMapper.findById(1L)).thenReturn(testProduct);
        doNothing().when(productMapper).update(any(Product.class));

        Product updatedProduct = productService.updateProduct(1L, updateData);

        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals("Updated high-performance laptop", updatedProduct.getDescription());
        assertEquals(ProductCategory.ELECTRONICS, updatedProduct.getCategory());
        assertEquals(new BigDecimal("1200.00"), updatedProduct.getPrice());
        assertEquals(75, updatedProduct.getStockQuantity());
        assertNotNull(updatedProduct.getUpdatedAt());
        verify(productMapper, times(1)).findById(1L);
        verify(productMapper, times(1)).update(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        Product updateData = new Product();
        updateData.setName("Updated Product");

        when(productMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.updateProduct(999L, updateData));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productMapper, times(1)).findById(999L);
        verify(productMapper, times(0)).update(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        when(productMapper.findById(1L)).thenReturn(testProduct);
        doNothing().when(productMapper).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productMapper, times(1)).findById(1L);
        verify(productMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.deleteProduct(999L));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productMapper, times(1)).findById(999L);
        verify(productMapper, times(0)).deleteById(any());
    }

    @Test
    void testUpdateStock_Success() {
        when(productMapper.findById(1L)).thenReturn(testProduct);
        doNothing().when(productMapper).updateStock(1L, 25);

        productService.updateStock(1L, 25);

        verify(productMapper, times(1)).findById(1L);
        verify(productMapper, times(1)).updateStock(1L, 25);
    }

    @Test
    void testUpdateStock_NegativeQuantity() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> productService.updateStock(1L, -5));

        assertEquals("Stock quantity cannot be negative", exception.getMessage());
        verify(productMapper, times(0)).findById(any());
        verify(productMapper, times(0)).updateStock(any(), any());
    }

    @Test
    void testUpdateStock_ProductNotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> productService.updateStock(999L, 25));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productMapper, times(1)).findById(999L);
        verify(productMapper, times(0)).updateStock(any(), any());
    }
}