package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(
            OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrder(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id
                        )
                );
    }

    @Transactional
    public Order updateOrderStatus(
            Long id,
            String status) {

        Order order = getOrder(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException(
                    "Order status is required"
            );
        }

        String newStatus =
                status.trim().toUpperCase();

        if (!isValidOrderStatus(newStatus)) {
            throw new RuntimeException(
                    "Invalid order status: " + newStatus
            );
        }

        order.setOrderStatus(newStatus);

        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(
            Long id,
            String status) {

        Order order = getOrder(id);

        if (status == null || status.isBlank()) {
            throw new RuntimeException(
                    "Payment status is required"
            );
        }

        String newStatus =
                status.trim().toUpperCase();

        if (!isValidPaymentStatus(newStatus)) {
            throw new RuntimeException(
                    "Invalid payment status: " + newStatus
            );
        }

        order.setPaymentStatus(newStatus);

        return orderRepository.save(order);
    }

    public long getTotalOrders() {
        return orderRepository.count();
    }

    public long getPendingOrders() {

        return orderRepository.countByOrderStatus(
                "PAYMENT_PENDING"
        );
    }

    public long getDeliveredOrders() {

        return orderRepository.countByOrderStatus(
                "DELIVERED"
        );
    }

    public long getCancelledOrders() {

        return orderRepository.countByOrderStatus(
                "CANCELLED"
        );
    }

    private boolean isValidOrderStatus(
            String status) {

        return status.equals("PLACED")
                || status.equals("PROCESSING")
                || status.equals("SHIPPED")
                || status.equals("DELIVERED")
                || status.equals("CANCELLED")
                || status.equals("PAYMENT_PENDING");
    }

    private boolean isValidPaymentStatus(
            String status) {

        return status.equals("PENDING")
                || status.equals("PAID")
                || status.equals("FAILED")
                || status.equals("REFUNDED");
    }
}