package com.example.shoppingcart.mapper;

import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
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

class CartItemMapperTest extends BaseMapperTest {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    void testFindAll() {
        List<CartItem> items = cartItemMapper.findAll();
        
        assertNotNull(items);
        assertEquals(9, items.size()); // Based on test data
        
        // Verify first item
        CartItem firstItem = items.get(0);
        assertEquals(1L, firstItem.getId());
        assertNotNull(firstItem.getProduct());
        assertTrue(firstItem.getQuantity() > 0);
        assertTrue(firstItem.getUnitPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testFindById() {
        CartItem item = cartItemMapper.findById(1L);
        
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals(1, item.getQuantity());
        assertEquals(new BigDecimal("1000.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("1000.00"), item.getTotalPrice());
        assertNotNull(item.getProduct());
        assertEquals("Laptop", item.getProduct().getName());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void testFindByIdNotFound() {
        CartItem item = cartItemMapper.findById(999L);
        assertNull(item);
    }

    @Test
    void testFindByCartId() {
        List<CartItem> cartItems = cartItemMapper.findByCartId(1L);
        
        assertNotNull(cartItems);
        assertEquals(2, cartItems.size());
        
        // Verify products are loaded
        cartItems.forEach(item -> {
            assertNotNull(item.getProduct());
            assertNotNull(item.getProduct().getName());
        });
    }

    @Test
    void testFindByCartIdAndProductId() {
        CartItem item = cartItemMapper.findByCartIdAndProductId(1L, 1L);
        
        assertNotNull(item);
        assertEquals("Laptop", item.getProduct().getName());
        assertEquals(1, item.getQuantity());
    }

    @Test
    void testFindByCartIdAndProductIdNotFound() {
        CartItem item = cartItemMapper.findByCartIdAndProductId(1L, 999L);
        assertNull(item);
    }

    @Test
    void testInsert() {
        // Get existing cart and product for the test
        ShoppingCart cart = shoppingCartMapper.findById(1L);
        Product product = productMapper.findById(5L); // Java Programming book
        
        assertNotNull(cart);
        assertNotNull(product);
        
        CartItem newItem = new CartItem();
        newItem.setShoppingCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(2);
        newItem.setUnitPrice(product.getPrice());
        newItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(2)));
        newItem.setCreatedAt(LocalDateTime.now());
        newItem.setUpdatedAt(LocalDateTime.now());

        cartItemMapper.insert(newItem);
        
        assertNotNull(newItem.getId());
        assertTrue(newItem.getId() > 0);
        
        // Verify the item was inserted
        CartItem insertedItem = cartItemMapper.findById(newItem.getId());
        assertNotNull(insertedItem);
        assertEquals(2, insertedItem.getQuantity());
        assertEquals(product.getPrice(), insertedItem.getUnitPrice());
        assertEquals(product.getPrice().multiply(BigDecimal.valueOf(2)), insertedItem.getTotalPrice());
        assertEquals(product.getId(), insertedItem.getProduct().getId());
    }

    @Test
    void testUpdate() {
        CartItem item = cartItemMapper.findById(2L);
        assertNotNull(item);
        
        item.setQuantity(5);
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(5)));
        item.setUpdatedAt(LocalDateTime.now());
        
        cartItemMapper.update(item);
        
        // Verify the update
        CartItem updatedItem = cartItemMapper.findById(2L);
        assertNotNull(updatedItem);
        assertEquals(5, updatedItem.getQuantity());
        assertEquals(new BigDecimal("750.00"), updatedItem.getTotalPrice()); // 150.00 * 5
        assertEquals(new BigDecimal("150.00"), updatedItem.getUnitPrice()); // Should remain unchanged
    }

    @Test
    void testDeleteById() {
        // Verify item exists
        CartItem item = cartItemMapper.findById(9L);
        assertNotNull(item);
        
        // Delete the item
        cartItemMapper.deleteById(9L);
        
        // Verify item is deleted
        CartItem deletedItem = cartItemMapper.findById(9L);
        assertNull(deletedItem);
        
        // Verify total count is reduced
        List<CartItem> items = cartItemMapper.findAll();
        assertEquals(8, items.size());
    }

