package com.rishtabox.backend.dto;

public class OrderRequest {

    private String paymentMethod;

    private String shippingAddress;

    private String shippingCity;

    private String shippingState;

    private String shippingPincode;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================

    public OrderRequest() {
    }


    // ==========================================
    // FULL CONSTRUCTOR
    // ==========================================

    public OrderRequest(
            String paymentMethod,
            String shippingAddress,
            String shippingCity,
            String shippingState,
            String shippingPincode
    ) {

        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingState = shippingState;
        this.shippingPincode = shippingPincode;
    }


    // ==========================================
    // PAYMENT METHOD
    // ==========================================

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    // ==========================================
    // SHIPPING ADDRESS
    // ==========================================

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    // ==========================================
    // SHIPPING CITY
    // ==========================================

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }


    // ==========================================
    // SHIPPING STATE
    // ==========================================

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }


    // ==========================================
    // SHIPPING PINCODE
    // ==========================================

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }
}