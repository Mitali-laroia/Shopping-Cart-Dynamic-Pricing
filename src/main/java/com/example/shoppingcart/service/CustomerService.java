package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.CustomerMapper;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerMapper customerMapper;

    public List<Customer> getAllCustomers() {
        logger.debug("Fetching all customers");
        return customerMapper.findAll();
    }

    public Customer getCustomerById(Long id) {
        logger.debug("Fetching customer with id: {}", id);
        Customer customer = customerMapper.findById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + id);
        }
        return customer;
    }

    public Customer getCustomerByEmail(String email) {
        logger.debug("Fetching customer with email: {}", email);
        Customer customer = customerMapper.findByEmail(email);
        if (customer == null) {
            throw new RuntimeException("Customer not found with email: " + email);
        }
        return customer;
    }

    public List<Customer> getCustomersByLoyaltyLevel(LoyaltyLevel loyaltyLevel) {
        logger.debug("Fetching customers by loyalty level: {}", loyaltyLevel);
        return customerMapper.findByLoyaltyLevel(loyaltyLevel);
    }

    public Customer createCustomer(Customer customer) {
        logger.debug("Creating new customer: {}", customer.getEmail());
        validateCustomer(customer);
        
        // Check if email already exists
        Customer existingCustomer = customerMapper.findByEmail(customer.getEmail());
        if (existingCustomer != null) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }
        
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        if (customer.getLoyaltyLevel() == null) {
            customer.setLoyaltyLevel(LoyaltyLevel.BRONZE); // Default loyalty level
        }
        
        customerMapper.insert(customer);
        logger.info("Created customer with id: {}", customer.getId());
        return customer;
    }

    public Customer updateCustomer(Long id, Customer customerUpdate) {
        logger.debug("Updating customer with id: {}", id);
        Customer existingCustomer = getCustomerById(id);
        
        // If email is being changed, check it doesn't conflict with another customer
        if (!existingCustomer.getEmail().equals(customerUpdate.getEmail())) {
            Customer customerWithNewEmail = customerMapper.findByEmail(customerUpdate.getEmail());
            if (customerWithNewEmail != null && !customerWithNewEmail.getId().equals(id)) {
                throw new IllegalArgumentException("Email " + customerUpdate.getEmail() + " is already in use by another customer");
            }
        }
        
        existingCustomer.setName(customerUpdate.getName());
        existingCustomer.setEmail(customerUpdate.getEmail());
        existingCustomer.setLoyaltyLevel(customerUpdate.getLoyaltyLevel());
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        
        validateCustomer(existingCustomer);
        customerMapper.update(existingCustomer);
        logger.info("Updated customer with id: {}", id);
        return existingCustomer;
    }

    public void deleteCustomer(Long id) {
        logger.debug("Deleting customer with id: {}", id);
        Customer existingCustomer = getCustomerById(id);
        customerMapper.deleteById(id);
        logger.info("Deleted customer with id: {}", id);
    }

    public Customer updateLoyaltyLevel(Long id, LoyaltyLevel loyaltyLevel) {
        logger.debug("Updating loyalty level for customer id: {} to: {}", id, loyaltyLevel);
        Customer customer = getCustomerById(id);
        customer.setLoyaltyLevel(loyaltyLevel);
        customer.setUpdatedAt(LocalDateTime.now());
        customerMapper.update(customer);
        logger.info("Updated loyalty level for customer id: {} to: {}", id, loyaltyLevel);
        return customer;
    }

    private void validateCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        if (!isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (customer.getLoyaltyLevel() == null) {
            throw new IllegalArgumentException("Loyalty level is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}