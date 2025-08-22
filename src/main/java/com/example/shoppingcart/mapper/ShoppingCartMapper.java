package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
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
public interface ShoppingCartMapper {
    
    @Select("SELECT c.*, cu.name as customer_name, cu.email as customer_email, cu.loyalty_level as customer_loyalty_level " +
            "FROM shopping_cart.shopping_carts c " +
            "LEFT JOIN shopping_cart.customers cu ON c.customer_id = cu.id " +
            "ORDER BY c.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer", column = "customer_id", one = @One(select = "com.example.shoppingcart.mapper.CustomerMapper.findById"))
    })
    List<ShoppingCart> findAll();
    
    @Select("SELECT c.*, cu.name as customer_name, cu.email as customer_email, cu.loyalty_level as customer_loyalty_level " +
            "FROM shopping_cart.shopping_carts c " +
            "LEFT JOIN shopping_cart.customers cu ON c.customer_id = cu.id " +
            "WHERE c.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer", column = "customer_id", one = @One(select = "com.example.shoppingcart.mapper.CustomerMapper.findById"))
    })
    ShoppingCart findById(@Param("id") Long id);
    
    @Select("SELECT c.*, cu.name as customer_name, cu.email as customer_email, cu.loyalty_level as customer_loyalty_level " +
            "FROM shopping_cart.shopping_carts c " +
            "LEFT JOIN shopping_cart.customers cu ON c.customer_id = cu.id " +
            "WHERE c.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer", column = "customer_id", one = @One(select = "com.example.shoppingcart.mapper.CustomerMapper.findById")),
        @Result(property = "items", column = "id", many = @Many(select = "com.example.shoppingcart.mapper.CartItemMapper.findByCartId"))
    })
    ShoppingCart findByIdWithItems(@Param("id") Long id);
    
    @Select("SELECT c.*, cu.name as customer_name, cu.email as customer_email, cu.loyalty_level as customer_loyalty_level " +
            "FROM shopping_cart.shopping_carts c " +
            "LEFT JOIN shopping_cart.customers cu ON c.customer_id = cu.id " +
            "WHERE c.customer_id = #{customerId} ORDER BY c.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer", column = "customer_id", one = @One(select = "com.example.shoppingcart.mapper.CustomerMapper.findById"))
    })
    List<ShoppingCart> findByCustomerId(@Param("customerId") Long customerId);
    
    @Select("SELECT c.*, cu.name as customer_name, cu.email as customer_email, cu.loyalty_level as customer_loyalty_level " +
            "FROM shopping_cart.shopping_carts c " +
            "LEFT JOIN shopping_cart.customers cu ON c.customer_id = cu.id " +
            "WHERE c.status = #{status} ORDER BY c.id")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "totalAmount", column = "total_amount"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "customer", column = "customer_id", one = @One(select = "com.example.shoppingcart.mapper.CustomerMapper.findById"))
    })
    List<ShoppingCart> findByStatus(@Param("status") String status);
    
    @Insert("INSERT INTO shopping_cart.shopping_carts (customer_id, status, total_amount, created_at, updated_at) " +
            "VALUES (#{customer.id}, #{status}, #{totalAmount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ShoppingCart shoppingCart);
    
    @Update("UPDATE shopping_cart.shopping_carts SET status = #{status}, total_amount = #{totalAmount}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(ShoppingCart shoppingCart);
    
    @Delete("DELETE FROM shopping_cart.shopping_carts WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}