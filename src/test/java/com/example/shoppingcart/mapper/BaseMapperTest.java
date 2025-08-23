package com.example.shoppingcart.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@MybatisTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseMapperTest {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetDatabase() {
        // Reset AUTO_INCREMENT sequences to avoid ID conflicts
        jdbcTemplate.execute("ALTER TABLE shopping_cart.customers ALTER COLUMN id RESTART WITH 100");
        jdbcTemplate.execute("ALTER TABLE shopping_cart.products ALTER COLUMN id RESTART WITH 100"); 
        jdbcTemplate.execute("ALTER TABLE shopping_cart.shopping_carts ALTER COLUMN id RESTART WITH 100");
        jdbcTemplate.execute("ALTER TABLE shopping_cart.cart_items ALTER COLUMN id RESTART WITH 100");
    }
}