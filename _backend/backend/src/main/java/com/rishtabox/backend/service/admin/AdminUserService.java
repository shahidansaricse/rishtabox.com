package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + id
                        )
                );
    }

    @Transactional
    public User updateRole(
            Long id,
            String role) {

        User user = getUser(id);

        if (role == null || role.isBlank()) {
            throw new RuntimeException(
                    "Role is required"
            );
        }

        String newRole =
                role.trim().toUpperCase();

        if (!newRole.equals("USER")
                && !newRole.equals("ADMIN")) {

            throw new RuntimeException(
                    "Invalid role: " + newRole
            );
        }

        user.setRole(
                User.Role.valueOf(newRole)
        );

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "User ID is required"
            );
        }

        if (!userRepository.existsById(id)) {
            throw new RuntimeException(
                    "User not found with id: " + id
            );
        }

        userRepository.deleteById(id);
    }

    public long getTotalUsers() {
        return userRepository.count();
    }
}