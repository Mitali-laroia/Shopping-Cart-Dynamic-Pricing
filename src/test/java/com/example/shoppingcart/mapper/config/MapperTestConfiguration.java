package com.example.shoppingcart.mapper.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@EnableAutoConfiguration
@MapperScan("com.example.shoppingcart.mapper")
@ComponentScan("com.example.shoppingcart")
public class MapperTestConfiguration {
    // Uses Spring Boot auto-configuration for H2 datasource
    // Configuration comes from application-test.properties
}