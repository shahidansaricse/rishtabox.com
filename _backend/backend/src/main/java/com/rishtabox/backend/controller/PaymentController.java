package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.CreatePaymentOrderRequest;
import com.rishtabox.backend.dto.PaymentVerifyRequest;
import com.rishtabox.backend.entity.Payment;
import com.rishtabox.backend.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    // =========================================================
    // RAZORPAY KEY
    // =========================================================

    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "key",
                paymentService.getRazorpayKeyId()
        );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // CREATE RAZORPAY PAYMENT ORDER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody CreatePaymentOrderRequest request) {

        try {

            Payment payment =
                    paymentService.createRazorpayOrder(
                            request
                    );

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Unable to create payment order"
                            )
                    );
        }
    }

    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody PaymentVerifyRequest request) {

        System.out.println(
                "========== RAZORPAY VERIFY =========="
        );

        if (request != null) {

            System.out.println(
                    "Order ID: "
                            + request.getOrderId()
            );

            System.out.println(
                    "Razorpay Order ID: "
                            + request.getRazorpayOrderId()
            );

            System.out.println(
                    "Razorpay Payment ID: "
                            + request.getRazorpayPaymentId()
            );

            System.out.println(
                    "Signature received: "
                            + (
                            request.getRazorpaySignature()
                                    != null
                    )
            );
        }

        try {

            Payment payment =
                    paymentService.verifyPayment(
                            request
                    );

            System.out.println(
                    "========== PAYMENT VERIFIED SUCCESS =========="
            );

            System.out.println(
                    "Payment DB ID: "
                            + payment.getId()
            );

            System.out.println(
                    "Status: "
                            + payment.getStatus()
            );

            return ResponseEntity.ok(payment);

        } catch (Exception e) {

            System.out.println(
                    "========== PAYMENT VERIFY FAILED =========="
            );

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage() != null
                                            ? e.getMessage()
                                            : "Payment verification failed"
                            )
                    );
        }
    }
}