package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.AddItemRequest;
import com.example.shoppingcart.dto.CartResponse;
import com.example.shoppingcart.dto.CreateCartRequest;
import com.example.shoppingcart.dto.UpdateItemRequest;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.ShoppingCart;
import com.example.shoppingcart.service.CartItemService;
import com.example.shoppingcart.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
public class ShoppingCartController {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<ShoppingCart>> getAllCarts() {
        logger.debug("GET /api/v1/cart - Get all carts");
        try {
            List<ShoppingCart> carts = shoppingCartService.getAllCarts();
            return ResponseEntity.ok(carts);
        } catch (RuntimeException e) {
            logger.error("Error retrieving all carts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<ShoppingCart> createCart(@RequestBody CreateCartRequest request) {
        logger.debug("POST /api/v1/cart - Create new cart for customer: {}", request.getCustomerId());
        try {
            ShoppingCart cart = shoppingCartService.createCart(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cart);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Customer not found: {}", request.getCustomerId());
                return ResponseEntity.notFound().build();
            }
            logger.warn("Invalid cart creation request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartWithItems(@PathVariable Long cartId) {
        logger.debug("GET /api/v1/cart/{} - Get cart details with items", cartId);
        try {
            CartResponse cartResponse = shoppingCartService.getCartWithItems(cartId);
            return ResponseEntity.ok(cartResponse);
        } catch (RuntimeException e) {
            logger.warn("Cart not found with id: {}", cartId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ShoppingCart>> getCartsByCustomer(@PathVariable Long customerId) {
        logger.debug("GET /api/v1/cart/customer/{} - Get carts by customer", customerId);
        try {
            List<ShoppingCart> carts = shoppingCartService.getCartsByCustomerId(customerId);
            return ResponseEntity.ok(carts);
        } catch (RuntimeException e) {
            logger.error("Error retrieving carts for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ShoppingCart>> getCartsByStatus(@PathVariable String status) {
        logger.debug("GET /api/v1/cart/status/{} - Get carts by status", status);
        try {
            List<ShoppingCart> carts = shoppingCartService.getCartsByStatus(status);
            return ResponseEntity.ok(carts);
        } catch (RuntimeException e) {
            logger.error("Error retrieving carts by status {}: {}", status, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItem> addItemToCart(@PathVariable Long cartId, @RequestBody AddItemRequest request) {
        logger.debug("POST /api/v1/cart/{}/items - Add item to cart", cartId);
        try {
            CartItem cartItem = shoppingCartService.addItemToCart(cartId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Cart or product not found: {}", e.getMessage());
                return ResponseEntity.notFound().build();
            }
            if (e instanceof IllegalArgumentException) {
                logger.warn("Invalid add item request: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
            logger.error("Unexpected error adding item to cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long cartId) {
        logger.debug("GET /api/v1/cart/{}/items - Get cart items", cartId);
        try {
            List<CartItem> items = cartItemService.getCartItemsByCartId(cartId);
            return ResponseEntity.ok(items);
        } catch (RuntimeException e) {
            logger.error("Error retrieving cart items for cart {}: {}", cartId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long cartId, @PathVariable Long itemId, 
                                                  @RequestBody UpdateItemRequest request) {
        logger.debug("PUT /api/v1/cart/{}/items/{} - Update cart item", cartId, itemId);
        try {
            CartItem cartItem = shoppingCartService.updateCartItem(cartId, itemId, request.getQuantity());
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Cart item not found: {}", e.getMessage());
                return ResponseEntity.notFound().build();
            }
            if (e instanceof IllegalArgumentException) {
                logger.warn("Invalid update item request: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
            logger.error("Unexpected error updating cart item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<Map<String, String>> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        logger.debug("DELETE /api/v1/cart/{}/items/{} - Remove item from cart", cartId, itemId);
        try {
            shoppingCartService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(Map.of("message", "Item removed successfully", "cartId", cartId.toString(), "itemId", itemId.toString()));
        } catch (RuntimeException e) {
            logger.warn("Cart item not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable Long cartId) {
        logger.debug("DELETE /api/v1/cart/{}/items - Clear all items from cart", cartId);
        try {
            shoppingCartService.clearCart(cartId);
            return ResponseEntity.ok(Map.of("message", "Cart cleared successfully", "cartId", cartId.toString()));
        } catch (RuntimeException e) {
            logger.warn("Cart not found with id: {}", cartId);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        logger.debug("DELETE /api/v1/cart/{} - Delete cart", cartId);
        try {
            shoppingCartService.deleteCart(cartId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.warn("Cart not found with id: {}", cartId);
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error in ShoppingCartController: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", e.getMessage()));
    }
}