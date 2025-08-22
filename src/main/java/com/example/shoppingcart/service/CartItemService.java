package com.example.shoppingcart.service;

import com.example.shoppingcart.mapper.CartItemMapper;
import com.example.shoppingcart.model.CartItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartItemService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemService.class);

    @Autowired
    private CartItemMapper cartItemMapper;

    public List<CartItem> getAllCartItems() {
        logger.debug("Fetching all cart items");
        return cartItemMapper.findAll();
    }

    public CartItem getCartItemById(Long itemId) {
        logger.debug("Fetching cart item with id: {}", itemId);
        CartItem cartItem = cartItemMapper.findById(itemId);
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found with id: " + itemId);
        }
        return cartItem;
    }

    public List<CartItem> getCartItemsByCartId(Long cartId) {
        logger.debug("Fetching cart items for cart: {}", cartId);
        return cartItemMapper.findByCartId(cartId);
    }

    public CartItem getCartItemByCartAndProduct(Long cartId, Long productId) {
        logger.debug("Fetching cart item for cart: {} and product: {}", cartId, productId);
        CartItem cartItem = cartItemMapper.findByCartIdAndProductId(cartId, productId);
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found for cart: " + cartId + " and product: " + productId);
        }
        return cartItem;
    }

    public void deleteCartItem(Long itemId) {
        logger.debug("Deleting cart item with id: {}", itemId);
        CartItem existingItem = getCartItemById(itemId);
        cartItemMapper.deleteById(itemId);
        logger.info("Deleted cart item with id: {}", itemId);
    }

    public void deleteCartItemsByCartId(Long cartId) {
        logger.debug("Deleting all cart items for cart: {}", cartId);
        cartItemMapper.deleteByCartId(cartId);
        logger.info("Deleted all cart items for cart: {}", cartId);
    }
}