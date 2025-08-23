package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AddItemRequest;
import com.example.shoppingcart.dto.CartCalculationRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.dto.CartResponse;
import com.example.shoppingcart.dto.CreateCartRequest;
import com.example.shoppingcart.mapper.CartItemMapper;
import com.example.shoppingcart.mapper.CustomerMapper;
import com.example.shoppingcart.mapper.ProductMapper;
import com.example.shoppingcart.mapper.ShoppingCartMapper;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ShoppingCartService shoppingCartService;

    private Customer testCustomer;
    private Product testProduct;
    private ShoppingCart testCart;
    private CartItem testCartItem;

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
        testProduct.setDescription("High-performance laptop");
        testProduct.setCategory(ProductCategory.ELECTRONICS);
        testProduct.setPrice(new BigDecimal("1000.00"));
        testProduct.setStockQuantity(50);
        
        testCart = new ShoppingCart();
        testCart.setId(1L);
        testCart.setCustomer(testCustomer);
        testCart.setStatus("ACTIVE");
        testCart.setTotalAmount(new BigDecimal("1000.00"));
        testCart.setCreatedAt(LocalDateTime.now());
        testCart.setUpdatedAt(LocalDateTime.now());
        
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setShoppingCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(1);
        testCartItem.setUnitPrice(new BigDecimal("1000.00"));
        testCartItem.setTotalPrice(new BigDecimal("1000.00"));
        testCartItem.setCreatedAt(LocalDateTime.now());
        testCartItem.setUpdatedAt(LocalDateTime.now());

        testCart.setItems(Arrays.asList(testCartItem));
    }

    @Test
    void testGetAllCarts() {
        List<ShoppingCart> expectedCarts = Arrays.asList(testCart);
        when(shoppingCartMapper.findAll()).thenReturn(expectedCarts);

        List<ShoppingCart> actualCarts = shoppingCartService.getAllCarts();

        assertEquals(expectedCarts, actualCarts);
        verify(shoppingCartMapper, times(1)).findAll();
    }

    @Test
    void testGetCartById_Success() {
        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);

        ShoppingCart actualCart = shoppingCartService.getCartById(1L);

        assertEquals(testCart, actualCart);
        verify(shoppingCartMapper, times(1)).findById(1L);
    }

    @Test
    void testGetCartById_NotFound() {
        when(shoppingCartMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.getCartById(999L));

        assertEquals("Shopping cart not found with id: 999", exception.getMessage());
        verify(shoppingCartMapper, times(1)).findById(999L);
    }

    @Test
    void testGetCartWithItems_Success() {
        when(shoppingCartMapper.findByIdWithItems(1L)).thenReturn(testCart);

        CartResponse response = shoppingCartService.getCartWithItems(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(testCustomer, response.getCustomer());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(new BigDecimal("1000.00"), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getTotalItems());
        verify(shoppingCartMapper, times(1)).findByIdWithItems(1L);
    }

    @Test
    void testGetCartWithItems_NotFound() {
        when(shoppingCartMapper.findByIdWithItems(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.getCartWithItems(999L));

        assertEquals("Shopping cart not found with id: 999", exception.getMessage());
        verify(shoppingCartMapper, times(1)).findByIdWithItems(999L);
    }

    @Test
    void testGetCartsByCustomerId() {
        List<ShoppingCart> expectedCarts = Arrays.asList(testCart);
        when(shoppingCartMapper.findByCustomerId(1L)).thenReturn(expectedCarts);

        List<ShoppingCart> actualCarts = shoppingCartService.getCartsByCustomerId(1L);

        assertEquals(expectedCarts, actualCarts);
        verify(shoppingCartMapper, times(1)).findByCustomerId(1L);
    }

    @Test
    void testGetCartsByStatus() {
        List<ShoppingCart> expectedCarts = Arrays.asList(testCart);
        when(shoppingCartMapper.findByStatus("ACTIVE")).thenReturn(expectedCarts);

        List<ShoppingCart> actualCarts = shoppingCartService.getCartsByStatus("ACTIVE");

        assertEquals(expectedCarts, actualCarts);
        verify(shoppingCartMapper, times(1)).findByStatus("ACTIVE");
    }

    @Test
    void testCreateCart_Success() {
        CreateCartRequest request = new CreateCartRequest();
        request.setCustomerId(1L);

        when(customerMapper.findById(1L)).thenReturn(testCustomer);
        doNothing().when(shoppingCartMapper).insert(any(ShoppingCart.class));

        ShoppingCart createdCart = shoppingCartService.createCart(request);

        assertNotNull(createdCart);
        assertEquals(testCustomer, createdCart.getCustomer());
        assertEquals("ACTIVE", createdCart.getStatus());
        assertEquals(BigDecimal.ZERO, createdCart.getTotalAmount());
        assertNotNull(createdCart.getCreatedAt());
        assertNotNull(createdCart.getUpdatedAt());
        verify(customerMapper, times(1)).findById(1L);
        verify(shoppingCartMapper, times(1)).insert(any(ShoppingCart.class));
    }

    @Test
    void testCreateCart_CustomerNotFound() {
        CreateCartRequest request = new CreateCartRequest();
        request.setCustomerId(999L);

        when(customerMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.createCart(request));

        assertEquals("Customer not found with id: 999", exception.getMessage());
        verify(customerMapper, times(1)).findById(999L);
        verify(shoppingCartMapper, times(0)).insert(any(ShoppingCart.class));
    }

    @Test
    void testAddItemToCart_NewItem() {
        AddItemRequest request = new AddItemRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(cartItemMapper.findByCartIdAndProductId(1L, 1L)).thenReturn(null);
        when(cartItemMapper.findByCartId(1L)).thenReturn(Arrays.asList());
        doNothing().when(cartItemMapper).insert(any(CartItem.class));
        doNothing().when(shoppingCartMapper).update(any(ShoppingCart.class));

        CartItem addedItem = shoppingCartService.addItemToCart(1L, request);

        assertNotNull(addedItem);
        assertEquals(testProduct, addedItem.getProduct());
        assertEquals(2, addedItem.getQuantity());
        assertEquals(testProduct.getPrice(), addedItem.getUnitPrice());
        assertEquals(testProduct.getPrice().multiply(BigDecimal.valueOf(2)), addedItem.getTotalPrice());
        verify(cartItemMapper, times(1)).insert(any(CartItem.class));
        verify(shoppingCartMapper, times(1)).update(any(ShoppingCart.class));
    }

    @Test
    void testAddItemToCart_ExistingItem() {
        AddItemRequest request = new AddItemRequest();
        request.setProductId(1L);
        request.setQuantity(1);

        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        when(productMapper.findById(1L)).thenReturn(testProduct);
        when(cartItemMapper.findByCartIdAndProductId(1L, 1L)).thenReturn(testCartItem);
        when(cartItemMapper.findByCartId(1L)).thenReturn(Arrays.asList(testCartItem));
        doNothing().when(cartItemMapper).update(any(CartItem.class));
        doNothing().when(shoppingCartMapper).update(any(ShoppingCart.class));

        CartItem updatedItem = shoppingCartService.addItemToCart(1L, request);

        assertNotNull(updatedItem);
        assertEquals(2, updatedItem.getQuantity()); // Original 1 + new 1
        verify(cartItemMapper, times(1)).update(any(CartItem.class));
        verify(shoppingCartMapper, times(1)).update(any(ShoppingCart.class));
    }

    @Test
    void testAddItemToCart_InsufficientStock() {
        AddItemRequest request = new AddItemRequest();
        request.setProductId(1L);
        request.setQuantity(100);

        Product lowStockProduct = new Product();
        lowStockProduct.setId(1L);
        lowStockProduct.setStockQuantity(5);

        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        when(productMapper.findById(1L)).thenReturn(lowStockProduct);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> shoppingCartService.addItemToCart(1L, request));

        assertEquals("Insufficient stock. Available: 5", exception.getMessage());
        verify(cartItemMapper, times(0)).insert(any(CartItem.class));
        verify(cartItemMapper, times(0)).update(any(CartItem.class));
    }

    @Test
    void testAddItemToCart_InvalidQuantity() {
        AddItemRequest request = new AddItemRequest();
        request.setProductId(1L);
        request.setQuantity(0);

        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        when(productMapper.findById(1L)).thenReturn(testProduct);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> shoppingCartService.addItemToCart(1L, request));

        assertEquals("Quantity must be greater than 0", exception.getMessage());
        verify(cartItemMapper, times(0)).insert(any(CartItem.class));
    }

    @Test
    void testUpdateCartItem_Success() {
        when(cartItemMapper.findById(1L)).thenReturn(testCartItem);
        when(cartItemMapper.findByCartId(1L)).thenReturn(Arrays.asList(testCartItem));
        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        doNothing().when(cartItemMapper).update(any(CartItem.class));
        doNothing().when(shoppingCartMapper).update(any(ShoppingCart.class));

        CartItem updatedItem = shoppingCartService.updateCartItem(1L, 1L, 3);

        assertEquals(3, updatedItem.getQuantity());
        assertEquals(testProduct.getPrice().multiply(BigDecimal.valueOf(3)), updatedItem.getTotalPrice());
        verify(cartItemMapper, times(1)).update(any(CartItem.class));
        verify(shoppingCartMapper, times(1)).update(any(ShoppingCart.class));
    }

    @Test
    void testUpdateCartItem_NotFound() {
        when(cartItemMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.updateCartItem(1L, 999L, 2));

        assertEquals("Cart item not found with id: 999 in cart: 1", exception.getMessage());
        verify(cartItemMapper, times(1)).findById(999L);
        verify(cartItemMapper, times(0)).update(any(CartItem.class));
    }

    @Test
    void testRemoveItemFromCart_Success() {
        when(cartItemMapper.findById(1L)).thenReturn(testCartItem);
        when(cartItemMapper.findByCartId(1L)).thenReturn(Arrays.asList());
        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        doNothing().when(cartItemMapper).deleteById(1L);
        doNothing().when(shoppingCartMapper).update(any(ShoppingCart.class));

        shoppingCartService.removeItemFromCart(1L, 1L);

        verify(cartItemMapper, times(1)).deleteById(1L);
        verify(shoppingCartMapper, times(1)).update(any(ShoppingCart.class));
    }

    @Test
    void testRemoveItemFromCart_NotFound() {
        when(cartItemMapper.findById(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.removeItemFromCart(1L, 999L));

        assertEquals("Cart item not found with id: 999 in cart: 1", exception.getMessage());
        verify(cartItemMapper, times(1)).findById(999L);
        verify(cartItemMapper, times(0)).deleteById(any());
    }

    @Test
    void testClearCart_Success() {
        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        when(cartItemMapper.findByCartId(1L)).thenReturn(Arrays.asList());
        doNothing().when(cartItemMapper).deleteByCartId(1L);
        doNothing().when(shoppingCartMapper).update(any(ShoppingCart.class));

        shoppingCartService.clearCart(1L);

        verify(cartItemMapper, times(1)).deleteByCartId(1L);
        verify(shoppingCartMapper, times(1)).update(any(ShoppingCart.class));
    }

    @Test
    void testDeleteCart_Success() {
        when(shoppingCartMapper.findById(1L)).thenReturn(testCart);
        doNothing().when(cartItemMapper).deleteByCartId(1L);
        doNothing().when(shoppingCartMapper).deleteById(1L);

        shoppingCartService.deleteCart(1L);

        verify(cartItemMapper, times(1)).deleteByCartId(1L);
        verify(shoppingCartMapper, times(1)).deleteById(1L);
    }

    @Test
    void testCalculateCartPricing_Success() {
        CartCalculationResponse mockResponse = new CartCalculationResponse();
        mockResponse.setFinalTotal(new BigDecimal("950.00"));

        when(shoppingCartMapper.findByIdWithItems(1L)).thenReturn(testCart);
        when(pricingService.calculateCartPricing(any(CartCalculationRequest.class))).thenReturn(mockResponse);

        CartCalculationResponse response = shoppingCartService.calculateCartPricing(1L);

        assertNotNull(response);
        assertEquals(new BigDecimal("950.00"), response.getFinalTotal());
        verify(shoppingCartMapper, times(1)).findByIdWithItems(1L);
        verify(pricingService, times(1)).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void testCalculateCartPricing_EmptyCart() {
        ShoppingCart emptyCart = new ShoppingCart();
        emptyCart.setId(1L);
        emptyCart.setCustomer(testCustomer);
        emptyCart.setItems(Arrays.asList()); // Empty cart

        when(shoppingCartMapper.findByIdWithItems(1L)).thenReturn(emptyCart);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.calculateCartPricing(1L));

        assertEquals("Cart is empty, cannot calculate pricing", exception.getMessage());
        verify(shoppingCartMapper, times(1)).findByIdWithItems(1L);
        verify(pricingService, times(0)).calculateCartPricing(any(CartCalculationRequest.class));
    }

    @Test
    void testCalculateCartPricing_CartNotFound() {
        when(shoppingCartMapper.findByIdWithItems(999L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoppingCartService.calculateCartPricing(999L));

        assertEquals("Shopping cart not found with id: 999", exception.getMessage());
        verify(shoppingCartMapper, times(1)).findByIdWithItems(999L);
        verify(pricingService, times(0)).calculateCartPricing(any(CartCalculationRequest.class));
    }
}