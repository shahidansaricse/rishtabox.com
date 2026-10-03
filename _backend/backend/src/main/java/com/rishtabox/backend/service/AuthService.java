package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.AuthResponse;
import com.rishtabox.backend.dto.LoginRequest;
import com.rishtabox.backend.dto.RegisterRequest;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;
import com.rishtabox.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
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
        // CHECK EMAIL
        // -----------------------------------------------------

        if (userRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // -----------------------------------------------------
        // CHECK PHONE
        // Mobile number is stored, but NO mobile OTP
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
                request.getName()
                        .trim()
        );

        user.setEmail(email);

        // Mobile number is saved normally
        // No OTP verification is performed
        user.setPhone(phone);

        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // -----------------------------------------------------
        // ROLE
        // -----------------------------------------------------

        user.setRole(
                User.Role.USER
        );

        // -----------------------------------------------------
        // ACTIVE
        // -----------------------------------------------------

        user.setActive(true);

        // -----------------------------------------------------
        // TOKEN VERSION
        // -----------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);
        }

        // -----------------------------------------------------
        // SAVE USER
        // -----------------------------------------------------

        return userRepository.save(user);
    }


    // =========================================================
    // ADMIN REGISTER
    // =========================================================

    @Deprecated
    public User registerAdmin(
            RegisterRequest request) {

        throw new RuntimeException(
                "Public admin registration is disabled. " +
                        "Only a SUPER_ADMIN can create an ADMIN."
        );
    }


    // =========================================================
    // NORMAL LOGIN
    // =========================================================

    public AuthResponse login(
            LoginRequest request) {

        // -----------------------------------------------------
        // VALIDATE REQUEST
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Login data is required"
            );
        }

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

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
        // ACCOUNT STATUS
        // -----------------------------------------------------

        if (!user.isActive()) {

            throw new RuntimeException(
                    "Your account has been blocked"
            );
        }

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
        // CREATE JWT RESPONSE
        // -----------------------------------------------------

        return createAuthResponse(user);
    }


    // =========================================================
    // LOGIN AFTER EMAIL OTP VERIFICATION + PASSWORD
    // =========================================================
    //
    // Flow:
    //
    // Email
    //   ↓
    // Email OTP
    //   ↓
    // Verify Email OTP
    //   ↓
    // Password
    //   ↓
    // This method
    //   ↓
    // JWT
    //
    // Mobile OTP is NOT used.
    // =========================================================

    public AuthResponse loginAfterOtp(
            String email,
            String password) {

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        if (password == null ||
                password.isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        email =
                email.trim()
                        .toLowerCase();

        // -----------------------------------------------------
        // FIND USER
        // -----------------------------------------------------

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        // -----------------------------------------------------
        // ACCOUNT STATUS
        // -----------------------------------------------------

        if (!user.isActive()) {

            throw new RuntimeException(
                    "Your account has been blocked"
            );
        }

        // -----------------------------------------------------
        // PASSWORD CHECK
        // -----------------------------------------------------

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        // -----------------------------------------------------
        // CREATE JWT
        // -----------------------------------------------------

        return createAuthResponse(user);
    }


    // =========================================================
    // CREATE JWT RESPONSE
    // =========================================================

    private AuthResponse createAuthResponse(
            User user) {

        // -----------------------------------------------------
        // TOKEN VERSION
        // -----------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);

            userRepository.save(user);
        }

        // -----------------------------------------------------
        // GENERATE JWT
        // -----------------------------------------------------

        String token =
                jwtService.generateToken(user);

        // -----------------------------------------------------
        // RETURN AUTH RESPONSE
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


    // =========================================================
    // VALIDATE REGISTER REQUEST
    // =========================================================

    private void validateRegisterRequest(
            RegisterRequest request) {

        // -----------------------------------------------------
        // REQUEST
        // -----------------------------------------------------

        if (request == null) {

            throw new RuntimeException(
                    "Registration data is required"
            );
        }

        // -----------------------------------------------------
        // NAME
        // -----------------------------------------------------

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException(
                    "Name is required"
            );
        }

        // -----------------------------------------------------
        // EMAIL
        // -----------------------------------------------------

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        // -----------------------------------------------------
        // PHONE
        // -----------------------------------------------------
        //
        // Phone is REQUIRED.
        // Phone is stored in DB.
        // NO mobile OTP is required.
        // -----------------------------------------------------

        if (request.getPhone() == null ||
                request.getPhone().isBlank()) {

            throw new RuntimeException(
                    "Phone is required"
            );
        }

        // -----------------------------------------------------
        // PASSWORD
        // -----------------------------------------------------

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }
    }
}