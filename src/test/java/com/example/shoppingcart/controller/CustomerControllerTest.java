package com.example.shoppingcart.controller;

import com.example.shoppingcart.controller.config.TestConfiguration;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = {TestConfiguration.class, CustomerController.class})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer sampleCustomer;
    private Customer sampleCustomer2;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setName("John Doe");
        sampleCustomer.setEmail("john@example.com");
        sampleCustomer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        sampleCustomer2 = new Customer();
        sampleCustomer2.setId(2L);
        sampleCustomer2.setName("Jane Smith");
        sampleCustomer2.setEmail("jane@example.com");
        sampleCustomer2.setLoyaltyLevel(LoyaltyLevel.GOLD);
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(sampleCustomer, sampleCustomer2);
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void getCustomerById_ExistingCustomer_ShouldReturnCustomer() throws Exception {
        when(customerService.getCustomerById(1L)).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.loyaltyLevel").value("SILVER"));

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    void getCustomerById_NonExistingCustomer_ShouldReturnNotFound() throws Exception {
        when(customerService.getCustomerById(999L)).thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(999L);
    }

    @Test
    void getCustomerByEmail_ExistingEmail_ShouldReturnCustomer() throws Exception {
        when(customerService.getCustomerByEmail("john@example.com")).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/v1/customers/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService, times(1)).getCustomerByEmail("john@example.com");
    }

    @Test
    void getCustomerByEmail_NonExistingEmail_ShouldReturnNotFound() throws Exception {
        when(customerService.getCustomerByEmail("nonexistent@example.com"))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerByEmail("nonexistent@example.com");
    }

    @Test
    void getCustomersByLoyaltyLevel_ShouldReturnCustomersOfLoyaltyLevel() throws Exception {
        List<Customer> goldCustomers = Arrays.asList(sampleCustomer2);
        when(customerService.getCustomersByLoyaltyLevel(LoyaltyLevel.GOLD)).thenReturn(goldCustomers);

        mockMvc.perform(get("/api/v1/customers/loyalty/GOLD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].loyaltyLevel").value("GOLD"));

        verify(customerService, times(1)).getCustomersByLoyaltyLevel(LoyaltyLevel.GOLD);
    }

    @Test
    void createCustomer_ValidCustomer_ShouldReturnCreatedCustomer() throws Exception {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void createCustomer_InvalidCustomer_ShouldReturnBadRequest() throws Exception {
        when(customerService.createCustomer(any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Invalid customer"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer_ExistingCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void updateCustomer_NonExistingCustomer_ShouldReturnNotFound() throws Exception {
        when(customerService.updateCustomer(eq(999L), any(Customer.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(eq(999L), any(Customer.class));
    }

    @Test
    void updateCustomer_InvalidData_ShouldReturnBadRequest() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(Customer.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCustomer)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void deleteCustomer_ExistingCustomer_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer(1L);
    }

    @Test
    void deleteCustomer_NonExistingCustomer_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Customer not found")).when(customerService).deleteCustomer(999L);

        mockMvc.perform(delete("/api/v1/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).deleteCustomer(999L);
    }

    @Test
    void updateLoyaltyLevel_ValidRequest_ShouldReturnUpdatedCustomer() throws Exception {
        Map<String, LoyaltyLevel> loyaltyUpdate = new HashMap<>();
        loyaltyUpdate.put("loyaltyLevel", LoyaltyLevel.GOLD);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("John Doe");
        updatedCustomer.setEmail("john@example.com");
        updatedCustomer.setLoyaltyLevel(LoyaltyLevel.GOLD);

        when(customerService.updateLoyaltyLevel(1L, LoyaltyLevel.GOLD)).thenReturn(updatedCustomer);

        mockMvc.perform(patch("/api/v1/customers/1/loyalty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loyaltyUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.loyaltyLevel").value("GOLD"));

        verify(customerService, times(1)).updateLoyaltyLevel(1L, LoyaltyLevel.GOLD);
    }

    @Test
    void updateLoyaltyLevel_MissingLoyaltyLevel_ShouldReturnBadRequest() throws Exception {
        Map<String, Object> loyaltyUpdate = new HashMap<>();

        mockMvc.perform(patch("/api/v1/customers/1/loyalty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loyaltyUpdate)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).updateLoyaltyLevel(anyLong(), any(LoyaltyLevel.class));
    }

    @Test
    void updateLoyaltyLevel_NonExistingCustomer_ShouldReturnNotFound() throws Exception {
        Map<String, LoyaltyLevel> loyaltyUpdate = new HashMap<>();
        loyaltyUpdate.put("loyaltyLevel", LoyaltyLevel.GOLD);

        when(customerService.updateLoyaltyLevel(999L, LoyaltyLevel.GOLD))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(patch("/api/v1/customers/999/loyalty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loyaltyUpdate)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateLoyaltyLevel(999L, LoyaltyLevel.GOLD);
    }

    @Test
    void updateLoyaltyLevel_InvalidLoyaltyData_ShouldReturnBadRequest() throws Exception {
        Map<String, LoyaltyLevel> loyaltyUpdate = new HashMap<>();
        loyaltyUpdate.put("loyaltyLevel", LoyaltyLevel.GOLD);

        when(customerService.updateLoyaltyLevel(1L, LoyaltyLevel.GOLD))
                .thenThrow(new RuntimeException("Invalid loyalty level data"));

        mockMvc.perform(patch("/api/v1/customers/1/loyalty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loyaltyUpdate)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).updateLoyaltyLevel(1L, LoyaltyLevel.GOLD);
    }

    @Test
    void handleException_ShouldReturnInternalServerError() throws Exception {
        when(customerService.getAllCustomers()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));

        verify(customerService, times(1)).getAllCustomers();
    }
}