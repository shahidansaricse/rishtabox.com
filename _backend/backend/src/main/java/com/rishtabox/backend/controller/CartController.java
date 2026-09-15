package com.rishtabox.backend.controller;


import com.rishtabox.backend.dto.AddCartRequest;
import com.rishtabox.backend.entity.Cart;
import com.rishtabox.backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody AddCartRequest request) {

        return ResponseEntity.ok(
                cartService.addToCart(request)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeItem(
            @PathVariable Long cartItemId) {

        cartService.removeItem(cartItemId);

        return ResponseEntity.ok(
                "Item removed from cart"
        );
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.ok(
                "Cart cleared"
        );
    }
}