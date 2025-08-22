package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    
    List<Customer> findAll();
    
    Customer findById(@Param("id") Long id);
    
    Customer findByEmail(@Param("email") String email);
    
    List<Customer> findByLoyaltyLevel(@Param("loyaltyLevel") LoyaltyLevel loyaltyLevel);
    
    void insert(Customer customer);
    
    void update(Customer customer);
    
    void deleteById(@Param("id") Long id);
}