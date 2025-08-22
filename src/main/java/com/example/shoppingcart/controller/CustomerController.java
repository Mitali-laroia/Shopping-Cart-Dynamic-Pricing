package com.example.shoppingcart.controller;

import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        logger.debug("GET /api/v1/customers - Get all customers");
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        logger.debug("GET /api/v1/customers/{} - Get customer by id", id);
        try {
            Customer customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            logger.warn("Customer not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) {
        logger.debug("GET /api/v1/customers/email/{} - Get customer by email", email);
        try {
            Customer customer = customerService.getCustomerByEmail(email);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            logger.warn("Customer not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/loyalty/{loyaltyLevel}")
    public ResponseEntity<List<Customer>> getCustomersByLoyaltyLevel(@PathVariable LoyaltyLevel loyaltyLevel) {
        logger.debug("GET /api/v1/customers/loyalty/{} - Get customers by loyalty level", loyaltyLevel);
        List<Customer> customers = customerService.getCustomersByLoyaltyLevel(loyaltyLevel);
        return ResponseEntity.ok(customers);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        logger.debug("POST /api/v1/customers - Create new customer");
        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid customer data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        logger.debug("PUT /api/v1/customers/{} - Update customer", id);
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Customer not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.warn("Invalid customer data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        logger.debug("DELETE /api/v1/customers/{} - Delete customer", id);
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Customer not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/loyalty")
    public ResponseEntity<Customer> updateLoyaltyLevel(@PathVariable Long id, @RequestBody Map<String, LoyaltyLevel> loyaltyUpdate) {
        logger.debug("PATCH /api/v1/customers/{}/loyalty - Update loyalty level", id);
        try {
            LoyaltyLevel loyaltyLevel = loyaltyUpdate.get("loyaltyLevel");
            if (loyaltyLevel == null) {
                return ResponseEntity.badRequest().build();
            }
            Customer updatedCustomer = customerService.updateLoyaltyLevel(id, loyaltyLevel);
            return ResponseEntity.ok(updatedCustomer);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Customer not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
            logger.warn("Invalid loyalty level data: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error in CustomerController: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", e.getMessage()));
    }
}