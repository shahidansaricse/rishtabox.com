package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.AddCartRequest;
import com.rishtabox.backend.entity.Cart;
import com.rishtabox.backend.entity.CartItem;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.CartItemRepository;
import com.rishtabox.backend.repository.CartRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Cart addToCart(AddCartRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() ->
                        cartRepository.save(new Cart(user)));

        int quantity =
                request.getQuantity() == null
                        ? 1
                        : request.getQuantity();

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                product.getId()
                        )
                        .orElse(null);

        if (cartItem == null) {

            cartItem =
                    new CartItem(
                            cart,
                            product,
                            quantity
                    );

        } else {

            cartItem.setQuantity(
                    cartItem.getQuantity() + quantity
            );
        }

        cartItemRepository.save(cartItem);

        return cartRepository
                .findById(cart.getId())
                .orElseThrow();
    }

    public Cart getCart(Long userId) {

        return cartRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));
    }

    public void removeItem(Long cartItemId) {

        cartItemRepository.deleteById(cartItemId);
    }

    public void clearCart(Long userId) {

        Cart cart = getCart(userId);

        cart.getItems().clear();

        cartRepository.save(cart);
    }
}