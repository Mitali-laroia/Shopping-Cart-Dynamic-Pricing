package com.example.shoppingcart.controller;

import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import com.example.shoppingcart.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.debug("GET /api/v1/products - Get all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.debug("GET /api/v1/products/{} - Get product by id", id);
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            logger.warn("Product not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable ProductCategory category) {
        logger.debug("GET /api/v1/products/category/{} - Get products by category", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.debug("POST /api/v1/products - Create new product");
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid product data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        logger.debug("PUT /api/v1/products/{} - Update product", id);
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Product not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.warn("Invalid product data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.debug("DELETE /api/v1/products/{} - Delete product", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Product not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> stockUpdate) {
        logger.debug("PATCH /api/v1/products/{}/stock - Update stock", id);
        try {
            Integer quantity = stockUpdate.get("quantity");
            if (quantity == null) {
                return ResponseEntity.badRequest().build();
            }
            productService.updateStock(id, quantity);
            return ResponseEntity.ok(Map.of("message", "Stock updated successfully", "productId", id, "newStock", quantity));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Product not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.warn("Invalid stock data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error in ProductController: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", e.getMessage()));
    }
}