package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.OrderRequest;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // Place Order
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody OrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrder(request)
        );
    }


    // Get all orders of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getUserOrders(userId)
        );
    }


    // Get single order
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrder(orderId)
        );
    }
    // Cancel Order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.cancelOrder(orderId)
        );
    }
}