package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.admin.AdminOrderUpdateRequest;
import com.rishtabox.backend.dto.OrderRequest;
import com.rishtabox.backend.entity.Cart;
import com.rishtabox.backend.entity.CartItem;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.entity.OrderItem;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.CartRepository;
import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductService productService;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            ProductService productService) {

        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    // =========================================================
    // GET AUTHENTICATED USER
    // =========================================================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "Authentication required"
            );
        }

        String email = authentication.getName();

        if (email == null || email.isBlank()) {

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
    // CREATE ORDER
    // =========================================================

    @Transactional
    public Order createOrder(OrderRequest request) {

        if (request == null) {

            throw new RuntimeException(
                    "Order request is required"
            );
        }

        User user = getAuthenticatedUser();

        Cart cart =
                cartRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart not found"
                                ));

        List<CartItem> cartItems =
                cart.getItems();

        if (cartItems == null ||
                cartItems.isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }

        String paymentMethod =
                request.getPaymentMethod();

        if (paymentMethod == null ||
                paymentMethod.isBlank()) {

            throw new RuntimeException(
                    "Payment method is required"
            );
        }

        paymentMethod =
                paymentMethod
                        .trim()
                        .toUpperCase();

        if (!paymentMethod.equals("COD") &&
                !paymentMethod.equals("RAZORPAY")) {

            throw new RuntimeException(
                    "Invalid payment method"
            );
        }

        Order order = new Order();

        order.setUser(user);

        order.setPaymentMethod(
                paymentMethod
        );

        double productTotal = 0;

        for (CartItem cartItem : cartItems) {

            if (cartItem == null) {

                throw new RuntimeException(
                        "Invalid cart item"
                );
            }

            Product product =
                    cartItem.getProduct();

            if (product == null) {

                throw new RuntimeException(
                        "Product not found in cart"
                );
            }

            Integer quantity =
                    cartItem.getQuantity();

            if (quantity == null ||
                    quantity <= 0) {

                throw new RuntimeException(
                        "Invalid product quantity"
                );
            }

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                throw new RuntimeException(
                        "Product is currently unavailable: "
                                + product.getName()
                );
            }

            Integer stock =
                    product.getStock();

            if (stock == null ||
                    stock <= 0) {

                throw new RuntimeException(
                        "Out of stock: "
                                + product.getName()
                );
            }

            if (quantity > stock) {

                throw new RuntimeException(
                        "Only " +
                                stock +
                                " item(s) available for: " +
                                product.getName()
                );
            }

            if (product.getPrice() == null) {

                throw new RuntimeException(
                        "Product price not found"
                );
            }

            double price =
                    product.getPrice();

            productTotal +=
                    price * quantity;
        }

        double deliveryFee =
                productTotal >= 500
                        ? 0
                        : 50;

        double totalAmount =
                productTotal + deliveryFee;

        order.setTotalAmount(
                totalAmount
        );

        order.setPaymentStatus(
                "PENDING"
        );

        if (paymentMethod.equals("RAZORPAY")) {

            order.setOrderStatus(
                    "PAYMENT_PENDING"
            );

        } else {

            order.setOrderStatus(
                    "PLACED"
            );
        }

        order.setCreatedAt(
                LocalDateTime.now()
        );

        for (CartItem cartItem : cartItems) {

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(
                    cartItem.getProduct()
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setPrice(
                    cartItem.getProduct().getPrice()
            );

            order.getItems().add(
                    orderItem
            );
        }

        Order savedOrder =
                orderRepository.save(order);

        if (paymentMethod.equals("COD")) {

            for (OrderItem orderItem :
                    savedOrder.getItems()) {

                if (orderItem.getProduct() == null) {

                    throw new RuntimeException(
                            "Product not found in order"
                    );
                }

                productService.decreaseStock(
                        orderItem.getProduct().getId(),
                        orderItem.getQuantity()
                );
            }

            cartItems.clear();

            cartRepository.save(cart);
        }

        return savedOrder;
    }

    // =========================================================
    // CONFIRM RAZORPAY PAYMENT
    // =========================================================

    @Transactional
    public Order confirmRazorpayPayment(Long orderId) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        User authenticatedUser =
                getAuthenticatedUser();

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        if (order.getUser() == null ||
                order.getUser().getId() == null ||
                !order.getUser()
                        .getId()
                        .equals(authenticatedUser.getId())) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        if (!"RAZORPAY".equalsIgnoreCase(
                order.getPaymentMethod())) {

            throw new RuntimeException(
                    "This order is not a Razorpay order"
            );
        }

        if ("PAID".equalsIgnoreCase(
                order.getPaymentStatus())) {

            return order;
        }

        if ("CANCELLED".equalsIgnoreCase(
                order.getOrderStatus())) {

            throw new RuntimeException(
                    "Cancelled order cannot be completed"
            );
        }

        for (OrderItem orderItem :
                order.getItems()) {

            Product product =
                    orderItem.getProduct();

            if (product == null) {

                throw new RuntimeException(
                        "Product not found in order"
                );
            }

            Integer quantity =
                    orderItem.getQuantity();

            if (quantity == null ||
                    quantity <= 0) {

                throw new RuntimeException(
                        "Invalid order quantity"
                );
            }

            Integer stock =
                    product.getStock();

            if (stock == null ||
                    stock <= 0) {

                throw new RuntimeException(
                        "Out of stock: "
                                + product.getName()
                );
            }

            if (quantity > stock) {

                throw new RuntimeException(
                        "Insufficient stock for: "
                                + product.getName()
                );
            }

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                throw new RuntimeException(
                        "Product is unavailable: "
                                + product.getName()
                );
            }
        }

        for (OrderItem orderItem :
                order.getItems()) {

            Product product =
                    orderItem.getProduct();

            productService.decreaseStock(
                    product.getId(),
                    orderItem.getQuantity()
            );
        }

        order.setPaymentStatus(
                "PAID"
        );

        order.setOrderStatus(
                "PLACED"
        );

        Order savedOrder =
                orderRepository.save(order);

        Cart cart =
                cartRepository.findByUserId(
                        authenticatedUser.getId()
                ).orElse(null);

        if (cart != null) {

            cart.getItems().clear();

            cartRepository.save(cart);
        }

        return savedOrder;
    }

    // =========================================================
    // GET USER ORDERS
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    public List<Order> getUserOrders(Long userId) {

        User authenticatedUser =
                getAuthenticatedUser();

        if (userId == null) {

            throw new RuntimeException(
                    "User ID is required"
            );
        }

        if (!authenticatedUser
                .getId()
                .equals(userId)) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        return orderRepository
                .findByUserId(userId);
    }

    // =========================================================
    // GET SINGLE ORDER
    // =========================================================

    @PreAuthorize("isAuthenticated()")
    public Order getOrder(Long orderId) {

        User authenticatedUser =
                getAuthenticatedUser();

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        // -----------------------------------------------------
        // ADMIN
        // -----------------------------------------------------

        if (authenticatedUser.getRole()
                == User.Role.ADMIN ||
                authenticatedUser.getRole()
                        == User.Role.SUPER_ADMIN) {

            return order;
        }

        // -----------------------------------------------------
        // NORMAL USER
        // -----------------------------------------------------

        if (order.getUser() == null ||
                order.getUser().getId() == null ||
                !order.getUser()
                        .getId()
                        .equals(
                                authenticatedUser.getId()
                        )) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        return order;
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Order cancelOrder(Long orderId) {

        User authenticatedUser =
                getAuthenticatedUser();

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        if (order.getUser() == null ||
                order.getUser().getId() == null ||
                !order.getUser()
                        .getId()
                        .equals(
                                authenticatedUser.getId()
                        )) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        String currentStatus =
                order.getOrderStatus();

        if ("CANCELLED".equalsIgnoreCase(
                currentStatus)) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        if ("SHIPPED".equalsIgnoreCase(
                currentStatus)
                ||
                "OUT_FOR_DELIVERY"
                        .equalsIgnoreCase(
                                currentStatus)
                ||
                "DELIVERED"
                        .equalsIgnoreCase(
                                currentStatus)) {

            throw new RuntimeException(
                    "Order cannot be cancelled at this stage"
            );
        }

        order.setOrderStatus(
                "CANCELLED"
        );

        return orderRepository.save(order);
    }

    // =========================================================
    // ADMIN - UPDATE ORDER
    //
    // ADMIN + SUPER_ADMIN
    // =========================================================

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Order updateOrderFromAdmin(
            Long orderId,
            AdminOrderUpdateRequest request) {

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        if (request == null) {

            throw new RuntimeException(
                    "Order update request is required"
            );
        }

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        String status =
                request.getOrderStatus();

        if (status == null ||
                status.trim().isEmpty()) {

            throw new RuntimeException(
                    "Order status is required"
            );
        }

        status =
                status.trim()
                        .toUpperCase();

        Set<String> allowedStatuses =
                Set.of(
                        "PAYMENT_PENDING",
                        "PLACED",
                        "CONFIRMED",
                        "PROCESSING",
                        "SHIPPED",
                        "OUT_FOR_DELIVERY",
                        "DELIVERED",
                        "COMPLETED",
                        "CANCELLED"
                );

        if (!allowedStatuses.contains(status)) {

            throw new RuntimeException(
                    "Invalid order status: "
                            + status
            );
        }

        String shippingMode =
                request.getShippingMode();

        if (shippingMode != null &&
                !shippingMode.trim().isEmpty()) {

            shippingMode =
                    shippingMode.trim()
                            .toUpperCase();

            Set<String> allowedShippingModes =
                    Set.of(
                            "SHIPROCKET",
                            "GOSWIFT",
                            "OTHER"
                    );

            if (!allowedShippingModes.contains(
                    shippingMode)) {

                throw new RuntimeException(
                        "Invalid shipping mode: "
                                + shippingMode
                );
            }

            order.setShippingMode(
                    shippingMode
            );

        } else {

            order.setShippingMode(null);
        }

        String trackingId =
                request.getTrackingId();

        if (trackingId != null) {

            trackingId =
                    trackingId.trim();

            if (trackingId.isEmpty()) {

                trackingId = null;
            }
        }

        order.setTrackingId(
                trackingId
        );

        order.setOrderStatus(
                status
        );

        return orderRepository.save(order);
    }

    // =========================================================
    // ADMIN - GET ALL ORDERS
    //
    // ADMIN + SUPER_ADMIN
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }
}