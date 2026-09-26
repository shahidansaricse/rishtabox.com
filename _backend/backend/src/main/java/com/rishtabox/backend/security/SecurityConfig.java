package com.rishtabox.backend.config;

import com.rishtabox.backend.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // CORS CONFIGURATION
    // =====================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        // -------------------------------------------------
        // ALLOWED FRONTEND ORIGINS
        // -------------------------------------------------

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:63342",
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "https://rishtabox.com",
                        "https://www.rishtabox.com"
                )
        );


        // -------------------------------------------------
        // ALLOWED HTTP METHODS
        // -------------------------------------------------

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );


        // -------------------------------------------------
        // ALLOWED HEADERS
        // -------------------------------------------------

        configuration.setAllowedHeaders(
                List.of("*")
        );


        // -------------------------------------------------
        // CREDENTIALS
        // -------------------------------------------------

        configuration.setAllowCredentials(true);


        // -------------------------------------------------
        // EXPOSED HEADERS
        // -------------------------------------------------

        configuration.setExposedHeaders(
                List.of("Authorization")
        );


        // -------------------------------------------------
        // REGISTER CORS CONFIGURATION
        // -------------------------------------------------

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }


    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf ->
                        csrf.disable()
                )


                // =================================================
                // CORS
                // =================================================

                .cors(cors -> {
                })


                // =================================================
                // SESSION
                // =================================================
                //
                // JWT authentication is stateless.
                //
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth


                        // =========================================
                        // PUBLIC AUTH
                        // =========================================

                        .requestMatchers(
                                "/api/auth/**",
                                "/api/users/signup",
                                "/api/users/login"
                        ).permitAll()


                        // =========================================
                        // USER ADMIN APIs
                        // =========================================
                        //
                        // Authentication is required here.
                        //
                        // Exact permissions are handled by:
                        //
                        // @PreAuthorize(...)
                        //
                        // in UserController.
                        //
                        // =========================================

                        .requestMatchers(
                                "/api/users/admin/**"
                        ).authenticated()


                        // =========================================
                        // PUBLIC PRODUCTS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC CATEGORIES
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC FESTIVALS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/festivals/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC RELATIONSHIPS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/relationships/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC BLOGS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/blogs/**"
                        ).permitAll()


                        // =========================================
                        // PUBLIC REVIEWS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews",
                                "/api/reviews/product/**"
                        ).permitAll()


                        // =========================================
                        // CREATE REVIEW
                        // =========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/reviews"
                        ).authenticated()


                        // =========================================
                        // ADMIN APIs
                        //
                        // ADMIN + SUPER_ADMIN
                        // =========================================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "SUPER_ADMIN"
                        )


                        // =========================================
                        // PAYMENT KEY
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/payments/key"
                        ).permitAll()


                        // =========================================
                        // PAYMENT
                        // =========================================

                        .requestMatchers(
                                "/api/payments/create-order",
                                "/api/payments/verify"
                        ).authenticated()


                        // =========================================
                        // CREATE ORDER
                        // =========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orders",
                                "/api/orders/**"
                        ).authenticated()


                        // =========================================
                        // READ ORDERS
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders",
                                "/api/orders/**"
                        ).authenticated()


                        // =========================================
                        // EVERYTHING ELSE
                        // =========================================

                        .anyRequest().permitAll()
                )


                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}