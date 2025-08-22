package com.example.shoppingcart.service;

import com.example.shoppingcart.dto.AddItemRequest;
import com.example.shoppingcart.dto.CartResponse;
import com.example.shoppingcart.dto.CreateCartRequest;
import com.example.shoppingcart.mapper.CartItemMapper;
import com.example.shoppingcart.mapper.CustomerMapper;
import com.example.shoppingcart.mapper.ProductMapper;
import com.example.shoppingcart.mapper.ShoppingCartMapper;
import com.example.shoppingcart.model.CartItem;
import com.example.shoppingcart.model.Customer;
import com.example.shoppingcart.model.Product;
import com.example.shoppingcart.model.ShoppingCart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ProductMapper productMapper;

    public List<ShoppingCart> getAllCarts() {
        logger.debug("Fetching all shopping carts");
        return shoppingCartMapper.findAll();
    }

    public ShoppingCart getCartById(Long cartId) {
        logger.debug("Fetching cart with id: {}", cartId);
        ShoppingCart cart = shoppingCartMapper.findById(cartId);
        if (cart == null) {
            throw new RuntimeException("Shopping cart not found with id: " + cartId);
        }
        return cart;
    }

    public CartResponse getCartWithItems(Long cartId) {
        logger.debug("Fetching cart with items for id: {}", cartId);
        ShoppingCart cart = shoppingCartMapper.findByIdWithItems(cartId);
        if (cart == null) {
            throw new RuntimeException("Shopping cart not found with id: " + cartId);
        }

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCustomer(cart.getCustomer());
        response.setStatus(cart.getStatus());
        response.setTotalAmount(cart.getTotalAmount());
        response.setItems(cart.getItems());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        response.setTotalItems(cart.getItems() != null ? cart.getItems().size() : 0);

        return response;
    }

    public List<ShoppingCart> getCartsByCustomerId(Long customerId) {
        logger.debug("Fetching carts for customer: {}", customerId);
        return shoppingCartMapper.findByCustomerId(customerId);
    }

    public List<ShoppingCart> getCartsByStatus(String status) {
        logger.debug("Fetching carts by status: {}", status);
        return shoppingCartMapper.findByStatus(status);
    }

    public ShoppingCart createCart(CreateCartRequest request) {
        logger.debug("Creating cart for customer: {}", request.getCustomerId());
        
        Customer customer = customerMapper.findById(request.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + request.getCustomerId());
        }

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);
        cart.setStatus("ACTIVE");
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());

        shoppingCartMapper.insert(cart);
        logger.info("Created cart with id: {}", cart.getId());
        return cart;
    }

    public CartItem addItemToCart(Long cartId, AddItemRequest request) {
        logger.debug("Adding item to cart: {} with product: {}", cartId, request.getProductId());
        
        ShoppingCart cart = getCartById(cartId);
        Product product = productMapper.findById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + request.getProductId());
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        CartItem existingItem = cartItemMapper.findByCartIdAndProductId(cartId, request.getProductId());
        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStockQuantity() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setTotalPrice(existingItem.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
            existingItem.setUpdatedAt(LocalDateTime.now());
            cartItemMapper.update(existingItem);
            
            updateCartTotal(cartId);
            logger.info("Updated existing cart item with id: {}", existingItem.getId());
            return existingItem;
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setShoppingCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(product.getPrice());
            cartItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());

            cartItemMapper.insert(cartItem);
            updateCartTotal(cartId);
            logger.info("Added new cart item with id: {}", cartItem.getId());
            return cartItem;
        }
    }

    public CartItem updateCartItem(Long cartId, Long itemId, Integer quantity) {
        logger.debug("Updating cart item: {} in cart: {} with quantity: {}", itemId, cartId, quantity);
        
        CartItem cartItem = cartItemMapper.findById(itemId);
        if (cartItem == null || !cartItem.getShoppingCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item not found with id: " + itemId + " in cart: " + cartId);
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setUpdatedAt(LocalDateTime.now());

        cartItemMapper.update(cartItem);
        updateCartTotal(cartId);
        logger.info("Updated cart item with id: {}", itemId);
        return cartItem;
    }

    public void removeItemFromCart(Long cartId, Long itemId) {
        logger.debug("Removing item: {} from cart: {}", itemId, cartId);
        
        CartItem cartItem = cartItemMapper.findById(itemId);
        if (cartItem == null || !cartItem.getShoppingCart().getId().equals(cartId)) {
            throw new RuntimeException("Cart item not found with id: " + itemId + " in cart: " + cartId);
        }

        cartItemMapper.deleteById(itemId);
        updateCartTotal(cartId);
        logger.info("Removed cart item with id: {}", itemId);
    }

    public void clearCart(Long cartId) {
        logger.debug("Clearing all items from cart: {}", cartId);
        getCartById(cartId); // Verify cart exists
        
        cartItemMapper.deleteByCartId(cartId);
        updateCartTotal(cartId);
        logger.info("Cleared all items from cart: {}", cartId);
    }

    public void deleteCart(Long cartId) {
        logger.debug("Deleting cart: {}", cartId);
        getCartById(cartId); // Verify cart exists
        
        cartItemMapper.deleteByCartId(cartId);
        shoppingCartMapper.deleteById(cartId);
        logger.info("Deleted cart with id: {}", cartId);
    }

    private void updateCartTotal(Long cartId) {
        List<CartItem> items = cartItemMapper.findByCartId(cartId);
        BigDecimal total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ShoppingCart cart = shoppingCartMapper.findById(cartId);
        cart.setTotalAmount(total);
        cart.setUpdatedAt(LocalDateTime.now());
        shoppingCartMapper.update(cart);
        
        logger.debug("Updated cart total for cart: {} to: {}", cartId, total);
    }
}