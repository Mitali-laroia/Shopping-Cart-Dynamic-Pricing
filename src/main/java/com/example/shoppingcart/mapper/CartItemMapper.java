package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartItemMapper {
    
    List<CartItem> findAll();
    
    CartItem findById(@Param("id") Long id);
    
    List<CartItem> findByCartId(@Param("cartId") Long cartId);
    
    CartItem findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    void insert(CartItem cartItem);
    
    void update(CartItem cartItem);
    
    void deleteById(@Param("id") Long id);
    
    void deleteByCartId(@Param("cartId") Long cartId);
}