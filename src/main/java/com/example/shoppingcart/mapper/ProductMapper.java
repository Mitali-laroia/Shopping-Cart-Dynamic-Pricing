package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Product;
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
public interface ProductMapper {
    
    @Select("SELECT * FROM shopping_cart.products ORDER BY id")
    List<Product> findAll();
    
    @Select("SELECT * FROM shopping_cart.products WHERE id = #{id}")
    Product findById(@Param("id") Long id);
    
    @Select("SELECT * FROM shopping_cart.products WHERE category = #{category} ORDER BY id")
    List<Product> findByCategory(@Param("category") ProductCategory category);
    
    @Insert("INSERT INTO shopping_cart.products (name, category, price, description, stock_quantity, created_at, updated_at) " +
            "VALUES (#{name}, #{category}, #{price}, #{description}, #{stockQuantity}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);
    
    @Update("UPDATE shopping_cart.products SET name = #{name}, category = #{category}, price = #{price}, " +
            "description = #{description}, stock_quantity = #{stockQuantity}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(Product product);
    
    @Delete("DELETE FROM shopping_cart.products WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
    
    @Update("UPDATE shopping_cart.products SET stock_quantity = #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}