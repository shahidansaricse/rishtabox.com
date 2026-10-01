package com.rishtabox.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiprocketConfig {

    @Value("${shiprocket.base-url}")
    private String baseUrl;

    @Value("${shiprocket.email}")
    private String email;

    @Value("${shiprocket.password}")
    private String password;


    // =====================================================
    // GET BASE URL
    // =====================================================

    public String getBaseUrl() {
        return baseUrl;
    }


    // =====================================================
    // GET EMAIL
    // =====================================================

    public String getEmail() {
        return email;
    }


    // =====================================================
    // GET PASSWORD
    // =====================================================

    public String getPassword() {
        return password;
    }
}