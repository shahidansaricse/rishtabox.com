package com.rishtabox.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import com.rishtabox.backend.dto.CreatePaymentOrderRequest;
import com.rishtabox.backend.dto.PaymentVerifyRequest;
import com.rishtabox.backend.entity.Payment;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.PaymentRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RazorpayClient razorpayClient;
    private final OrderService orderService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            RazorpayClient razorpayClient,
            OrderService orderService) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.razorpayClient = razorpayClient;
        this.orderService = orderService;
    }

    // =========================================================
    // GET RAZORPAY KEY ID
    // =========================================================

    public String getRazorpayKeyId() {

        return razorpayKeyId;
    }

    // =========================================================
    // GET AUTHENTICATED USER
    // =========================================================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required"
            );
        }

        String email =
                authentication.getName();

        if (email == null
                || email.isBlank()) {

            throw new RuntimeException(
                    "Authenticated user email not found"
            );
        }

        return userRepository.findByEmail(
                email.trim().toLowerCase()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Authenticated user not found"
                ));
    }

    // =========================================================
    // VALIDATE ORDER OWNERSHIP
    // =========================================================

    private void validateOrderOwnership(
            com.rishtabox.backend.entity.Order order,
            User user) {

        if (order == null
                || order.getUser() == null
                || order.getUser().getId() == null
                || user == null
                || user.getId() == null
                || !order.getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You can only make payment for your own order"
            );
        }
    }

    // =========================================================
    // VALIDATE ORDER PRODUCTS / STOCK
    //
    // Final stock validation happens again during
    // successful payment confirmation.
    // =========================================================

    private void validateOrderStock(
            com.rishtabox.backend.entity.Order order) {

        if (order == null) {

            throw new RuntimeException(
                    "Order not found"
            );
        }

        if (order.getItems() == null
                || order.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Order has no items"
            );
        }

        order.getItems().forEach(orderItem -> {

            if (orderItem == null) {

                throw new RuntimeException(
                        "Invalid order item"
                );
            }

            Product product =
                    orderItem.getProduct();

            if (product == null) {

                throw new RuntimeException(
                        "Product not found in order"
                );
            }

            // -------------------------------------------------
            // PRODUCT MUST BE ACTIVE
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                throw new RuntimeException(
                        "Product is currently unavailable: "
                                + product.getName()
                );
            }

            // -------------------------------------------------
            // STOCK
            // -------------------------------------------------

            Integer stock =
                    product.getStock();

            Integer quantity =
                    orderItem.getQuantity();

            if (quantity == null
                    || quantity <= 0) {

                throw new RuntimeException(
                        "Invalid product quantity"
                );
            }

            if (stock == null
                    || stock <= 0) {

                throw new RuntimeException(
                        "Out of stock: "
                                + product.getName()
                );
            }

            if (quantity > stock) {

                throw new RuntimeException(
                        "Only "
                                + stock
                                + " item(s) available for: "
                                + product.getName()
                );
            }
        });
    }

    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    public Payment createRazorpayOrder(
            CreatePaymentOrderRequest request)
            throws Exception {

        if (request == null
                || request.getOrderId() == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        User authenticatedUser =
                getAuthenticatedUser();

        // -----------------------------------------------------
        // FIND DATABASE ORDER
        // -----------------------------------------------------

        com.rishtabox.backend.entity.Order order =
                orderRepository.findById(
                        request.getOrderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        // -----------------------------------------------------
        // OWNERSHIP
        // -----------------------------------------------------

        validateOrderOwnership(
                order,
                authenticatedUser
        );

        // -----------------------------------------------------
        // PAYMENT METHOD
        // -----------------------------------------------------

        if (order.getPaymentMethod() == null) {

            throw new RuntimeException(
                    "Payment method not found"
            );
        }

        if ("COD".equalsIgnoreCase(
                order.getPaymentMethod())) {

            throw new RuntimeException(
                    "COD order cannot create Razorpay payment"
            );
        }

        if (!"RAZORPAY".equalsIgnoreCase(
                order.getPaymentMethod())) {

            throw new RuntimeException(
                    "Invalid payment method"
            );
        }

        // -----------------------------------------------------
        // PAYMENT STATUS
        // -----------------------------------------------------

        if ("PAID".equalsIgnoreCase(
                order.getPaymentStatus())) {

            throw new RuntimeException(
                    "Order is already paid"
            );
        }

        // -----------------------------------------------------
        // ORDER STATUS
        // -----------------------------------------------------

        if ("CANCELLED".equalsIgnoreCase(
                order.getOrderStatus())) {

            throw new RuntimeException(
                    "Cancelled order cannot be paid"
            );
        }

        // -----------------------------------------------------
        // VALIDATE STOCK BEFORE OPENING RAZORPAY
        // -----------------------------------------------------

        validateOrderStock(order);

        // -----------------------------------------------------
        // ORDER AMOUNT
        // -----------------------------------------------------

        if (order.getTotalAmount() == null
                || order.getTotalAmount() <= 0) {

            throw new RuntimeException(
                    "Invalid order amount"
            );
        }

        // -----------------------------------------------------
        // CONVERT TO PAISE
        // -----------------------------------------------------

        long amountInPaise =
                Math.round(
                        order.getTotalAmount() * 100
                );

        if (amountInPaise <= 0) {

            throw new RuntimeException(
                    "Invalid Razorpay amount"
            );
        }

        // -----------------------------------------------------
        // CREATE RAZORPAY ORDER
        // -----------------------------------------------------

        JSONObject options =
                new JSONObject();

        options.put(
                "amount",
                amountInPaise
        );

        options.put(
                "currency",
                "INR"
        );

        options.put(
                "receipt",
                "RISHTABOX_" + order.getId()
        );

        Order razorpayOrder =
                razorpayClient.orders.create(
                        options
                );

        // -----------------------------------------------------
        // SAVE PAYMENT
        // -----------------------------------------------------

        Payment payment =
                new Payment();

        payment.setOrder(order);

        payment.setRazorpayOrderId(
                razorpayOrder.get("id")
        );

        payment.setStatus(
                "CREATED"
        );

        payment.setAmount(
                order.getTotalAmount()
        );

        payment.setCreatedAt(
                LocalDateTime.now()
        );

        return paymentRepository.save(
                payment
        );
    }

    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    //
    // Signature valid hone ke baad:
    //
    // 1. Stock decrease
    // 2. Payment = PAID
    // 3. Order = PLACED
    // 4. Cart clear
    //
    // Sab ek transaction mein.
    // =========================================================

    @Transactional
    public Payment verifyPayment(
            PaymentVerifyRequest request)
            throws Exception {

        // -----------------------------------------------------
        // BASIC VALIDATION
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Payment verification request is required"
            );
        }

        if (request.getOrderId() == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        if (request.getRazorpayOrderId() == null
                || request.getRazorpayOrderId()
                .isBlank()) {

            throw new RuntimeException(
                    "Razorpay order ID is required"
            );
        }

        if (request.getRazorpayPaymentId() == null
                || request.getRazorpayPaymentId()
                .isBlank()) {

            throw new RuntimeException(
                    "Razorpay payment ID is required"
            );
        }

        if (request.getRazorpaySignature() == null
                || request.getRazorpaySignature()
                .isBlank()) {

            throw new RuntimeException(
                    "Razorpay signature is required"
            );
        }

        // -----------------------------------------------------
        // AUTHENTICATED USER
        // -----------------------------------------------------

        User authenticatedUser =
                getAuthenticatedUser();

        // -----------------------------------------------------
        // FIND DATABASE ORDER
        // -----------------------------------------------------

        com.rishtabox.backend.entity.Order order =
                orderRepository.findById(
                        request.getOrderId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        // -----------------------------------------------------
        // OWNERSHIP
        // -----------------------------------------------------

        validateOrderOwnership(
                order,
                authenticatedUser
        );

        // -----------------------------------------------------
        // PAYMENT RECORD
        // -----------------------------------------------------

        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                request.getRazorpayOrderId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment order not found"
                                ));

        // -----------------------------------------------------
        // PAYMENT -> ORDER MATCH
        // -----------------------------------------------------

        if (payment.getOrder() == null
                || payment.getOrder().getId() == null
                || !payment.getOrder()
                .getId()
                .equals(order.getId())) {

            throw new RuntimeException(
                    "Payment does not belong to this order"
            );
        }

        // -----------------------------------------------------
        // ALREADY VERIFIED
        // -----------------------------------------------------

        if ("SUCCESS".equalsIgnoreCase(
                payment.getStatus())) {

            return payment;
        }

        // -----------------------------------------------------
        // ORDER MUST NOT BE CANCELLED
        // -----------------------------------------------------

        if ("CANCELLED".equalsIgnoreCase(
                order.getOrderStatus())) {

            throw new RuntimeException(
                    "Cancelled order cannot be verified"
            );
        }

        // -----------------------------------------------------
        // VERIFY RAZORPAY SIGNATURE
        // -----------------------------------------------------

        String signatureData =
                request.getRazorpayOrderId()
                        + "|"
                        + request.getRazorpayPaymentId();

        boolean valid =
                Utils.verifySignature(
                        signatureData,
                        request.getRazorpaySignature(),
                        getRazorpaySecret()
                );

        // -----------------------------------------------------
        // INVALID SIGNATURE
        // -----------------------------------------------------

        if (!valid) {

            payment.setStatus(
                    "FAILED"
            );

            paymentRepository.save(
                    payment
            );

            throw new RuntimeException(
                    "Invalid Razorpay signature"
            );
        }

        // -----------------------------------------------------
        // IMPORTANT
        //
        // SIGNATURE IS VALID.
        //
        // NOW CONFIRM ORDER + REDUCE STOCK.
        // -----------------------------------------------------

        /*
         * confirmRazorpayPayment() does:
         *
         * - ownership check
         * - product availability check
         * - stock check
         * - stock decrease
         * - order PAID
         * - order PLACED
         * - cart clear
         *
         * If stock fails, the transaction fails and
         * payment verification will not be completed.
         */

        orderService.confirmRazorpayPayment(
                order.getId()
        );

        // -----------------------------------------------------
        // SAVE RAZORPAY PAYMENT DETAILS
        // -----------------------------------------------------

        payment.setRazorpayPaymentId(
                request.getRazorpayPaymentId()
        );

        payment.setRazorpaySignature(
                request.getRazorpaySignature()
        );

        payment.setStatus(
                "SUCCESS"
        );

        return paymentRepository.save(
                payment
        );
    }

    // =========================================================
    // RAZORPAY SECRET
    // =========================================================

    private String getRazorpaySecret() {

        return razorpaySecret;
    }
}