package com.rishtabox.backend.dto;

public class OrderRequest {

    private String paymentMethod;

    public OrderRequest() {
    }

    public OrderRequest(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}