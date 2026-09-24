package com.rishtabox.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =====================================================
    // USER
    // =====================================================

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    // =====================================================
    // ORDER AMOUNT
    // =====================================================

    private Double totalAmount;


    // =====================================================
    // PAYMENT
    // =====================================================

    private String paymentMethod;

    private String paymentStatus;


    // =====================================================
    // ORDER STATUS
    // =====================================================

    private String orderStatus;


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
    // DATE
    // =====================================================

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
    private List<OrderItem> items = new ArrayList<>();


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
    // CREATED AT
    // =====================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // =====================================================
    // ITEMS
    // =====================================================

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }


    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

