package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.CustomerMapper;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setLoyaltyLevel(LoyaltyLevel.BRONZE);
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerMapper.findAll()).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getAllCustomers();

        assertEquals(expectedCustomers, actualCustomers);
        verify(customerMapper, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);

        Customer actualCustomer = customerService.getCustomerById(1L);

        assertEquals(testCustomer, actualCustomer);
        verify(customerMapper, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerById(999L));

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerMapper, times(1)).findById(999L);
    }

    @Test
    void testGetCustomerByEmail_Success() {
        when(customerMapper.findByEmail("john.doe@example.com")).thenReturn(testCustomer);

        Customer actualCustomer = customerService.getCustomerByEmail("john.doe@example.com");

        assertEquals(testCustomer, actualCustomer);
        verify(customerMapper, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    void testGetCustomerByEmail_NotFound() {
        when(customerMapper.findByEmail("nonexistent@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.getCustomerByEmail("nonexistent@example.com"));

        assertEquals("Customer not found with email: nonexistent@example.com", exception.getMessage());
        verify(customerMapper, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testGetCustomersByLoyaltyLevel() {
        List<Customer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerMapper.findByLoyaltyLevel(LoyaltyLevel.BRONZE)).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getCustomersByLoyaltyLevel(LoyaltyLevel.BRONZE);

        assertEquals(expectedCustomers, actualCustomers);
        verify(customerMapper, times(1)).findByLoyaltyLevel(LoyaltyLevel.BRONZE);
    }

    @Test
    void testCreateCustomer_Success() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("jane.smith@example.com");
        newCustomer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        when(customerMapper.findByEmail("jane.smith@example.com")).thenReturn(null);
        doNothing().when(customerMapper).insert(any(Customer.class));

        Customer createdCustomer = customerService.createCustomer(newCustomer);

        assertNotNull(createdCustomer);
        assertEquals("Jane Smith", createdCustomer.getName());
        assertEquals("jane.smith@example.com", createdCustomer.getEmail());
        assertEquals(LoyaltyLevel.SILVER, createdCustomer.getLoyaltyLevel());
        assertNotNull(createdCustomer.getCreatedAt());
        assertNotNull(createdCustomer.getUpdatedAt());
        verify(customerMapper, times(1)).findByEmail("jane.smith@example.com");
        verify(customerMapper, times(1)).insert(any(Customer.class));
    }

    @Test
    void testCreateCustomer_WithDefaultLoyaltyLevel() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Test User");
        newCustomer.setEmail("test@example.com");

        when(customerMapper.findByEmail("test@example.com")).thenReturn(null);
        doNothing().when(customerMapper).insert(any(Customer.class));

        Customer createdCustomer = customerService.createCustomer(newCustomer);

        assertEquals(LoyaltyLevel.BRONZE, createdCustomer.getLoyaltyLevel());
        verify(customerMapper, times(1)).insert(any(Customer.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane Smith");
        newCustomer.setEmail("john.doe@example.com");
        newCustomer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        when(customerMapper.findByEmail("john.doe@example.com")).thenReturn(testCustomer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.createCustomer(newCustomer));

        assertEquals("Customer with email john.doe@example.com already exists", exception.getMessage());
        verify(customerMapper, times(1)).findByEmail("john.doe@example.com");
        verify(customerMapper, times(0)).insert(any(Customer.class));
    }

    @Test
    void testCreateCustomer_InvalidEmail() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Test User");
        newCustomer.setEmail("invalid-email");
        newCustomer.setLoyaltyLevel(LoyaltyLevel.BRONZE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.createCustomer(newCustomer));

        assertEquals("Invalid email format", exception.getMessage());
        verify(customerMapper, times(0)).findByEmail(any());
        verify(customerMapper, times(0)).insert(any(Customer.class));
    }

    @Test
    void testCreateCustomer_MissingName() {
        Customer newCustomer = new Customer();
        newCustomer.setEmail("test@example.com");
        newCustomer.setLoyaltyLevel(LoyaltyLevel.BRONZE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.createCustomer(newCustomer));

        assertEquals("Customer name is required", exception.getMessage());
    }

    @Test
    void testUpdateCustomer_Success() {
        Customer updateData = new Customer();
        updateData.setName("John Updated");
        updateData.setEmail("john.updated@example.com");
        updateData.setLoyaltyLevel(LoyaltyLevel.GOLD);

        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.findByEmail("john.updated@example.com")).thenReturn(null);
        doNothing().when(customerMapper).update(any(Customer.class));

        Customer updatedCustomer = customerService.updateCustomer(1L, updateData);

        assertEquals("John Updated", updatedCustomer.getName());
        assertEquals("john.updated@example.com", updatedCustomer.getEmail());
        assertEquals(LoyaltyLevel.GOLD, updatedCustomer.getLoyaltyLevel());
        assertNotNull(updatedCustomer.getUpdatedAt());
        verify(customerMapper, times(1)).findById(1L);
        verify(customerMapper, times(1)).findByEmail("john.updated@example.com");
        verify(customerMapper, times(1)).update(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_EmailConflict() {
        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setEmail("another@example.com");

        Customer updateData = new Customer();
        updateData.setName("John Updated");
        updateData.setEmail("another@example.com");
        updateData.setLoyaltyLevel(LoyaltyLevel.GOLD);

        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        when(customerMapper.findByEmail("another@example.com")).thenReturn(anotherCustomer);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> customerService.updateCustomer(1L, updateData));

        assertEquals("Email another@example.com is already in use by another customer", exception.getMessage());
        verify(customerMapper, times(1)).findById(1L);
        verify(customerMapper, times(1)).findByEmail("another@example.com");
        verify(customerMapper, times(0)).update(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        doNothing().when(customerMapper).deleteById(1L);

        customerService.deleteCustomer(1L);

        verify(customerMapper, times(1)).findById(1L);
        verify(customerMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        when(customerMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> customerService.deleteCustomer(999L));

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerMapper, times(1)).findById(999L);
        verify(customerMapper, times(0)).deleteById(any());
    }

    @Test
    void testUpdateLoyaltyLevel_Success() {
        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        doNothing().when(customerMapper).update(any(Customer.class));

        Customer updatedCustomer = customerService.updateLoyaltyLevel(1L, LoyaltyLevel.GOLD);

        assertEquals(LoyaltyLevel.GOLD, updatedCustomer.getLoyaltyLevel());
        assertNotNull(updatedCustomer.getUpdatedAt());
        verify(customerMapper, times(1)).findById(1L);
        verify(customerMapper, times(1)).update(any(Customer.class));
    }
}