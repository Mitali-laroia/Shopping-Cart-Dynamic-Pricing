package com.example.shoppingcart.controller;

import com.example.shoppingcart.controller.config.TestConfiguration;
import com.example.shoppingcart.dto.AddItemRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.dto.CartResponse;
import com.example.shoppingcart.dto.CreateCartRequest;
import com.example.shoppingcart.dto.UpdateItemRequest;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.LoyaltyLevel;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ProductCategory;
import com.example.shoppingcart.model.ShoppingCart;
import com.example.shoppingcart.service.CartItemService;
import com.example.shoppingcart.service.ShoppingCartService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ShoppingCartController.class)
@ContextConfiguration(classes = {TestConfiguration.class, ShoppingCartController.class})
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @MockBean
    private CartItemService cartItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ShoppingCart sampleCart;
    private CartItem sampleCartItem;
    private CartResponse sampleCartResponse;
    private CreateCartRequest createCartRequest;
    private AddItemRequest addItemRequest;
    private UpdateItemRequest updateItemRequest;
    private CartCalculationResponse calculationResponse;
    private Customer sampleCustomer;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setName("John Doe");
        sampleCustomer.setLoyaltyLevel(LoyaltyLevel.SILVER);

        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Laptop");
        sampleProduct.setCategory(ProductCategory.ELECTRONICS);
        sampleProduct.setPrice(BigDecimal.valueOf(1000.00));

        sampleCart = new ShoppingCart();
        sampleCart.setId(1L);
        sampleCart.setCustomer(sampleCustomer);
        sampleCart.setStatus("ACTIVE");
        sampleCart.setCreatedAt(LocalDateTime.now());

        sampleCartItem = new CartItem();
        sampleCartItem.setId(1L);
        sampleCartItem.setShoppingCart(sampleCart);
        sampleCartItem.setProduct(sampleProduct);
        sampleCartItem.setQuantity(2);
        sampleCartItem.setUnitPrice(BigDecimal.valueOf(1000.00));

        sampleCartResponse = new CartResponse();
        sampleCartResponse.setId(1L);
        sampleCartResponse.setCustomer(sampleCustomer);
        sampleCartResponse.setItems(Arrays.asList(sampleCartItem));

        createCartRequest = new CreateCartRequest();
        createCartRequest.setCustomerId(1L);

        addItemRequest = new AddItemRequest();
        addItemRequest.setProductId(1L);
        addItemRequest.setQuantity(2);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setQuantity(3);

        calculationResponse = new CartCalculationResponse();
        calculationResponse.setSubtotal(BigDecimal.valueOf(2000.00));
        calculationResponse.setTotalTax(BigDecimal.valueOf(200.00));
        calculationResponse.setLoyaltyDiscount(BigDecimal.valueOf(300.00));
        calculationResponse.setFinalTotal(BigDecimal.valueOf(1900.00));
    }

    @Test
    void getAllCarts_ShouldReturnAllCarts() throws Exception {
        List<ShoppingCart> carts = Arrays.asList(sampleCart);
        when(shoppingCartService.getAllCarts()).thenReturn(carts);

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customer.id").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(shoppingCartService, times(1)).getAllCarts();
    }

    @Test
    void getAllCarts_ServiceError_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.getAllCarts()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isInternalServerError());

        verify(shoppingCartService, times(1)).getAllCarts();
    }

    @Test
    void createCart_ValidRequest_ShouldReturnCreatedCart() throws Exception {
        when(shoppingCartService.createCart(any(CreateCartRequest.class))).thenReturn(sampleCart);

        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customer.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(shoppingCartService, times(1)).createCart(any(CreateCartRequest.class));
    }

    @Test
    void createCart_CustomerNotFound_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.createCart(any(CreateCartRequest.class)))
                .thenThrow(new RuntimeException("Customer not found"));

        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartRequest)))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).createCart(any(CreateCartRequest.class));
    }

    @Test
    void createCart_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        when(shoppingCartService.createCart(any(CreateCartRequest.class)))
                .thenThrow(new RuntimeException("Invalid request"));

        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartRequest)))
                .andExpect(status().isBadRequest());

        verify(shoppingCartService, times(1)).createCart(any(CreateCartRequest.class));
    }

    @Test
    void getCartWithItems_ExistingCart_ShouldReturnCartResponse() throws Exception {
        when(shoppingCartService.getCartWithItems(1L)).thenReturn(sampleCartResponse);

        mockMvc.perform(get("/api/v1/cart/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.items.length()").value(1));

        verify(shoppingCartService, times(1)).getCartWithItems(1L);
    }

    @Test
    void getCartWithItems_NonExistingCart_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.getCartWithItems(999L))
                .thenThrow(new RuntimeException("Cart not found"));

        mockMvc.perform(get("/api/v1/cart/999"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).getCartWithItems(999L);
    }

    @Test
    void getCartsByCustomer_ShouldReturnCustomerCarts() throws Exception {
        List<ShoppingCart> carts = Arrays.asList(sampleCart);
        when(shoppingCartService.getCartsByCustomerId(1L)).thenReturn(carts);

        mockMvc.perform(get("/api/v1/cart/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customer.id").value(1));

        verify(shoppingCartService, times(1)).getCartsByCustomerId(1L);
    }

    @Test
    void getCartsByCustomer_ServiceError_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.getCartsByCustomerId(1L))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/cart/customer/1"))
                .andExpect(status().isInternalServerError());

        verify(shoppingCartService, times(1)).getCartsByCustomerId(1L);
    }

    @Test
    void getCartsByStatus_ShouldReturnCartsByStatus() throws Exception {
        List<ShoppingCart> carts = Arrays.asList(sampleCart);
        when(shoppingCartService.getCartsByStatus("ACTIVE")).thenReturn(carts);

        mockMvc.perform(get("/api/v1/cart/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(shoppingCartService, times(1)).getCartsByStatus("ACTIVE");
    }

    @Test
    void addItemToCart_ValidRequest_ShouldReturnCreatedItem() throws Exception {
        when(shoppingCartService.addItemToCart(eq(1L), any(AddItemRequest.class))).thenReturn(sampleCartItem);

        mockMvc.perform(post("/api/v1/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shoppingCart.id").value(1))
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(shoppingCartService, times(1)).addItemToCart(eq(1L), any(AddItemRequest.class));
    }

    @Test
    void addItemToCart_CartNotFound_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.addItemToCart(eq(999L), any(AddItemRequest.class)))
                .thenThrow(new RuntimeException("Cart not found"));

        mockMvc.perform(post("/api/v1/cart/999/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).addItemToCart(eq(999L), any(AddItemRequest.class));
    }

    @Test
    void addItemToCart_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        when(shoppingCartService.addItemToCart(eq(1L), any(AddItemRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid quantity"));

        mockMvc.perform(post("/api/v1/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isBadRequest());

        verify(shoppingCartService, times(1)).addItemToCart(eq(1L), any(AddItemRequest.class));
    }

    @Test
    void addItemToCart_UnexpectedError_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.addItemToCart(eq(1L), any(AddItemRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/v1/cart/1/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isInternalServerError());

        verify(shoppingCartService, times(1)).addItemToCart(eq(1L), any(AddItemRequest.class));
    }

    @Test
    void getCartItems_ShouldReturnCartItems() throws Exception {
        List<CartItem> items = Arrays.asList(sampleCartItem);
        when(cartItemService.getCartItemsByCartId(1L)).thenReturn(items);

        mockMvc.perform(get("/api/v1/cart/1/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].shoppingCart.id").value(1));

        verify(cartItemService, times(1)).getCartItemsByCartId(1L);
    }

    @Test
    void getCartItems_ServiceError_ShouldReturnInternalServerError() throws Exception {
        when(cartItemService.getCartItemsByCartId(1L))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/v1/cart/1/items"))
                .andExpect(status().isInternalServerError());

        verify(cartItemService, times(1)).getCartItemsByCartId(1L);
    }

    @Test
    void updateCartItem_ValidRequest_ShouldReturnUpdatedItem() throws Exception {
        CartItem updatedItem = new CartItem();
        updatedItem.setId(1L);
        updatedItem.setShoppingCart(sampleCart);
        updatedItem.setProduct(sampleProduct);
        updatedItem.setQuantity(3);

        when(shoppingCartService.updateCartItem(1L, 1L, 3)).thenReturn(updatedItem);

        mockMvc.perform(put("/api/v1/cart/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity").value(3));

        verify(shoppingCartService, times(1)).updateCartItem(1L, 1L, 3);
    }

    @Test
    void updateCartItem_ItemNotFound_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.updateCartItem(1L, 999L, 3))
                .thenThrow(new RuntimeException("Cart item not found"));

        mockMvc.perform(put("/api/v1/cart/1/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).updateCartItem(1L, 999L, 3);
    }

    @Test
    void updateCartItem_InvalidQuantity_ShouldReturnBadRequest() throws Exception {
        when(shoppingCartService.updateCartItem(1L, 1L, 3))
                .thenThrow(new IllegalArgumentException("Invalid quantity"));

        mockMvc.perform(put("/api/v1/cart/1/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isBadRequest());

        verify(shoppingCartService, times(1)).updateCartItem(1L, 1L, 3);
    }

    @Test
    void removeItemFromCart_ExistingItem_ShouldReturnSuccess() throws Exception {
        doNothing().when(shoppingCartService).removeItemFromCart(1L, 1L);

        mockMvc.perform(delete("/api/v1/cart/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item removed successfully"))
                .andExpect(jsonPath("$.cartId").value("1"))
                .andExpect(jsonPath("$.itemId").value("1"));

        verify(shoppingCartService, times(1)).removeItemFromCart(1L, 1L);
    }

    @Test
    void removeItemFromCart_ItemNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Cart item not found")).when(shoppingCartService).removeItemFromCart(1L, 999L);

        mockMvc.perform(delete("/api/v1/cart/1/items/999"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).removeItemFromCart(1L, 999L);
    }

    @Test
    void clearCart_ExistingCart_ShouldReturnSuccess() throws Exception {
        doNothing().when(shoppingCartService).clearCart(1L);

        mockMvc.perform(delete("/api/v1/cart/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Cart cleared successfully"))
                .andExpect(jsonPath("$.cartId").value("1"));

        verify(shoppingCartService, times(1)).clearCart(1L);
    }

    @Test
    void clearCart_CartNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Cart not found")).when(shoppingCartService).clearCart(999L);

        mockMvc.perform(delete("/api/v1/cart/999/items"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).clearCart(999L);
    }

    @Test
    void deleteCart_ExistingCart_ShouldReturnNoContent() throws Exception {
        doNothing().when(shoppingCartService).deleteCart(1L);

        mockMvc.perform(delete("/api/v1/cart/1"))
                .andExpect(status().isNoContent());

        verify(shoppingCartService, times(1)).deleteCart(1L);
    }

    @Test
    void deleteCart_CartNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Cart not found")).when(shoppingCartService).deleteCart(999L);

        mockMvc.perform(delete("/api/v1/cart/999"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).deleteCart(999L);
    }

    @Test
    void calculateCartPricing_ExistingCart_ShouldReturnCalculationResponse() throws Exception {
        when(shoppingCartService.calculateCartPricing(1L)).thenReturn(calculationResponse);

        mockMvc.perform(post("/api/v1/cart/1/calculate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subtotal").value(2000.00))
                .andExpect(jsonPath("$.totalTax").value(200.00))
                .andExpect(jsonPath("$.loyaltyDiscount").value(300.00))
                .andExpect(jsonPath("$.finalTotal").value(1900.00));

        verify(shoppingCartService, times(1)).calculateCartPricing(1L);
    }

    @Test
    void calculateCartPricing_CartNotFound_ShouldReturnNotFound() throws Exception {
        when(shoppingCartService.calculateCartPricing(999L))
                .thenThrow(new RuntimeException("Cart not found"));

        mockMvc.perform(post("/api/v1/cart/999/calculate"))
                .andExpect(status().isNotFound());

        verify(shoppingCartService, times(1)).calculateCartPricing(999L);
    }

    @Test
    void calculateCartPricing_EmptyCart_ShouldReturnBadRequest() throws Exception {
        when(shoppingCartService.calculateCartPricing(1L))
                .thenThrow(new RuntimeException("Cart is empty"));

        mockMvc.perform(post("/api/v1/cart/1/calculate"))
                .andExpect(status().isBadRequest());

        verify(shoppingCartService, times(1)).calculateCartPricing(1L);
    }

    @Test
    void calculateCartPricing_ServiceError_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.calculateCartPricing(1L))
                .thenThrow(new RuntimeException("Calculation error"));

        mockMvc.perform(post("/api/v1/cart/1/calculate"))
                .andExpect(status().isInternalServerError());

        verify(shoppingCartService, times(1)).calculateCartPricing(1L);
    }

    @Test
    void handleException_ShouldReturnInternalServerError() throws Exception {
        when(shoppingCartService.getAllCarts()).thenThrow(new OutOfMemoryError("System error"));

        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));

        verify(shoppingCartService, times(1)).getAllCarts();
    }
}