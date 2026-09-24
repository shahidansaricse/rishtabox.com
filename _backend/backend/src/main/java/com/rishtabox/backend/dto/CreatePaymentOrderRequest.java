package com.rishtabox.backend.dto;

public class CreatePaymentOrderRequest {

    private Long orderId;

    public CreatePaymentOrderRequest() {
    }

    public CreatePaymentOrderRequest(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}