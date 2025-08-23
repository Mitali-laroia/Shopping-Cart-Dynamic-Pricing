package com.example.shoppingcart.controller;

import com.example.shoppingcart.dto.CartCalculationRequest;
import com.example.shoppingcart.dto.CartCalculationResponse;
import com.example.shoppingcart.service.PricingService;
import com.example.shoppingcart.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private static final Logger logger = LoggerFactory.getLogger(PricingController.class);

    @Autowired
    private PricingService pricingService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/calculate")
    public ResponseEntity<CartCalculationResponse> calculateCartPricing(
            @RequestBody CartCalculationRequest request) {
        logger.debug("POST /api/v1/pricing/calculate - Calculate pricing for {} items", 
                    request.getItems() != null ? request.getItems().size() : 0);
        
        try {
            if (request.getItems() == null || request.getItems().isEmpty()) {
                logger.warn("Empty or null items list in pricing calculation request");
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getCustomer() == null || request.getCustomer().getLoyaltyLevel() == null) {
                logger.warn("Missing customer or loyalty level in pricing calculation request");
                return ResponseEntity.badRequest().build();
            }
            
            CartCalculationResponse response = pricingService.calculateCartPricing(request);
            logger.info("Pricing calculation completed successfully. Final total: {}", response.getFinalTotal());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid pricing calculation request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error during pricing calculation: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cart/{cartId}/calculate")
    public ResponseEntity<CartCalculationResponse> calculateCartPricingById(
            @PathVariable Long cartId) {
        logger.debug("POST /api/v1/pricing/cart/{}/calculate - Calculate pricing for existing cart", cartId);
        
        try {
            CartCalculationResponse response = shoppingCartService.calculateCartPricing(cartId);
            logger.info("Cart pricing calculation completed for cart {}. Final total: {}", 
                       cartId, response.getFinalTotal());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                logger.warn("Cart not found with id: {}", cartId);
                return ResponseEntity.notFound().build();
            }
            
            logger.error("Error calculating pricing for cart {}: {}", cartId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error in PricingController: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal server error", "message", e.getMessage()));
    }
}