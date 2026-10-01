package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.service.ShiprocketService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shiprocket")
@CrossOrigin
public class ShiprocketController {

    private final ShiprocketService shiprocketService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ShiprocketController(
            ShiprocketService shiprocketService) {

        this.shiprocketService =
                shiprocketService;
    }


    // =========================================================
    // TEST SHIPROCKET LOGIN
    // ADMIN ONLY
    // =========================================================

    @PostMapping("/login")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> login() {

        String token =
                shiprocketService.login();

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Shiprocket login successful"
        );

        /*
         * TESTING ONLY
         *
         * Do not expose this token
         * to customer frontend.
         */

        response.put(
                "token",
                token
        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // CREATE SHIPROCKET ORDER
    // ADMIN ONLY
    // =========================================================

    @PostMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> createShiprocketOrder(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService
                        .createShiprocketOrder(
                                orderId
                        );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Shiprocket order created successfully"
        );

        response.put(
                "order",
                order
        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // ASSIGN COURIER / AWB
    // ADMIN ONLY
    // =========================================================

    @PostMapping(
            "/orders/{orderId}/assign-courier/{shipmentId}"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> assignCourier(
            @PathVariable Long orderId,
            @PathVariable Long shipmentId) {

        Order order =
                shiprocketService
                        .assignCourier(
                                orderId,
                                shipmentId
                        );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Courier/AWB assigned successfully"
        );

        response.put(
                "order",
                order
        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // TRACK SHIPMENT
    // ADMIN ONLY
    //
    // This calls Shiprocket and refreshes:
    // - AWB
    // - Courier
    // - Shipment status
    // - Tracking URL
    // =========================================================

    @GetMapping(
            "/orders/{orderId}/track"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> trackShipment(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService
                        .trackShipment(
                                orderId
                        );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Shipment tracking updated"
        );

        response.put(
                "order",
                order
        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // GET SHIPPING INFORMATION
    // ADMIN ONLY
    //
    // Returns saved Shiprocket information.
    // =========================================================

    @GetMapping(
            "/orders/{orderId}"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getShippingInformation(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService
                        .getShippingInformation(
                                orderId
                        );

        return ResponseEntity.ok(
                createShippingResponse(order)
        );
    }


    // =========================================================
    // CUSTOMER TRACKING
    //
    // IMPORTANT:
    // This endpoint does NOT expose the Shiprocket login token.
    //
    // Customer can receive:
    // - Order ID
    // - AWB
    // - Tracking ID
    // - Courier
    // - Shipment status
    // - Tracking URL
    // =========================================================

    @GetMapping(
            "/public/orders/{orderId}"
    )
    public ResponseEntity<?> getCustomerShippingInformation(
            @PathVariable Long orderId) {

        Order order =
                shiprocketService
                        .getShippingInformation(
                                orderId
                        );

        Map<String, Object> response =
                new HashMap<>();

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

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // CUSTOMER TRACKING BY ORDER NUMBER
    //
    // Example:
    // /api/shiprocket/public/order-number/ORD123456
    //
    // This is better for the customer website because
    // customers normally see ORDER NUMBER, not database ID.
    // =========================================================

    @GetMapping(
            "/public/order-number/{orderNumber}"
    )
    public ResponseEntity<?> getCustomerShippingByOrderNumber(
            @PathVariable String orderNumber) {

        Order order =
                shiprocketService
                        .getShippingInformationByOrderNumber(
                                orderNumber
                        );

        Map<String, Object> response =
                new HashMap<>();

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

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // CREATE SHIPPING RESPONSE
    // =========================================================

    private Map<String, Object> createShippingResponse(
            Order order) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "success",
                true
        );

        response.put(
                "message",
                "Shipping information fetched successfully"
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
                "shiprocketOrderId",
                order.getShiprocketOrderId()
        );

        response.put(
                "shiprocketShipmentId",
                order.getShiprocketShipmentId()
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