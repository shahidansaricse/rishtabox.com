package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.AuthResponse;
import com.rishtabox.backend.dto.LoginPasswordRequest;
import com.rishtabox.backend.dto.LoginRequest;
import com.rishtabox.backend.dto.OtpRequest;
import com.rishtabox.backend.dto.RegisterRequest;
import com.rishtabox.backend.dto.SendOtpRequest;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.service.AuthService;
import com.rishtabox.backend.service.OtpService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(
            AuthService authService,
            OtpService otpService) {

        this.authService = authService;
        this.otpService = otpService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            User user = authService.register(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // ADMIN REGISTER
    // =========================================================

    @PostMapping("/admin-register")
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request) {

        try {

            User user =
                    authService.registerAdmin(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // NORMAL LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            AuthResponse response =
                    authService.login(request);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // EMAIL OTP - REGISTER - SEND
    // =========================================================

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegisterOtp(
            @RequestBody SendOtpRequest request) {

        try {

            if (request == null ||
                    request.getEmail() == null ||
                    request.getEmail().isBlank()) {

                throw new RuntimeException(
                        "Email is required"
                );
            }

            otpService.sendOtp(
                    request.getEmail(),
                    "REGISTER"
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Email OTP sent successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // EMAIL OTP - REGISTER - VERIFY
    // =========================================================

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyRegisterOtp(
            @RequestBody OtpRequest request) {

        try {

            if (request == null) {

                throw new RuntimeException(
                        "OTP data is required"
                );
            }

            otpService.verifyOtp(
                    request.getEmail(),
                    request.getOtp(),
                    "REGISTER"
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Email OTP verified successfully",
                            "verified",
                            true
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage(),
                                    "verified",
                                    false
                            )
                    );
        }
    }

    // =========================================================
    // EMAIL OTP - LOGIN - SEND
    // =========================================================

    @PostMapping("/login/send-otp")
    public ResponseEntity<?> sendLoginOtp(
            @RequestBody SendOtpRequest request) {

        try {

            if (request == null ||
                    request.getEmail() == null ||
                    request.getEmail().isBlank()) {

                throw new RuntimeException(
                        "Email is required"
                );
            }

            otpService.sendOtp(
                    request.getEmail(),
                    "LOGIN"
            );

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Email OTP sent successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // EMAIL OTP - LOGIN - VERIFY
    // =========================================================

    @PostMapping("/login/verify-otp")
    public ResponseEntity<?> verifyLoginOtp(
            @RequestBody OtpRequest request) {

        try {

            if (request == null) {

                throw new RuntimeException(
                        "OTP data is required"
                );
            }

            otpService.verifyOtp(
                    request.getEmail(),
                    request.getOtp(),
                    "LOGIN"
            );

            // OTP verification only.
            // DO NOT LOGIN HERE.

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Email OTP verified successfully",
                            "verified",
                            true
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage(),
                                    "verified",
                                    false
                            )
                    );
        }
    }

    // =========================================================
    // LOGIN AFTER OTP + PASSWORD
    // =========================================================

    @PostMapping("/login/password")
    public ResponseEntity<?> loginAfterOtp(
            @RequestBody LoginPasswordRequest request) {

        try {

            if (request == null) {

                throw new RuntimeException(
                        "Login data is required"
                );
            }

            AuthResponse response =
                    authService.loginAfterOtp(
                            request.getEmail(),
                            request.getPassword()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}