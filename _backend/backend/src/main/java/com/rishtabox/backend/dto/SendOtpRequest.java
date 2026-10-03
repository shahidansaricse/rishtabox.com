package com.rishtabox.backend.dto;

public class SendOtpRequest {

    private String email;

    public SendOtpRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}