package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.OrderRequest;
import com.rishtabox.backend.entity.Cart;
import com.rishtabox.backend.entity.CartItem;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.entity.OrderItem;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.CartRepository;
import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartRepository cartRepository) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order createOrder(OrderRequest request) {

        // 1. Check payment method
        if (request.getPaymentMethod() == null
                || (!request.getPaymentMethod().equalsIgnoreCase("COD")
                && !request.getPaymentMethod().equalsIgnoreCase("RAZORPAY"))) {

            throw new RuntimeException("Invalid payment method");
        }

        // 2. Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 3. Find user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        // 4. Check cart
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 5. Create order
        Order order = new Order();

        order.setUser(user);

        // Store payment method
        String paymentMethod =
                request.getPaymentMethod().toUpperCase();

        order.setPaymentMethod(paymentMethod);

        /*
         * COD:
         * Payment is not required online,
         * so order can be placed directly.
         */
        if (paymentMethod.equals("COD")) {
            order.setOrderStatus("PLACED");
        } else {

            /*
             * Razorpay payment has not succeeded yet.
             */
            order.setOrderStatus("PAYMENT_PENDING");
        }

        order.setCreatedAt(LocalDateTime.now());

        // 6. Calculate total using Double
        double totalAmount = 0.0;

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            // Connect order
            orderItem.setOrder(order);

            // Connect product
            orderItem.setProduct(cartItem.getProduct());

            // Product quantity
            orderItem.setQuantity(cartItem.getQuantity());

            // Product price
            Double price =
                    cartItem.getProduct().getPrice();

            if (price == null) {
                throw new RuntimeException(
                        "Product price is missing");
            }

            orderItem.setPrice(price);

            // Calculate item total
            double itemTotal =
                    price * cartItem.getQuantity();

            // Add to order total
            totalAmount += itemTotal;

            // Add item to order
            order.getItems().add(orderItem);
        }

        // 7. Set total amount
        order.setTotalAmount(totalAmount);

        // 8. Save order
        Order savedOrder =
                orderRepository.save(order);

        /*
         * COD:
         * Order is already placed, so clear cart.
         *
         * Razorpay:
         * DO NOT clear cart here.
         * Clear it only after successful Razorpay payment.
         */
        if (paymentMethod.equals("COD")) {

            cart.getItems().clear();
            cartRepository.save(cart);
        }

        // 9. Return saved order
        return savedOrder;
    }

    // Get all orders of a user
    public List<Order> getUserOrders(Long userId) {

        return orderRepository.findByUserId(userId);
    }

    // Get single order
    public Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));
    }
}