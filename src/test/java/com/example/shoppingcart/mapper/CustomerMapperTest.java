package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerMapperTest extends BaseMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void testFindAll() {
        List<Customer> customers = customerMapper.findAll();
        
        assertNotNull(customers);
        assertEquals(5, customers.size());
        
        // Verify first customer
        Customer firstCustomer = customers.get(0);
        assertEquals(1L, firstCustomer.getId());
        assertEquals("John Doe", firstCustomer.getName());
        assertEquals("john.doe@example.com", firstCustomer.getEmail());
        assertEquals(LoyaltyLevel.BRONZE, firstCustomer.getLoyaltyLevel());
    }

    @Test
    void testFindById() {
        Customer customer = customerMapper.findById(2L);
        
        assertNotNull(customer);
        assertEquals(2L, customer.getId());
        assertEquals("Jane Smith", customer.getName());
        assertEquals("jane.smith@example.com", customer.getEmail());
        assertEquals(LoyaltyLevel.SILVER, customer.getLoyaltyLevel());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
    }

    @Test
    void testFindByIdNotFound() {
        Customer customer = customerMapper.findById(999L);
        assertNull(customer);
    }

    @Test
    void testFindByEmail() {
        Customer customer = customerMapper.findByEmail("bob.johnson@example.com");
        
        assertNotNull(customer);
        assertEquals(3L, customer.getId());
        assertEquals("Bob Johnson", customer.getName());
        assertEquals(LoyaltyLevel.GOLD, customer.getLoyaltyLevel());
    }

    @Test
    void testFindByEmailNotFound() {
        Customer customer = customerMapper.findByEmail("notfound@example.com");
        assertNull(customer);
    }

    @Test
    void testFindByLoyaltyLevel() {
        List<Customer> bronzeCustomers = customerMapper.findByLoyaltyLevel(LoyaltyLevel.BRONZE);
        
        assertNotNull(bronzeCustomers);
        assertEquals(2, bronzeCustomers.size());
        assertTrue(bronzeCustomers.stream().allMatch(c -> LoyaltyLevel.BRONZE.equals(c.getLoyaltyLevel())));
        
        List<Customer> goldCustomers = customerMapper.findByLoyaltyLevel(LoyaltyLevel.GOLD);
        assertEquals(1, goldCustomers.size());
        assertEquals("Bob Johnson", goldCustomers.get(0).getName());
    }

    @Test
    void testInsert() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Test Customer");
        newCustomer.setEmail("test@example.com");
        newCustomer.setLoyaltyLevel(LoyaltyLevel.SILVER);
        newCustomer.setCreatedAt(LocalDateTime.now());
        newCustomer.setUpdatedAt(LocalDateTime.now());

        customerMapper.insert(newCustomer);
        
        assertNotNull(newCustomer.getId());
        assertTrue(newCustomer.getId() > 0);
        
        // Verify the customer was inserted
        Customer insertedCustomer = customerMapper.findById(newCustomer.getId());
        assertNotNull(insertedCustomer);
        assertEquals("Test Customer", insertedCustomer.getName());
        assertEquals("test@example.com", insertedCustomer.getEmail());
        assertEquals(LoyaltyLevel.SILVER, insertedCustomer.getLoyaltyLevel());
    }

    @Test
    void testUpdate() {
        Customer customer = customerMapper.findById(1L);
        assertNotNull(customer);
        
        customer.setName("Updated Name");
        customer.setLoyaltyLevel(LoyaltyLevel.GOLD);
        customer.setUpdatedAt(LocalDateTime.now());
        
        customerMapper.update(customer);
        
        // Verify the update
        Customer updatedCustomer = customerMapper.findById(1L);
        assertNotNull(updatedCustomer);
        assertEquals("Updated Name", updatedCustomer.getName());
        assertEquals(LoyaltyLevel.GOLD, updatedCustomer.getLoyaltyLevel());
        assertEquals("john.doe@example.com", updatedCustomer.getEmail()); // Should remain unchanged
    }

    @Test
    void testDeleteById() {
        // Insert a test customer that has no dependencies
        Customer testCustomer = new Customer();
        testCustomer.setName("Delete Test Customer");
        testCustomer.setEmail("delete@example.com");
        testCustomer.setLoyaltyLevel(LoyaltyLevel.BRONZE);
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());
        
        customerMapper.insert(testCustomer);
        Long testId = testCustomer.getId();
        
        // Verify customer exists
        Customer customer = customerMapper.findById(testId);
        assertNotNull(customer);
        
        // Delete the customer
        customerMapper.deleteById(testId);
        
        // Verify customer is deleted
        Customer deletedCustomer = customerMapper.findById(testId);
        assertNull(deletedCustomer);
        
        // Verify total count remains the same since we added and then deleted
        List<Customer> customers = customerMapper.findAll();
        assertEquals(5, customers.size());
    }

    @Test
    void testExistsByEmailSimulation() {
        // Simulate existsByEmail using findByEmail
        Customer exists = customerMapper.findByEmail("john.doe@example.com");
        assertNotNull(exists);
        
        Customer notExists = customerMapper.findByEmail("nonexistent@example.com");
        assertNull(notExists);
    }
}