package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.AuthResponse;
import com.rishtabox.backend.dto.LoginRequest;
import com.rishtabox.backend.dto.RegisterRequest;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;
import com.rishtabox.backend.security.CustomUserDetailsService;
import com.rishtabox.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // =========================================================
    // STORE REGISTER
    // PUBLIC SIGNUP = USER
    // =========================================================

    public User register(RegisterRequest request) {

        validateRegisterRequest(request);

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        String phone =
                request.getPhone()
                        .trim();

        // -----------------------------------------------------
        // CHECK DUPLICATE EMAIL
        // -----------------------------------------------------

        if (userRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // -----------------------------------------------------
        // CHECK DUPLICATE PHONE
        // -----------------------------------------------------

        if (userRepository.existsByPhone(phone)) {

            throw new RuntimeException(
                    "Phone already registered"
            );
        }

        // -----------------------------------------------------
        // CREATE USER
        // -----------------------------------------------------

        User user = new User();

        user.setName(
                request.getName().trim()
        );

        user.setEmail(email);

        user.setPhone(phone);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // -----------------------------------------------------
        // PUBLIC REGISTRATION ALWAYS CREATES USER
        // -----------------------------------------------------

        user.setRole(
                User.Role.USER
        );

        return userRepository.save(user);
    }

    // =========================================================
    // ADMIN REGISTER
    //
    // IMPORTANT:
    // This method should NOT be exposed as a public signup.
    //
    // Admin creation will be moved to the SUPER_ADMIN
    // role-management system.
    // =========================================================

    @Deprecated
    public User registerAdmin(RegisterRequest request) {

        throw new RuntimeException(
                "Public admin registration is disabled. " +
                        "Only a SUPER_ADMIN can create an ADMIN."
        );
    }

    // =========================================================
    // COMMON REGISTER VALIDATION
    // =========================================================

    private void validateRegisterRequest(
            RegisterRequest request) {

        if (request == null) {

            throw new RuntimeException(
                    "Registration data is required"
            );
        }

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException(
                    "Name is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        if (request.getPhone() == null ||
                request.getPhone().isBlank()) {

            throw new RuntimeException(
                    "Phone is required"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    public AuthResponse login(LoginRequest request) {

        // -----------------------------------------------------
        // VALIDATE LOGIN REQUEST
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Login data is required"
            );
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // -----------------------------------------------------
        // NORMALIZE EMAIL
        // -----------------------------------------------------

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        // -----------------------------------------------------
        // FIND USER
        // -----------------------------------------------------

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );

        // -----------------------------------------------------
        // CHECK PASSWORD
        // -----------------------------------------------------

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        // -----------------------------------------------------
        // LOAD SPRING SECURITY USER
        // -----------------------------------------------------

        UserDetails userDetails =
                userDetailsService
                        .loadUserByUsername(
                                user.getEmail()
                        );

        // -----------------------------------------------------
        // GENERATE JWT
        //
        // JwtService now stores:
        //
        // email
        // role
        //
        // Example:
        //
        // SUPER_ADMIN
        // ADMIN
        // USER
        // -----------------------------------------------------

        String token =
                jwtService.generateToken(
                        userDetails
                );

        // -----------------------------------------------------
        // RETURN LOGIN RESPONSE
        // -----------------------------------------------------

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                token
        );
    }
}