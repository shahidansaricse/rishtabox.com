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

        // 1. Find user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        // 2. Find user's cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));


        // 3. Check cart
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }


        // 4. Only COD for Day 6A
        if (!request.getPaymentMethod()
                .equalsIgnoreCase("COD")) {

            throw new RuntimeException(
                    "Only COD payment is available currently"
            );
        }


        // 5. Create order
        Order order = new Order();

        order.setUser(user);
        order.setPaymentMethod("COD");
        order.setPaymentStatus("PENDING");
        order.setOrderStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());


        // 6. Copy cart items to order
        double totalAmount = 0;


        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            double price = cartItem.getProduct().getPrice();

            orderItem.setPrice(price);


            totalAmount =
                    totalAmount +
                            (price * cartItem.getQuantity());


            order.getItems().add(orderItem);
        }


        // 7. Set total
        order.setTotalAmount(totalAmount);


        // 8. Save order
        Order savedOrder = orderRepository.save(order);


        // 9. Clear cart
        cart.getItems().clear();

        cartRepository.save(cart);


        // 10. Return order
        return savedOrder;
    }


    public List<Order> getUserOrders(Long userId) {

        return orderRepository.findByUserId(userId);
    }


    public Order getOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));
    }
}