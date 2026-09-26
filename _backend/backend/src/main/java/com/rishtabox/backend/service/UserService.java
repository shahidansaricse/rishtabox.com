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


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // =====================================================
    // REGISTER USER
    // =====================================================
    //
    // Normal signup can ONLY create USER.
    //
    // ADMIN / SUPER_ADMIN cannot be created through
    // normal public signup.
    //
    // =====================================================

    public User registerUser(User user) {

        if (user == null) {

            throw new RuntimeException(
                    "User data is required"
            );
        }


        // -------------------------------------------------
        // Validate name
        // -------------------------------------------------

        if (user.getName() == null ||
                user.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Name is required"
            );
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

        String phone =
                user.getPhone().trim();

        user.setPhone(phone);


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

        if (userRepository.existsByPhone(phone)) {

            throw new RuntimeException(
                    "Phone already registered"
            );
        }


        // -------------------------------------------------
        // SECURITY
        // Normal signup is ALWAYS USER
        // -------------------------------------------------

        user.setRole(User.Role.USER);


        // -------------------------------------------------
        // SECURITY
        // New account is active
        // -------------------------------------------------

        user.setActive(true);


        // -------------------------------------------------
        // INITIAL TOKEN VERSION
        // -------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);
        }


        // -------------------------------------------------
        // SECURITY
        // Never store plain-text password
        // -------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );


        // -------------------------------------------------
        // SAVE
        // -------------------------------------------------

        return userRepository.save(user);
    }


    // =====================================================
    // LOGIN USER
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


        User user =
                userRepository
                        .findByEmail(
                                email.trim().toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );


        // -------------------------------------------------
        // BLOCKED USER CANNOT LOGIN
        // -------------------------------------------------

        if (!user.isActive()) {

            throw new RuntimeException(
                    "Your account has been blocked"
            );
        }


        // -------------------------------------------------
        // VERIFY PASSWORD
        // -------------------------------------------------

        if (!passwordEncoder.matches(
                password,
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // -------------------------------------------------
        // ENSURE TOKEN VERSION
        // -------------------------------------------------

        if (user.getTokenVersion() == null) {

            user.setTokenVersion(0L);

            userRepository.save(user);
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
                                "User not found with ID: "
                                        + id
                        )
                );
    }


    // =====================================================
    // ADMIN - UPDATE USER
    // =====================================================
    //
    // Updates:
    //
    // - Name
    // - Email
    // - Phone
    //
    // Password is NOT changed here.
    //
    // =====================================================

    public User updateUser(
            Long userId,
            String name,
            String email,
            String phone) {

        User user =
                getUserById(userId);


        boolean emailChanged = false;


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (name != null &&
                !name.trim().isEmpty()) {

            user.setName(
                    name.trim()
            );
        }


        // -------------------------------------------------
        // EMAIL
        // -------------------------------------------------

        if (email != null &&
                !email.trim().isEmpty()) {

            String normalizedEmail =
                    email.trim()
                            .toLowerCase();


            // Only check if email changed
            if (!normalizedEmail.equals(
                    user.getEmail())) {


                if (userRepository.existsByEmail(
                        normalizedEmail)) {

                    throw new RuntimeException(
                            "Email already registered"
                    );
                }


                user.setEmail(
                        normalizedEmail
                );

                emailChanged = true;
            }
        }


        // -------------------------------------------------
        // PHONE
        // -------------------------------------------------

        if (phone != null &&
                !phone.trim().isEmpty()) {

            String normalizedPhone =
                    phone.trim();


            // Only check if phone changed
            if (!normalizedPhone.equals(
                    user.getPhone())) {


                if (userRepository.existsByPhone(
                        normalizedPhone)) {

                    throw new RuntimeException(
                            "Phone already registered"
                    );
                }


                user.setPhone(
                        normalizedPhone
                );
            }
        }


        // -------------------------------------------------
        // EMAIL CHANGE
        // -------------------------------------------------
        //
        // Existing JWT contains the old email.
        //
        // Invalidate all existing sessions.
        //
        // -------------------------------------------------

        if (emailChanged) {

            incrementTokenVersion(user);
        }


        return userRepository.save(user);
    }


    // =====================================================
    // ADMIN - CHANGE USER ROLE
    // =====================================================
    //
    // USER
    // ADMIN
    // SUPER_ADMIN
    //
    // Controller/security layer decides who can execute
    // this operation.
    //
    // Existing sessions are invalidated because the user's
    // authorization has changed.
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


        User user =
                getUserById(userId);


        // -------------------------------------------------
        // NO CHANGE
        // -------------------------------------------------

        if (user.getRole() == newRole) {

            return user;
        }


        // -------------------------------------------------
        // CHANGE ROLE
        // -------------------------------------------------

        user.setRole(newRole);


        // -------------------------------------------------
        // INVALIDATE EXISTING JWTs
        // -------------------------------------------------

        incrementTokenVersion(user);


        return userRepository.save(user);
    }


    // =====================================================
    // ADMIN - BLOCK USER
    // =====================================================
    //
    // Block:
    //
    // 1. Prevents future login
    // 2. Invalidates existing JWTs
    //
    // =====================================================

    public User blockUser(Long userId) {

        User user =
                getUserById(userId);


        if (user.isActive()) {

            user.setActive(false);


            // -------------------------------------------------
            // FORCE INVALIDATE EXISTING SESSIONS
            // -------------------------------------------------

            incrementTokenVersion(user);
        }


        return userRepository.save(user);
    }


    // =====================================================
    // ADMIN - UNBLOCK USER
    // =====================================================

    public User unblockUser(Long userId) {

        User user =
                getUserById(userId);


        user.setActive(true);


        return userRepository.save(user);
    }


    // =====================================================
    // ADMIN - CHANGE ACCOUNT STATUS
    // =====================================================

    public User setUserStatus(
            Long userId,
            boolean active) {

        User user =
                getUserById(userId);


        // -------------------------------------------------
        // STATUS CHANGED
        // -------------------------------------------------

        if (user.isActive() != active) {

            user.setActive(active);


            // -------------------------------------------------
            // If account is being blocked, invalidate
            // existing sessions.
            // -------------------------------------------------

            if (!active) {

                incrementTokenVersion(user);
            }
        }


        return userRepository.save(user);
    }


    // =====================================================
    // FORCE LOGOUT
    // =====================================================
    //
    // Ends all currently issued JWT sessions for this user.
    //
    // It does NOT:
    //
    // - delete the user
    // - block the user
    // - change the role
    //
    // It only increments tokenVersion.
    //
    // =====================================================

    public User forceLogout(Long userId) {

        User user =
                getUserById(userId);


        // -------------------------------------------------
        // INVALIDATE ALL EXISTING JWTs
        // -------------------------------------------------

        incrementTokenVersion(user);


        return userRepository.save(user);
    }


    // =====================================================
    // TOKEN VERSION INCREMENT
    // =====================================================

    private void incrementTokenVersion(
            User user) {

        Long currentVersion =
                user.getTokenVersion();


        if (currentVersion == null) {

            currentVersion = 0L;
        }


        user.setTokenVersion(
                currentVersion + 1
        );
    }


    // =====================================================
    // DELETE USER
    // =====================================================
    //
    // Authorization must be handled by the controller /
    // Spring Security.
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
                    "User not found with ID: "
                            + userId
            );
        }


        userRepository.deleteById(userId);
    }
}