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

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                encodedPassword
        );

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}