package com.example.shoppingcart.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.example.shoppingcart.controller",
               excludeFilters = {
                   @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.example.shoppingcart.mapper.*"),
                   @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.example.shoppingcart.service.*")
               })
public class BaseControllerTest {
}