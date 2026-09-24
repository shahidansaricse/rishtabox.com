package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.admin.AdminOrderUpdateRequest;
import com.rishtabox.backend.dto.OrderRequest;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // =========================================================
    // PLACE ORDER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody OrderRequest request) {

        return ResponseEntity.ok(
                orderService.createOrder(request)
        );
    }


    // =========================================================
    // GET ALL ORDERS OF AUTHENTICATED USER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getUserOrders(userId)
        );
    }


    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }


    // =========================================================
    // ADMIN - UPDATE ORDER
    //
    // Updates:
    // 1. Order Status
    // 2. Shipping Mode
    // 3. Tracking ID
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{orderId}")
    public ResponseEntity<Order> updateOrderFromAdmin(
            @PathVariable Long orderId,
            @RequestBody AdminOrderUpdateRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderFromAdmin(
                        orderId,
                        request
                )
        );
    }


    // =========================================================
    // GET SINGLE ORDER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrder(orderId)
        );
    }


    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.cancelOrder(orderId)
        );
    }
}

