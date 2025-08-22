package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.DiscountRule;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DiscountRuleMapper {
    
    List<DiscountRule> findAll();
    
    DiscountRule findById(@Param("id") Long id);
    
    List<DiscountRule> findActiveRules();
    
    List<DiscountRule> findByRuleType(@Param("ruleType") String ruleType);
    
    List<DiscountRule> findByCategory(@Param("category") ProductCategory category);
    
    List<DiscountRule> findByLoyaltyLevel(@Param("loyaltyLevel") LoyaltyLevel loyaltyLevel);
    
    void insert(DiscountRule discountRule);
    
    void update(DiscountRule discountRule);
    
    void deleteById(@Param("id") Long id);
}