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

        // 2. Check user ID
        if (request.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }

        // 3. Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 4. Find user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        // 5. Check cart
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 6. Create order
        Order order = new Order();

        order.setUser(user);

        String paymentMethod =
                request.getPaymentMethod().toUpperCase();

        order.setPaymentMethod(paymentMethod);

        // IMPORTANT:
        // Payment has not been completed yet.
        order.setPaymentStatus("PENDING");

        // COD can be placed immediately.
        // Razorpay waits for successful payment.
        if (paymentMethod.equals("COD")) {
            order.setOrderStatus("PLACED");
        } else {
            order.setOrderStatus("PAYMENT_PENDING");
        }

        order.setCreatedAt(LocalDateTime.now());

        // 7. Calculate total
        double totalAmount = 0.0;

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            // Connect order
            orderItem.setOrder(order);

            // Connect product
            orderItem.setProduct(cartItem.getProduct());

            // Quantity
            orderItem.setQuantity(cartItem.getQuantity());

            // Product price
            Double price = cartItem.getProduct().getPrice();

            if (price == null) {
                throw new RuntimeException(
                        "Product price is missing");
            }

            orderItem.setPrice(price);

            // Item total
            double itemTotal =
                    price * cartItem.getQuantity();

            totalAmount += itemTotal;

            // Add item to order
            order.getItems().add(orderItem);
        }

        // 8. Set total
        double deliveryCharges = totalAmount >= 500 ? 0.0 : 50.0;

        totalAmount += deliveryCharges;

        order.setTotalAmount(totalAmount);

        // 9. Save order
        Order savedOrder =
                orderRepository.save(order);

        // 10. Clear cart only for COD
        if (paymentMethod.equals("COD")) {

            cart.getItems().clear();
            cartRepository.save(cart);
        }

        // 11. Return saved order
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


    // Cancel Order
    @Transactional
    public Order cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if ("CANCELLED".equalsIgnoreCase(
                order.getOrderStatus())) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        order.setOrderStatus("CANCELLED");

        return orderRepository.save(order);
    }

}