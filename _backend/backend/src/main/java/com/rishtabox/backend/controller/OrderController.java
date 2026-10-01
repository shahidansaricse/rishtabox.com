package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.admin.AdminOrderUpdateRequest;
import com.rishtabox.backend.dto.OrderRequest;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.service.OrderService;
import com.rishtabox.backend.service.ShiprocketService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ShiprocketService shiprocketService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public OrderController(
            OrderService orderService,
            ShiprocketService shiprocketService) {

        this.orderService =
                orderService;

        this.shiprocketService =
                shiprocketService;
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
    // CONFIRM RAZORPAY PAYMENT
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<Order> confirmRazorpayPayment(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.confirmRazorpayPayment(
                        orderId
                )
        );
    }


    // =========================================================
    // GET ALL ORDERS OF AUTHENTICATED USER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId) {

        List<Order> orders =
                orderService.getUserOrders(userId);

        return ResponseEntity.ok(orders);
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


    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }


    // =========================================================
    // ADMIN - UPDATE ORDER
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
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
    // SHIPROCKET - CREATE ORDER
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping("/{orderId}/shiprocket/create")
    public ResponseEntity<Order> createShiprocketOrder(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService.createShiprocketOrder(
                        orderId
                );

        return ResponseEntity.ok(order);
    }


    // =========================================================
    // SHIPROCKET - ASSIGN COURIER / AWB
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping(
            "/{orderId}/shiprocket/assign/{shipmentId}"
    )
    public ResponseEntity<Order> assignCourier(
            @PathVariable Long orderId,
            @PathVariable Long shipmentId) {

        Order order =
                shiprocketService.assignCourier(
                        orderId,
                        shipmentId
                );

        return ResponseEntity.ok(order);
    }


    // =========================================================
    // SHIPROCKET - TRACK SHIPMENT
    // ADMIN ONLY
    //
    // Refreshes data from Shiprocket.
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{orderId}/shiprocket/track")
    public ResponseEntity<Order> trackShipment(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService.trackShipment(
                        orderId
                );

        return ResponseEntity.ok(order);
    }


    // =========================================================
    // CUSTOMER - GET SAVED SHIPPING INFORMATION
    //
    // This does NOT call Shiprocket.
    //
    // It simply returns the information already saved
    // in our database.
    //
    // Customer can use this to display:
    //
    // - AWB
    // - Tracking ID
    // - Courier
    // - Shipment status
    // - Tracking URL
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{orderId}/shipping")
    public ResponseEntity<?> getCustomerShippingInformation(
            @PathVariable Long orderId) {

        Order order =
                orderService.getOrder(orderId);

        return ResponseEntity.ok(
                createShippingResponse(order)
        );
    }


    // =========================================================
    // SHIPPING RESPONSE
    // =========================================================

    private java.util.Map<String, Object> createShippingResponse(
            Order order) {

        java.util.Map<String, Object> response =
                new java.util.HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "orderId",
                order.getId()
        );

        response.put(
                "orderNumber",
                order.getOrderNumber()
        );

        response.put(
                "awbCode",
                order.getAwbCode()
        );

        response.put(
                "trackingId",
                order.getTrackingId()
        );

        response.put(
                "courierName",
                order.getCourierName()
        );

        response.put(
                "shipmentStatus",
                order.getShipmentStatus()
        );

        response.put(
                "trackingUrl",
                order.getTrackingUrl()
        );

        response.put(
                "shippingMode",
                order.getShippingMode()
        );

        return response;
    }
}