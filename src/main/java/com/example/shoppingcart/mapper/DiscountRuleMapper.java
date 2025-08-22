package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.DiscountRule;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.ProductCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface DiscountRuleMapper {
    
    @Select("SELECT * FROM shopping_cart.discount_rules ORDER BY id")
    List<DiscountRule> findAll();
    
    @Select("SELECT * FROM shopping_cart.discount_rules WHERE id = #{id}")
    DiscountRule findById(@Param("id") Long id);
    
    @Select("SELECT * FROM shopping_cart.discount_rules WHERE is_active = true ORDER BY id")
    List<DiscountRule> findActiveRules();
    
    @Select("SELECT * FROM shopping_cart.discount_rules WHERE rule_type = #{ruleType} AND is_active = true ORDER BY id")
    List<DiscountRule> findByRuleType(@Param("ruleType") String ruleType);
    
    @Select("SELECT * FROM shopping_cart.discount_rules WHERE category = #{category} AND is_active = true ORDER BY id")
    List<DiscountRule> findByCategory(@Param("category") ProductCategory category);
    
    @Select("SELECT * FROM shopping_cart.discount_rules WHERE loyalty_level = #{loyaltyLevel} AND is_active = true ORDER BY id")
    List<DiscountRule> findByLoyaltyLevel(@Param("loyaltyLevel") LoyaltyLevel loyaltyLevel);
    
    @Insert("INSERT INTO shopping_cart.discount_rules (rule_name, rule_type, category, min_quantity, min_cart_value, " +
            "loyalty_level, discount_percentage, is_active, created_at) " +
            "VALUES (#{ruleName}, #{ruleType}, #{category}, #{minQuantity}, #{minCartValue}, " +
            "#{loyaltyLevel}, #{discountPercentage}, #{isActive}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DiscountRule discountRule);
    
    @Update("UPDATE shopping_cart.discount_rules SET rule_name = #{ruleName}, rule_type = #{ruleType}, " +
            "category = #{category}, min_quantity = #{minQuantity}, min_cart_value = #{minCartValue}, " +
            "loyalty_level = #{loyaltyLevel}, discount_percentage = #{discountPercentage}, is_active = #{isActive} " +
            "WHERE id = #{id}")
    void update(DiscountRule discountRule);
    
    @Delete("DELETE FROM shopping_cart.discount_rules WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}