package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // REGISTER USER
    // =====================================================
    // Normal signup can ONLY create USER.
    // ADMIN / SUPER_ADMIN must never come from signup request.
    // =====================================================

    public User registerUser(User user) {

        if (user == null) {
            throw new RuntimeException("User data is required");
        }

        // -------------------------------------------------
        // Validate email
        // -------------------------------------------------

        if (user.getEmail() == null ||
                user.getEmail().trim().isEmpty()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        // -------------------------------------------------
        // Validate phone
        // -------------------------------------------------

        if (user.getPhone() == null ||
                user.getPhone().trim().isEmpty()) {

            throw new RuntimeException(
                    "Phone is required"
            );
        }

        // -------------------------------------------------
        // Validate password
        // -------------------------------------------------

        if (user.getPassword() == null ||
                user.getPassword().trim().isEmpty()) {

            throw new RuntimeException(
                    "Password is required"
            );
        }

        // -------------------------------------------------
        // Normalize email
        // -------------------------------------------------

        String email =
                user.getEmail()
                        .trim()
                        .toLowerCase();

        user.setEmail(email);

        // -------------------------------------------------
        // Normalize phone
        // -------------------------------------------------

        user.setPhone(
                user.getPhone().trim()
        );

        // -------------------------------------------------
        // Check duplicate email
        // -------------------------------------------------

        if (userRepository.existsByEmail(email)) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        // -------------------------------------------------
        // Check duplicate phone
        // -------------------------------------------------

        if (userRepository.existsByPhone(user.getPhone())) {

            throw new RuntimeException(
                    "Phone already registered"
            );
        }

        // -------------------------------------------------
        // SECURITY:
        // Normal signup is ALWAYS USER
        // -------------------------------------------------

        user.setRole(User.Role.USER);

        // -------------------------------------------------
        // SECURITY:
        // Never store plain-text password
        // -------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        // -------------------------------------------------
        // Save user
        // -------------------------------------------------

        return userRepository.save(user);
    }

    // =====================================================
    // LOGIN USER
    // =====================================================
    //
    // NOTE:
    // Password verification should preferably be handled
    // by AuthService using PasswordEncoder.matches().
    //
    // This method is kept for compatibility with your
    // existing UserController.
    // =====================================================

    public User loginUser(
            String email,
            String password) {

        if (email == null ||
                password == null) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        User user = userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        // -------------------------------------------------
        // BCrypt password verification
        // -------------------------------------------------

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    // =====================================================
    // ADMIN - USER COUNT
    // =====================================================

    public long getUserCount() {

        return userRepository.count();
    }

    // =====================================================
    // ADMIN - GET ALL USERS
    // =====================================================

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // =====================================================
    // GET USER BY ID
    // =====================================================

    public User getUserById(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID cannot be null"
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + id
                        )
                );
    }

    // =====================================================
    // SUPER ADMIN - CHANGE USER ROLE
    // =====================================================
    //
    // Allowed roles:
    // USER
    // ADMIN
    // SUPER_ADMIN
    //
    // The controller must protect this endpoint with:
    //
    // @PreAuthorize("hasRole('SUPER_ADMIN')")
    //
    // =====================================================

    public User changeUserRole(
            Long userId,
            User.Role newRole) {

        if (userId == null) {

            throw new RuntimeException(
                    "User ID cannot be null"
            );
        }

        if (newRole == null) {

            throw new RuntimeException(
                    "Role is required"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with ID: " + userId
                        )
                );

        user.setRole(newRole);

        return userRepository.save(user);
    }

    // =====================================================
    // DELETE USER
    // =====================================================
    //
    // Protect this endpoint at controller level.
    //
    // Recommended:
    // SUPER_ADMIN only
    //
    // =====================================================

    public void deleteUser(Long userId) {

        if (userId == null) {

            throw new RuntimeException(
                    "User ID cannot be null"
            );
        }

        if (!userRepository.existsById(userId)) {

            throw new RuntimeException(
                    "User not found with ID: " + userId
            );
        }

        userRepository.deleteById(userId);
    }
}