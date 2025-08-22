package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    
    List<ShoppingCart> findAll();
    
    ShoppingCart findById(@Param("id") Long id);
    
    ShoppingCart findByIdWithItems(@Param("id") Long id);
    
    List<ShoppingCart> findByCustomerId(@Param("customerId") Long customerId);
    
    List<ShoppingCart> findByStatus(@Param("status") String status);
    
    void insert(ShoppingCart shoppingCart);
    
    void update(ShoppingCart shoppingCart);
    
    void deleteById(@Param("id") Long id);
}