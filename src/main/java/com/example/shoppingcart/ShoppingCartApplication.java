package com.example.shoppingcart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.shoppingcart.mapper")
public class ShoppingCartApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }

}