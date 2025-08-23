package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.CartItemMapper;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import com.example.shoppingcart.model.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartItemServiceTest {

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartItemService cartItemService;

    private CartItem testCartItem;
    private ShoppingCart testCart;
    private Product testProduct;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setLoyaltyLevel(LoyaltyLevel.BRONZE);
        
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setCategory(ProductCategory.ELECTRONICS);
        testProduct.setPrice(new BigDecimal("1000.00"));
        testProduct.setStockQuantity(50);
        
        testCart = new ShoppingCart();
        testCart.setId(1L);
        testCart.setCustomer(testCustomer);
        testCart.setStatus("ACTIVE");
        testCart.setTotalAmount(new BigDecimal("1000.00"));
        
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setShoppingCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        testCartItem.setUnitPrice(new BigDecimal("1000.00"));
        testCartItem.setTotalPrice(new BigDecimal("1000.00"));
        testCartItem.setCreatedAt(LocalDateTime.now());
        testCartItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllCartItems() {
        List<CartItem> expectedItems = Arrays.asList(testCartItem);
        when(cartItemMapper.findAll()).thenReturn(expectedItems);

        List<CartItem> actualItems = cartItemService.getAllCartItems();

        assertEquals(expectedItems, actualItems);
        verify(cartItemMapper, times(1)).findAll();
    }

    @Test
    void testGetCartItemById_Success() {
        when(cartItemMapper.findById(1L)).thenReturn(testCartItem);

        CartItem actualItem = cartItemService.getCartItemById(1L);

        assertEquals(testCartItem, actualItem);
        verify(cartItemMapper, times(1)).findById(1L);
    }

    @Test
    void testGetCartItemById_NotFound() {
        when(cartItemMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartItemService.getCartItemById(999L));

        assertEquals("Cart item not found with id: 999", exception.getMessage());
        verify(cartItemMapper, times(1)).findById(999L);
    }

    @Test
    void testGetCartItemsByCartId() {
        List<CartItem> expectedItems = Arrays.asList(testCartItem);
        when(cartItemMapper.findByCartId(1L)).thenReturn(expectedItems);

        List<CartItem> actualItems = cartItemService.getCartItemsByCartId(1L);

        assertEquals(expectedItems, actualItems);
        verify(cartItemMapper, times(1)).findByCartId(1L);
    }

    @Test
    void testGetCartItemByCartAndProduct_Success() {
        when(cartItemMapper.findByCartIdAndProductId(1L, 1L)).thenReturn(testCartItem);

        CartItem actualItem = cartItemService.getCartItemByCartAndProduct(1L, 1L);

        assertEquals(testCartItem, actualItem);
        verify(cartItemMapper, times(1)).findByCartIdAndProductId(1L, 1L);
    }

    @Test
    void testGetCartItemByCartAndProduct_NotFound() {
        when(cartItemMapper.findByCartIdAndProductId(1L, 999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartItemService.getCartItemByCartAndProduct(1L, 999L));

        assertEquals("Cart item not found for cart: 1 and product: 999", exception.getMessage());
        verify(cartItemMapper, times(1)).findByCartIdAndProductId(1L, 999L);
    }

    @Test
    void testDeleteCartItem_Success() {
        when(cartItemMapper.findById(1L)).thenReturn(testCartItem);
        doNothing().when(cartItemMapper).deleteById(1L);

        cartItemService.deleteCartItem(1L);

        verify(cartItemMapper, times(1)).findById(1L);
        verify(cartItemMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCartItem_NotFound() {
        when(cartItemMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> cartItemService.deleteCartItem(999L));

        assertEquals("Cart item not found with id: 999", exception.getMessage());
        verify(cartItemMapper, times(1)).findById(999L);
        verify(cartItemMapper, times(0)).deleteById(999L);
    }

    @Test
    void testDeleteCartItemsByCartId() {
        doNothing().when(cartItemMapper).deleteByCartId(1L);

        cartItemService.deleteCartItemsByCartId(1L);

        verify(cartItemMapper, times(1)).deleteByCartId(1L);
    }
}