package com.rishtabox.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import com.rishtabox.backend.dto.CreatePaymentOrderRequest;
import com.rishtabox.backend.dto.PaymentVerifyRequest;
import com.rishtabox.backend.entity.Payment;
import com.rishtabox.backend.entity.OrderItem;
import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.PaymentRepository;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;


    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            RazorpayClient razorpayClient) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.razorpayClient = razorpayClient;
    }


    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }


    public Payment createRazorpayOrder(
            CreatePaymentOrderRequest request) throws Exception {

        com.rishtabox.backend.entity.Order order =
                orderRepository.findById(request.getOrderId())
                        .orElseThrow(() ->
                                new RuntimeException("Order not found"));


        if (order.getPaymentMethod().equalsIgnoreCase("COD")) {
            throw new RuntimeException(
                    "COD order cannot create Razorpay payment"
            );
        }


        // Razorpay accepts amount in paise
        long amountInPaise =
                Math.round(order.getTotalAmount() * 100);


        JSONObject options = new JSONObject();

        options.put("amount", amountInPaise);
        options.put("currency", "INR");
        options.put("receipt", "RISHTABOX_" + order.getId());


        Order razorpayOrder =
                razorpayClient.orders.create(options);


        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setRazorpayOrderId(
                razorpayOrder.get("id")
        );
        payment.setStatus("CREATED");
        payment.setAmount(order.getTotalAmount());
        payment.setCreatedAt(LocalDateTime.now());


        return paymentRepository.save(payment);
    }


    @Transactional
    public Payment verifyPayment(
            PaymentVerifyRequest request) throws Exception {


        com.rishtabox.backend.entity.Order order =
                orderRepository.findById(request.getOrderId())
                        .orElseThrow(() ->
                                new RuntimeException("Order not found"));


        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment order not found"
                                ));


        // Make sure payment belongs to this order
        if (!payment.getOrder().getId()
                .equals(order.getId())) {

            throw new RuntimeException(
                    "Payment does not belong to this order"
            );
        }


        String signatureData =
                request.getRazorpayOrderId()
                        + "|"
                        + request.getRazorpayPaymentId();


        boolean valid = Utils.verifySignature(
                signatureData,
                request.getRazorpaySignature(),
                getRazorpaySecret()
        );


        if (!valid) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            throw new RuntimeException(
                    "Invalid Razorpay signature"
            );
        }


        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId()
        );

        payment.setRazorpaySignature(
                request.getRazorpaySignature()
        );

        payment.setStatus("SUCCESS");
        order.setPaymentStatus("PAID");
        order.setOrderStatus("PLACED");
        orderRepository.save(order);


        return paymentRepository.save(payment);
    }


    @Value("${razorpay.key.secret}")
    private String razorpaySecret;


    private String getRazorpaySecret() {
        return razorpaySecret;
    }
}