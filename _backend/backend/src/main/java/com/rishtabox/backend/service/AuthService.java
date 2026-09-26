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


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    // =========================================================
    // STORE REGISTER
    // PUBLIC SIGNUP = USER
    // =========================================================

    public User register(RegisterRequest request) {

        // -----------------------------------------------------
        // VALIDATE REQUEST
        // -----------------------------------------------------

        validateRegisterRequest(request);


        // -----------------------------------------------------
        // NORMALIZE EMAIL
        // -----------------------------------------------------

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        // -----------------------------------------------------
        // NORMALIZE PHONE
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // NEW ACCOUNT IS ACTIVE
        // -----------------------------------------------------

        user.setActive(true);


        // -----------------------------------------------------
        // INITIAL TOKEN VERSION
        // -----------------------------------------------------
        //
        // This makes the first JWT version:
        //
        // 0
        //
        // -----------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);
        }


        return userRepository.save(user);
    }


    // =========================================================
    // ADMIN REGISTER
    // =========================================================
    //
    // Public ADMIN registration is disabled.
    //
    // ADMIN accounts will be created by SUPER_ADMIN through
    // the admin user-management system.
    //
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
        // CHECK ACCOUNT STATUS
        // -----------------------------------------------------
        //
        // BLOCKED USER CANNOT LOGIN.
        //
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
        // ENSURE TOKEN VERSION EXISTS
        // -----------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);

            userRepository.save(user);
        }


        // -----------------------------------------------------
        // GENERATE JWT
        // -----------------------------------------------------
        //
        // IMPORTANT:
        //
        // Use the User entity here.
        //
        // This puts the CURRENT tokenVersion into the JWT.
        //
        // JWT contains:
        //
        // email
        // role
        // tokenVersion
        // issuedAt
        // expiration
        //
        // -----------------------------------------------------

        String token =
                jwtService.generateToken(user);


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