package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.CreatePaymentOrderRequest;
import com.rishtabox.backend.dto.PaymentVerifyRequest;
import com.rishtabox.backend.entity.Payment;
import com.rishtabox.backend.service.PaymentService;

import org.springframework.http.ResponseEntity;
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


    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {

        Map<String, String> response = new HashMap<>();

        response.put(
                "key",
                paymentService.getRazorpayKeyId()
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create-order")
    public ResponseEntity<Payment> createOrder(
            @RequestBody CreatePaymentOrderRequest request)
            throws Exception {

        return ResponseEntity.ok(
                paymentService.createRazorpayOrder(request)
        );
    }


    @PostMapping("/verify")
    public ResponseEntity<Payment> verifyPayment(
            @RequestBody PaymentVerifyRequest request)
            throws Exception {

        return ResponseEntity.ok(
                paymentService.verifyPayment(request)
        );
    }
}