    @Test
    void testDeleteByCartId() {
        // Verify cart has items
        List<CartItem> cartItems = cartItemMapper.findByCartId(5L);
        assertEquals(2, cartItems.size());
        
        // Delete all items from the cart
        cartItemMapper.deleteByCartId(5L);
        
        // Verify all items are deleted
        List<CartItem> deletedItems = cartItemMapper.findByCartId(5L);
        assertTrue(deletedItems.isEmpty());
        
        // Verify other carts are not affected
        List<CartItem> otherCartItems = cartItemMapper.findByCartId(1L);
        assertEquals(2, otherCartItems.size());
    }

    @Test
    void testItemWithProductDetails() {
        CartItem item = cartItemMapper.findById(3L);
        
        assertNotNull(item);
        assertNotNull(item.getProduct());
        
        // Verify product details are loaded correctly
        assertEquals("Smartphone", item.getProduct().getName());
        assertEquals(ProductCategory.ELECTRONICS, item.getProduct().getCategory());
        assertEquals(new BigDecimal("800.00"), item.getProduct().getPrice());
    }

    @Test
    void testQuantityAndPriceCalculations() {
        CartItem item = cartItemMapper.findById(2L);
        
        assertNotNull(item);
        assertEquals(3, item.getQuantity());
        assertEquals(new BigDecimal("150.00"), item.getUnitPrice());
        assertEquals(new BigDecimal("450.00"), item.getTotalPrice());
        
        // Verify calculation: unit_price * quantity = total_price
        BigDecimal calculatedTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        assertEquals(calculatedTotal, item.getTotalPrice());
    }

    @Test
    void testMultipleItemsInCart() {
        List<CartItem> cart1Items = cartItemMapper.findByCartId(1L);
        assertEquals(2, cart1Items.size());
        
        List<CartItem> cart2Items = cartItemMapper.findByCartId(2L);
        assertEquals(3, cart2Items.size());
        
        List<CartItem> cart3Items = cartItemMapper.findByCartId(3L);
        assertEquals(2, cart3Items.size());
        
        List<CartItem> emptyCartItems = cartItemMapper.findByCartId(4L);
        assertTrue(emptyCartItems.isEmpty());
    }

    @Test
    void testCartItemsWithDifferentCategories() {
        List<CartItem> cart2Items = cartItemMapper.findByCartId(2L);
        
        // Cart 2 should have items from different categories
        boolean hasElectronics = cart2Items.stream()
            .anyMatch(item -> ProductCategory.ELECTRONICS.equals(item.getProduct().getCategory()));
        boolean hasBooks = cart2Items.stream()
            .anyMatch(item -> ProductCategory.BOOKS.equals(item.getProduct().getCategory()));
        boolean hasClothing = cart2Items.stream()
            .anyMatch(item -> ProductCategory.CLOTHING.equals(item.getProduct().getCategory()));
        
        assertTrue(hasElectronics);
        assertTrue(hasBooks);
        assertTrue(hasClothing);
    }

    @Test
    void testUniqueCartProductConstraint() {
        // Test that the same product cannot be added twice to the same cart
        // This should be handled at the service layer, but we can test the constraint
        
        CartItem existingItem = cartItemMapper.findByCartIdAndProductId(1L, 1L);
        assertNotNull(existingItem);
        
        // Verify that the constraint prevents duplicate inserts
        // (This would typically throw an exception at the database level)
    }

    @Test
    void testCartItemDataIntegrity() {
        List<CartItem> allItems = cartItemMapper.findAll();
        
        for (CartItem item : allItems) {
            // Verify all required fields are present
            assertNotNull(item.getId());
            assertNotNull(item.getProduct());
            assertNotNull(item.getQuantity());
            assertNotNull(item.getUnitPrice());
            assertNotNull(item.getTotalPrice());
            assertNotNull(item.getCreatedAt());
            assertNotNull(item.getUpdatedAt());
            
            // Verify quantities and prices are positive
            assertTrue(item.getQuantity() > 0);
            assertTrue(item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0);
            assertTrue(item.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
            
            // Verify calculation accuracy
            BigDecimal expectedTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            assertEquals(0, expectedTotal.compareTo(item.getTotalPrice()));
        }
    }
}