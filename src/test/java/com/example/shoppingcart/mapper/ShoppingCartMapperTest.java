package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.ShoppingCart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartMapperTest extends BaseMapperTest {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void testFindAll() {
        List<ShoppingCart> carts = shoppingCartMapper.findAll();
        
        assertNotNull(carts);
        assertEquals(5, carts.size());
        
        // Verify first cart
        ShoppingCart firstCart = carts.get(0);
        assertEquals(1L, firstCart.getId());
        assertEquals("ACTIVE", firstCart.getStatus());
        assertNotNull(firstCart.getCustomer());
        assertEquals("John Doe", firstCart.getCustomer().getName());
    }

    @Test
    void testFindById() {
        ShoppingCart cart = shoppingCartMapper.findById(2L);
        
        assertNotNull(cart);
        assertEquals(2L, cart.getId());
        assertEquals("ACTIVE", cart.getStatus());
        assertNotNull(cart.getCustomer());
        assertEquals("Jane Smith", cart.getCustomer().getName());
        assertEquals("SILVER", cart.getCustomer().getLoyaltyLevel());
        assertNotNull(cart.getCreatedAt());
        assertNotNull(cart.getUpdatedAt());
    }

    @Test
    void testFindByIdNotFound() {
        ShoppingCart cart = shoppingCartMapper.findById(999L);
        assertNull(cart);
    }

    @Test
    void testFindByIdWithItems() {
        ShoppingCart cart = shoppingCartMapper.findByIdWithItems(1L);
        
        assertNotNull(cart);
        assertEquals(1L, cart.getId());
        assertNotNull(cart.getCustomer());
        assertEquals("John Doe", cart.getCustomer().getName());
        assertNotNull(cart.getItems());
        assertEquals(2, cart.getItems().size()); // Based on test data
        
        // Verify items are loaded
        cart.getItems().forEach(item -> {
            assertNotNull(item.getProduct());
            assertNotNull(item.getProduct().getName());
            assertTrue(item.getQuantity() > 0);
            assertTrue(item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @Test
    void testFindByCustomerId() {
        List<ShoppingCart> carts = shoppingCartMapper.findByCustomerId(1L);
        
        assertNotNull(carts);
        assertEquals(1, carts.size());
        assertEquals("John Doe", carts.get(0).getCustomer().getName());
        
        // Test customer with multiple carts - Charlie Wilson has one cart
        List<ShoppingCart> charliesCarts = shoppingCartMapper.findByCustomerId(5L);
        assertEquals(1, charliesCarts.size());
        assertEquals("ABANDONED", charliesCarts.get(0).getStatus());
    }

    @Test
    void testFindByStatus() {
        List<ShoppingCart> activeCarts = shoppingCartMapper.findByStatus("ACTIVE");
        
        assertNotNull(activeCarts);
        assertEquals(3, activeCarts.size());
        assertTrue(activeCarts.stream().allMatch(cart -> "ACTIVE".equals(cart.getStatus())));
        
        List<ShoppingCart> completedCarts = shoppingCartMapper.findByStatus("COMPLETED");
        assertEquals(1, completedCarts.size());
        assertEquals("Bob Johnson", completedCarts.get(0).getCustomer().getName());
        
        List<ShoppingCart> abandonedCarts = shoppingCartMapper.findByStatus("ABANDONED");
        assertEquals(1, abandonedCarts.size());
        assertEquals("Charlie Wilson", abandonedCarts.get(0).getCustomer().getName());
    }

    @Test
    void testInsert() {
        Customer customer = customerMapper.findById(1L);
        assertNotNull(customer);
        
        ShoppingCart newCart = new ShoppingCart();
        newCart.setCustomer(customer);
        newCart.setStatus("ACTIVE");
        newCart.setTotalAmount(BigDecimal.ZERO);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());

        shoppingCartMapper.insert(newCart);
        
        assertNotNull(newCart.getId());
        assertTrue(newCart.getId() > 0);
        
        // Verify the cart was inserted
        ShoppingCart insertedCart = shoppingCartMapper.findById(newCart.getId());
        assertNotNull(insertedCart);
        assertEquals("ACTIVE", insertedCart.getStatus());
        assertEquals(BigDecimal.ZERO, insertedCart.getTotalAmount());
        assertEquals(customer.getId(), insertedCart.getCustomer().getId());
    }

    @Test
    void testUpdate() {
        ShoppingCart cart = shoppingCartMapper.findById(1L);
        assertNotNull(cart);
        
        cart.setStatus("COMPLETED");
        cart.setTotalAmount(new BigDecimal("2500.00"));
        cart.setUpdatedAt(LocalDateTime.now());
        
        shoppingCartMapper.update(cart);
        
        // Verify the update
        ShoppingCart updatedCart = shoppingCartMapper.findById(1L);
        assertNotNull(updatedCart);
        assertEquals("COMPLETED", updatedCart.getStatus());
        assertEquals(new BigDecimal("2500.00"), updatedCart.getTotalAmount());
        assertEquals(1L, updatedCart.getCustomer().getId()); // Customer should remain unchanged
    }

    @Test
    void testDeleteById() {
        // Verify cart exists
        ShoppingCart cart = shoppingCartMapper.findById(4L);
        assertNotNull(cart);
        
        // Delete the cart
        shoppingCartMapper.deleteById(4L);
        
        // Verify cart is deleted
        ShoppingCart deletedCart = shoppingCartMapper.findById(4L);
        assertNull(deletedCart);
        
        // Verify total count is reduced
        List<ShoppingCart> carts = shoppingCartMapper.findAll();
        assertEquals(4, carts.size());
    }

    @Test
    void testFindActiveCartsByCustomer() {
        // Test finding active carts for a specific customer
        List<ShoppingCart> activeCarts = shoppingCartMapper.findByStatus("ACTIVE");
        List<ShoppingCart> customerActiveCarts = activeCarts.stream()
            .filter(cart -> cart.getCustomer().getId().equals(1L))
            .toList();
        
        assertEquals(1, customerActiveCarts.size());
        assertEquals("ACTIVE", customerActiveCarts.get(0).getStatus());
        assertEquals("John Doe", customerActiveCarts.get(0).getCustomer().getName());
    }

    @Test
    void testFindCartsByCustomerAndStatus() {
        // This would be a complex query combining customer and status filtering
        // For now, we can test it by combining the existing methods
        List<ShoppingCart> customerCarts = shoppingCartMapper.findByCustomerId(3L);
        List<ShoppingCart> completedCustomerCarts = customerCarts.stream()
            .filter(cart -> "COMPLETED".equals(cart.getStatus()))
            .toList();
        
        assertEquals(1, completedCustomerCarts.size());
        assertEquals("Bob Johnson", completedCustomerCarts.get(0).getCustomer().getName());
        assertEquals("COMPLETED", completedCustomerCarts.get(0).getStatus());
    }

    @Test
    void testCartWithCustomerDetails() {
        ShoppingCart cart = shoppingCartMapper.findById(2L);
        
        assertNotNull(cart);
        assertNotNull(cart.getCustomer());
        assertEquals("Jane Smith", cart.getCustomer().getName());
        assertEquals("jane.smith@example.com", cart.getCustomer().getEmail());
        assertEquals("SILVER", cart.getCustomer().getLoyaltyLevel());
    }

    @Test
    void testCartTotalAmountPrecision() {
        ShoppingCart cart = shoppingCartMapper.findById(2L);
        
        assertNotNull(cart);
        assertNotNull(cart.getTotalAmount());
        assertEquals(new BigDecimal("966.98"), cart.getTotalAmount());
    }
}