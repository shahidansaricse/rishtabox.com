
package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.AuthResponse;
import com.rishtabox.backend.dto.LoginRequest;
import com.rishtabox.backend.dto.RegisterRequest;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;
import com.rishtabox.backend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // ================= REGISTER =================

    public User register(RegisterRequest request) {

        if (request == null) {
            throw new RuntimeException("Invalid registration request");
        }

        if (request.getName() == null ||
                request.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (request.getPhone() == null ||
                request.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase();
        String phone = request.getPhone().trim();
        String password = request.getPassword();

        // Check duplicate email
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Check duplicate phone
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone already registered");
        }

        // Encrypt password before saving
        String encodedPassword =
                passwordEncoder.encode(password);

        // Create user
        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(encodedPassword);

        // SAVE TO MYSQL
        User savedUser = userRepository.save(user);

        System.out.println("=================================");
        System.out.println("USER REGISTERED SUCCESSFULLY");
        System.out.println("ID    : " + savedUser.getId());
        System.out.println("NAME  : " + savedUser.getName());
        System.out.println("EMAIL : " + savedUser.getEmail());
        System.out.println("PHONE : " + savedUser.getPhone());
        System.out.println("=================================");

        return savedUser;
    }
// ================= LOGIN =================

    public AuthResponse login(LoginRequest request) {

        if (request == null) {
            throw new RuntimeException("Invalid login request");
        }

        if (request.getEmail() == null ||
                request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email or mobile number is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        String loginValue =
                request.getEmail().trim();

        User user;

        // =========================
        // LOGIN USING EMAIL
        // =========================

        if (loginValue.contains("@")) {

            String email =
                    loginValue.toLowerCase();

            user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No account found. Please create an account first."
                            ));
        }

        // =========================
        // LOGIN USING MOBILE
        // =========================

        else {

            user = userRepository
                    .findByPhone(loginValue)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No account found. Please create an account first."
                            ));
        }

        // =========================
        // CHECK PASSWORD
        // =========================

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email/mobile or password"
            );
        }

        // =========================
        // GENERATE JWT
        // =========================

        // ================= GENERATE JWT =================

        String userEmail = user.getEmail()
                .trim()
                .toLowerCase();

        String token = jwtService.generateToken(userEmail);

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("JWT token generation failed");
        }

        System.out.println("=================================");
        System.out.println("LOGIN SUCCESSFUL");
        System.out.println("USER ID    : " + user.getId());
        System.out.println("USER EMAIL : " + userEmail);
        System.out.println("TOKEN EXISTS : " + !token.isBlank());
        System.out.println("TOKEN LENGTH : " + token.length());
        System.out.println("TOKEN PARTS  : " + token.split("\\.", -1).length);
        System.out.println("=================================");

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                userEmail
        );
    }
}

