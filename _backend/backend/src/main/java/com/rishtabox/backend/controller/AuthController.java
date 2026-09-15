package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.AuthResponse;
import com.rishtabox.backend.dto.LoginRequest;
import com.rishtabox.backend.dto.RegisterRequest;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody RegisterRequest request) {

        User user = authService.register(request);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}