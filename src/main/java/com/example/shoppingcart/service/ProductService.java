package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.ProductMapper;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getAllProducts() {
        logger.debug("Fetching all products");
        return productMapper.findAll();
    }

    public Product getProductById(Long id) {
        logger.debug("Fetching product with id: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        return product;
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        logger.debug("Fetching products by category: {}", category);
        return productMapper.findByCategory(category);
    }

    public Product createProduct(Product product) {
        logger.debug("Creating new product: {}", product.getName());
        validateProduct(product);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        logger.info("Created product with id: {}", product.getId());
        return product;
    }

    public Product updateProduct(Long id, Product productUpdate) {
        logger.debug("Updating product with id: {}", id);
        Product existingProduct = getProductById(id);
        
        existingProduct.setName(productUpdate.getName());
        existingProduct.setCategory(productUpdate.getCategory());
        existingProduct.setPrice(productUpdate.getPrice());
        existingProduct.setDescription(productUpdate.getDescription());
        existingProduct.setStockQuantity(productUpdate.getStockQuantity());
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        validateProduct(existingProduct);
        productMapper.update(existingProduct);
        logger.info("Updated product with id: {}", id);
        return existingProduct;
    }

    public void deleteProduct(Long id) {
        logger.debug("Deleting product with id: {}", id);
        Product existingProduct = getProductById(id);
        productMapper.deleteById(id);
        logger.info("Deleted product with id: {}", id);
    }

    public void updateStock(Long id, Integer quantity) {
        logger.debug("Updating stock for product id: {} to quantity: {}", id, quantity);
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        getProductById(id); // Validate product exists
        productMapper.updateStock(id, quantity);
        logger.info("Updated stock for product id: {} to quantity: {}", id, quantity);
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be non-negative");
        }
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity must be non-negative");
        }
    }
}