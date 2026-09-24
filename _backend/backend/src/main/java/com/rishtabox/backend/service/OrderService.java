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
    //
    // COD:
    //   Order create + stock decrease immediately
    //
    // RAZORPAY:
    //   Order create + stock validate
    //   Stock decrease only after payment verification
    // =========================================================

    @Transactional
    public Order createOrder(OrderRequest request) {

        if (request == null) {

            throw new RuntimeException(
                    "Order request is required"
            );
        }

        User user = getAuthenticatedUser();

        // -----------------------------------------------------
        // GET USER CART
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // PAYMENT METHOD
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // CREATE ORDER
        // -----------------------------------------------------

        Order order = new Order();

        order.setUser(user);

        order.setPaymentMethod(
                paymentMethod
        );

        // -----------------------------------------------------
        // CALCULATE PRODUCT TOTAL
        // + VALIDATE STOCK
        // -----------------------------------------------------

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

            // -------------------------------------------------
            // PRODUCT ACTIVE CHECK
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                throw new RuntimeException(
                        "Product is currently unavailable: "
                                + product.getName()
                );
            }

            // -------------------------------------------------
            // PRODUCT STOCK CHECK
            // -------------------------------------------------

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

            // -------------------------------------------------
            // PRICE CHECK
            // -------------------------------------------------

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

        // -----------------------------------------------------
        // DELIVERY CHARGE
        //
        // ₹500 or above = FREE
        // Below ₹500 = ₹50
        // -----------------------------------------------------

        double deliveryFee =
                productTotal >= 500
                        ? 0
                        : 50;

        double totalAmount =
                productTotal + deliveryFee;

        // -----------------------------------------------------
        // ORDER TOTAL
        // -----------------------------------------------------

        order.setTotalAmount(
                totalAmount
        );

        // -----------------------------------------------------
        // PAYMENT STATUS
        // -----------------------------------------------------

        if (paymentMethod.equals("COD")) {

            order.setPaymentStatus(
                    "PENDING"
            );

        } else {

            order.setPaymentStatus(
                    "PENDING"
            );
        }

        // -----------------------------------------------------
        // ORDER STATUS
        // -----------------------------------------------------

        if (paymentMethod.equals("RAZORPAY")) {

            order.setOrderStatus(
                    "PAYMENT_PENDING"
            );

        } else {

            order.setOrderStatus(
                    "PLACED"
            );
        }

        // -----------------------------------------------------
        // CREATED TIME
        // -----------------------------------------------------

        order.setCreatedAt(
                LocalDateTime.now()
        );

        // -----------------------------------------------------
        // ORDER ITEMS
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // SAVE ORDER
        // -----------------------------------------------------

        Order savedOrder =
                orderRepository.save(order);

        // -----------------------------------------------------
        // COD
        //
        // COD order is placed successfully,
        // so decrease stock now.
        // -----------------------------------------------------

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

            // -------------------------------------------------
            // CLEAR CART
            // -------------------------------------------------

            cartItems.clear();

            cartRepository.save(cart);
        }

        // -----------------------------------------------------
        // RAZORPAY
        //
        // Do NOT decrease stock here.
        // Payment is still pending.
        //
        // Cart is also kept until payment succeeds.
        // -----------------------------------------------------

        return savedOrder;
    }

    // =========================================================
    // CONFIRM RAZORPAY PAYMENT
    //
    // IMPORTANT:
    // Call this method ONLY after Razorpay signature
    // verification succeeds.
    //
    // It:
    // 1. checks order
    // 2. prevents duplicate stock deduction
    // 3. decreases stock
    // 4. marks payment PAID
    // 5. changes order status to PLACED
    // 6. clears user's cart
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

        // -----------------------------------------------------
        // ORDER OWNERSHIP
        // -----------------------------------------------------

        if (order.getUser() == null ||
                order.getUser().getId() == null ||
                !order.getUser()
                        .getId()
                        .equals(authenticatedUser.getId())) {

            throw new RuntimeException(
                    "Access denied"
            );
        }

        // -----------------------------------------------------
        // CHECK PAYMENT METHOD
        // -----------------------------------------------------

        if (!"RAZORPAY".equalsIgnoreCase(
                order.getPaymentMethod())) {

            throw new RuntimeException(
                    "This order is not a Razorpay order"
            );
        }

        // -----------------------------------------------------
        // PREVENT DUPLICATE STOCK REDUCTION
        // -----------------------------------------------------

        if ("PAID".equalsIgnoreCase(
                order.getPaymentStatus())) {

            return order;
        }

        // -----------------------------------------------------
        // VERIFY ORDER STATE
        // -----------------------------------------------------

        if ("CANCELLED".equalsIgnoreCase(
                order.getOrderStatus())) {

            throw new RuntimeException(
                    "Cancelled order cannot be completed"
            );
        }

        // -----------------------------------------------------
        // CHECK EVERY PRODUCT AGAIN
        // -----------------------------------------------------

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

            // -------------------------------------------------
            // PRODUCT MUST STILL BE ACTIVE
            // -------------------------------------------------

            if (!Boolean.TRUE.equals(
                    product.getActive())) {

                throw new RuntimeException(
                        "Product is unavailable: "
                                + product.getName()
                );
            }
        }

        // -----------------------------------------------------
        // DECREASE STOCK
        // -----------------------------------------------------

        for (OrderItem orderItem :
                order.getItems()) {

            Product product =
                    orderItem.getProduct();

            productService.decreaseStock(
                    product.getId(),
                    orderItem.getQuantity()
            );
        }

        // -----------------------------------------------------
        // PAYMENT SUCCESS
        // -----------------------------------------------------

        order.setPaymentStatus(
                "PAID"
        );

        order.setOrderStatus(
                "PLACED"
        );

        Order savedOrder =
                orderRepository.save(order);

        // -----------------------------------------------------
        // CLEAR USER CART
        // -----------------------------------------------------

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
                == User.Role.ADMIN) {

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

        // -----------------------------------------------------
        // ONLY OWNER CAN CANCEL
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

        String currentStatus =
                order.getOrderStatus();

        // -----------------------------------------------------
        // ALREADY CANCELLED
        // -----------------------------------------------------

        if ("CANCELLED".equalsIgnoreCase(
                currentStatus)) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        // -----------------------------------------------------
        // SHIPPED / OUT FOR DELIVERY / DELIVERED
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // CANCEL
        // -----------------------------------------------------

        order.setOrderStatus(
                "CANCELLED"
        );

        return orderRepository.save(order);
    }

    // =========================================================
    // ADMIN - UPDATE ORDER
    //
    // Admin can edit:
    // 1. Order Status
    // 2. Shipping Mode
    // 3. Tracking ID
    //
    // Payment Method and Payment Status
    // are intentionally NOT changed.
    // =========================================================

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateOrderFromAdmin(
            Long orderId,
            AdminOrderUpdateRequest request) {

        // -----------------------------------------------------
        // ORDER ID CHECK
        // -----------------------------------------------------

        if (orderId == null) {

            throw new RuntimeException(
                    "Order ID is required"
            );
        }

        // -----------------------------------------------------
        // REQUEST CHECK
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Order update request is required"
            );
        }

        // -----------------------------------------------------
        // FIND ORDER
        // -----------------------------------------------------

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        // -----------------------------------------------------
        // ORDER STATUS
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // SHIPPING MODE
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // TRACKING ID
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // SET ORDER STATUS
        // -----------------------------------------------------

        order.setOrderStatus(
                status
        );

        // -----------------------------------------------------
        // SAVE ORDER
        // -----------------------------------------------------

        return orderRepository.save(order);
    }

    // =========================================================
    // ADMIN - GET ALL ORDERS
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }
}

