package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    
    List<Product> findAll();
    
    Product findById(@Param("id") Long id);
    
    List<Product> findByCategory(@Param("category") ProductCategory category);
    
    void insert(Product product);
    
    void update(Product product);
    
    void deleteById(@Param("id") Long id);
    
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}