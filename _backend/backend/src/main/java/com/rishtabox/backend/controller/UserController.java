package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =====================================================
    // SIGNUP
    // =====================================================

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody User user) {

        try {

            User savedUser =
                    userService.registerUser(user);

            return ResponseEntity.ok(savedUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User loginRequest) {

        try {

            User user =
                    userService.loginUser(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    );

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =====================================================
    // ADMIN - USER COUNT
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/count")
    public ResponseEntity<Long> getUserCount() {

        return ResponseEntity.ok(
                userService.getUserCount()
        );
    }

    // =====================================================
    // ADMIN - GET ALL USERS
    //
    // ADMIN + SUPER_ADMIN
    // =====================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }
}