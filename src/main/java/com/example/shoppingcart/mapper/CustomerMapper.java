package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CustomerMapper {
    
    @Select("SELECT * FROM shopping_cart.customers ORDER BY id")
    List<Customer> findAll();
    
    @Select("SELECT * FROM shopping_cart.customers WHERE id = #{id}")
    Customer findById(@Param("id") Long id);
    
    @Select("SELECT * FROM shopping_cart.customers WHERE email = #{email}")
    Customer findByEmail(@Param("email") String email);
    
    @Select("SELECT * FROM shopping_cart.customers WHERE loyalty_level = #{loyaltyLevel} ORDER BY id")
    List<Customer> findByLoyaltyLevel(@Param("loyaltyLevel") LoyaltyLevel loyaltyLevel);
    
    @Insert("INSERT INTO shopping_cart.customers (name, email, loyalty_level, created_at, updated_at) " +
            "VALUES (#{name}, #{email}, #{loyaltyLevel}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Customer customer);
    
    @Update("UPDATE shopping_cart.customers SET name = #{name}, email = #{email}, loyalty_level = #{loyaltyLevel}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(Customer customer);
    
    @Delete("DELETE FROM shopping_cart.customers WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}