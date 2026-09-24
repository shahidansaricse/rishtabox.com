package com.rishtabox.backend.dto.admin;

public class AdminOrderUpdateRequest {

    private String orderStatus;

    private String shippingMode;

    private String trackingId;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminOrderUpdateRequest() {
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
}

