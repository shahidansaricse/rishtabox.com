package com.rishtabox.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =====================================================
    // ORDER NUMBER
    // =====================================================

    @Column(
            name = "order_number",
            length = 100,
            unique = true,
            nullable = false
    )
    private String orderNumber;


    // =====================================================
    // USER
    // =====================================================

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    @JsonIgnore
    private User user;


    // =====================================================
    // ORDER AMOUNT
    // =====================================================

    @Column(name = "total_amount")
    private Double totalAmount;


    // =====================================================
    // PAYMENT
    // =====================================================

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_status")
    private String paymentStatus;


    // =====================================================
    // ORDER STATUS
    // =====================================================

    @Column(name = "order_status")
    private String orderStatus;


    // =====================================================
    // SHIPPING ADDRESS
    // =====================================================

    @Column(
            name = "shipping_address",
            length = 500
    )
    private String shippingAddress;

    @Column(
            name = "shipping_city",
            length = 100
    )
    private String shippingCity;

    @Column(
            name = "shipping_state",
            length = 100
    )
    private String shippingState;

    @Column(
            name = "shipping_pincode",
            length = 10
    )
    private String shippingPincode;


    // =====================================================
    // SHIPPING
    // =====================================================

    @Column(
            name = "shipping_mode",
            length = 50
    )
    private String shippingMode;

    @Column(
            name = "tracking_id",
            length = 100
    )
    private String trackingId;


    // =====================================================
    // SHIPROCKET
    // =====================================================

    @Column(name = "shiprocket_order_id")
    private Long shiprocketOrderId;

    @Column(name = "shiprocket_shipment_id")
    private Long shiprocketShipmentId;

    @Column(
            name = "awb_code",
            length = 100
    )
    private String awbCode;

    @Column(
            name = "courier_name",
            length = 150
    )
    private String courierName;

    @Column(
            name = "shipment_status",
            length = 100
    )
    private String shipmentStatus;

    @Column(
            name = "tracking_url",
            length = 500
    )
    private String trackingUrl;


    // =====================================================
    // DATE
    // =====================================================

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    // =====================================================
    // ORDER ITEMS
    // =====================================================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<OrderItem> items =
            new ArrayList<>();


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Order() {
    }


    // =====================================================
    // ID
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    // =====================================================
    // ORDER NUMBER
    // =====================================================

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    // =====================================================
    // USER
    // =====================================================

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    // =====================================================
    // TOTAL AMOUNT
    // =====================================================

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }


    // =====================================================
    // PAYMENT METHOD
    // =====================================================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    // =====================================================
    // PAYMENT STATUS
    // =====================================================

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    // =====================================================
    // ORDER STATUS
    // =====================================================

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }


    // =====================================================
    // SHIPPING ADDRESS
    // =====================================================

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    // =====================================================
    // SHIPPING CITY
    // =====================================================

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }


    // =====================================================
    // SHIPPING STATE
    // =====================================================

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }


    // =====================================================
    // SHIPPING PINCODE
    // =====================================================

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }


    // =====================================================
    // SHIPPING MODE
    // =====================================================

    public String getShippingMode() {
        return shippingMode;
    }

    public void setShippingMode(String shippingMode) {
        this.shippingMode = shippingMode;
    }


    // =====================================================
    // TRACKING ID
    // =====================================================

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }


    // =====================================================
    // SHIPROCKET ORDER ID
    // =====================================================

    public Long getShiprocketOrderId() {
        return shiprocketOrderId;
    }

    public void setShiprocketOrderId(
            Long shiprocketOrderId) {

        this.shiprocketOrderId =
                shiprocketOrderId;
    }


    // =====================================================
    // SHIPROCKET SHIPMENT ID
    // =====================================================

    public Long getShiprocketShipmentId() {
        return shiprocketShipmentId;
    }

    public void setShiprocketShipmentId(
            Long shiprocketShipmentId) {

        this.shiprocketShipmentId =
                shiprocketShipmentId;
    }


    // =====================================================
    // AWB
    // =====================================================

    public String getAwbCode() {
        return awbCode;
    }

    public void setAwbCode(String awbCode) {
        this.awbCode = awbCode;
    }


    // =====================================================
    // COURIER
    // =====================================================

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }


    // =====================================================
    // SHIPMENT STATUS
    // =====================================================

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(
            String shipmentStatus) {

        this.shipmentStatus =
                shipmentStatus;
    }


    // =====================================================
    // TRACKING URL
    // =====================================================

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }


    // =====================================================
    // CREATED AT
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    // =====================================================
    // ITEMS
    // =====================================================

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(
            List<OrderItem> items) {

        this.items = items;
    }


    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        // -------------------------------------------------
        // CREATED DATE
        // -------------------------------------------------

        if (createdAt == null) {

            createdAt =
                    LocalDateTime.now();
        }

        // -------------------------------------------------
        // UNIQUE ORDER NUMBER
        // -------------------------------------------------

        if (orderNumber == null ||
                orderNumber.isBlank()) {

            orderNumber =
                    "ORD"
                            + System.currentTimeMillis()
                            + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 8)
                            .toUpperCase();
        }

        // -------------------------------------------------
        // ORDER STATUS
        // -------------------------------------------------

        if (orderStatus == null ||
                orderStatus.isBlank()) {

            orderStatus =
                    "PLACED";
        }

        // -------------------------------------------------
        // PAYMENT STATUS
        // -------------------------------------------------

        if (paymentStatus == null ||
                paymentStatus.isBlank()) {

            if ("COD".equalsIgnoreCase(
                    paymentMethod)) {

                paymentStatus =
                        "PENDING";

            } else {

                paymentStatus =
                        "PAID";
            }
        }

        // -------------------------------------------------
        // SHIPMENT STATUS
        // -------------------------------------------------

        if (shipmentStatus == null ||
                shipmentStatus.isBlank()) {

            shipmentStatus =
                    "NOT_CREATED";
        }
    }
}