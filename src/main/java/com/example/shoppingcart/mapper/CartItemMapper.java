package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CartItemMapper {
    
    @Select("SELECT ci.*, p.name as product_name, p.category as product_category, " +
            "p.price as product_price, p.description as product_description, p.stock_quantity as product_stock_quantity " +
            "FROM shopping_cart.cart_items ci " +
            "LEFT JOIN shopping_cart.products p ON ci.product_id = p.id " +
            "ORDER BY ci.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "totalPrice", column = "total_price"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "product", column = "product_id", one = @One(select = "com.example.shoppingcart.mapper.ProductMapper.findById"))
    })
    List<CartItem> findAll();
    
    @Select("SELECT ci.*, p.name as product_name, p.category as product_category, " +
            "p.price as product_price, p.description as product_description, p.stock_quantity as product_stock_quantity " +
            "FROM shopping_cart.cart_items ci " +
            "LEFT JOIN shopping_cart.products p ON ci.product_id = p.id " +
            "WHERE ci.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "totalPrice", column = "total_price"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "product", column = "product_id", one = @One(select = "com.example.shoppingcart.mapper.ProductMapper.findById"))
    })
    CartItem findById(@Param("id") Long id);
    
    @Select("SELECT ci.*, p.name as product_name, p.category as product_category, " +
            "p.price as product_price, p.description as product_description, p.stock_quantity as product_stock_quantity " +
            "FROM shopping_cart.cart_items ci " +
            "LEFT JOIN shopping_cart.products p ON ci.product_id = p.id " +
            "WHERE ci.cart_id = #{cartId} ORDER BY ci.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "totalPrice", column = "total_price"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "product", column = "product_id", one = @One(select = "com.example.shoppingcart.mapper.ProductMapper.findById"))
    })
    List<CartItem> findByCartId(@Param("cartId") Long cartId);
    
    @Select("SELECT ci.*, p.name as product_name, p.category as product_category, " +
            "p.price as product_price, p.description as product_description, p.stock_quantity as product_stock_quantity " +
            "FROM shopping_cart.cart_items ci " +
            "LEFT JOIN shopping_cart.products p ON ci.product_id = p.id " +
            "WHERE ci.cart_id = #{cartId} AND ci.product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "unitPrice", column = "unit_price"),
        @Result(property = "totalPrice", column = "total_price"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "product", column = "product_id", one = @One(select = "com.example.shoppingcart.mapper.ProductMapper.findById"))
    })
    CartItem findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    @Insert("INSERT INTO shopping_cart.cart_items (cart_id, product_id, quantity, unit_price, total_price, created_at, updated_at) " +
            "VALUES (#{shoppingCart.id}, #{product.id}, #{quantity}, #{unitPrice}, #{totalPrice}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CartItem cartItem);
    
    @Update("UPDATE shopping_cart.cart_items SET quantity = #{quantity}, unit_price = #{unitPrice}, " +
            "total_price = #{totalPrice}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(CartItem cartItem);
    
    @Delete("DELETE FROM shopping_cart.cart_items WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM shopping_cart.cart_items WHERE cart_id = #{cartId}")
    void deleteByCartId(@Param("cartId") Long cartId);
}