package com.example.shoppingcart.controller;

import com.example.shoppingcart.controller.config.TestConfiguration;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import com.example.shoppingcart.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = {TestConfiguration.class, ProductController.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product sampleProduct;
    private Product sampleProduct2;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop");
        sampleProduct.setCategory(ProductCategory.ELECTRONICS);
        sampleProduct.setPrice(BigDecimal.valueOf(1000.00));

        sampleProduct2 = new Product();
        sampleProduct2.setId(2L);
        sampleProduct2.setName("Java Book");
        sampleProduct2.setCategory(ProductCategory.BOOKS);
        sampleProduct2.setPrice(BigDecimal.valueOf(25.99));
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws Exception {
        List<Product> products = Arrays.asList(sampleProduct, sampleProduct2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Java Book"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getProductById_ExistingProduct_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("ELECTRONICS"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void getProductById_NonExistingProduct_ShouldReturnNotFound() throws Exception {
        when(productService.getProductById(999L)).thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsOfCategory() throws Exception {
        List<Product> electronics = Arrays.asList(sampleProduct);
        when(productService.getProductsByCategory(ProductCategory.ELECTRONICS)).thenReturn(electronics);

        mockMvc.perform(get("/api/v1/products/category/ELECTRONICS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("ELECTRONICS"));

        verify(productService, times(1)).getProductsByCategory(ProductCategory.ELECTRONICS);
    }

    @Test
    void createProduct_ValidProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void createProduct_InvalidProduct_ShouldReturnBadRequest() throws Exception {
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Invalid product"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_ExistingProduct_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(sampleProduct);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_NonExistingProduct_ShouldReturnNotFound() throws Exception {
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(put("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void updateProduct_InvalidData_ShouldReturnBadRequest() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void deleteProduct_ExistingProduct_ShouldReturnNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    void deleteProduct_NonExistingProduct_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/v1/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999L);
    }

    @Test
    void updateStock_ValidRequest_ShouldReturnSuccess() throws Exception {
        Map<String, Integer> stockUpdate = new HashMap<>();
        stockUpdate.put("quantity", 50);

        doNothing().when(productService).updateStock(1L, 50);

        mockMvc.perform(patch("/api/v1/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Stock updated successfully"))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.newStock").value(50));

        verify(productService, times(1)).updateStock(1L, 50);
    }

    @Test
    void updateStock_MissingQuantity_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> stockUpdate = new HashMap<>();

        mockMvc.perform(patch("/api/v1/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).updateStock(anyLong(), anyInt());
    }

    @Test
    void updateStock_NonExistingProduct_ShouldReturnNotFound() throws Exception {
        Map<String, Integer> stockUpdate = new HashMap<>();
        stockUpdate.put("quantity", 50);

        doThrow(new RuntimeException("Product not found")).when(productService).updateStock(999L, 50);

        mockMvc.perform(patch("/api/v1/products/999/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateStock(999L, 50);
    }

    @Test
    void updateStock_InvalidStockData_ShouldReturnBadRequest() throws Exception {
        Map<String, Integer> stockUpdate = new HashMap<>();
        stockUpdate.put("quantity", -1);

        doThrow(new RuntimeException("Invalid stock")).when(productService).updateStock(1L, -1);

        mockMvc.perform(patch("/api/v1/products/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockUpdate)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateStock(1L, -1);
    }

    @Test
    void handleException_ShouldReturnInternalServerError() throws Exception {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));

        verify(productService, times(1)).getAllProducts();
    }
}