package com.rishtabox.backend.security;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // =====================================================
    // LOAD USER
    // =====================================================

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        // =================================================
        // ROLE
        // =================================================

        String role = user.getRole().name();

        /*
         * Database role:
         *
         * USER
         * ADMIN
         * SUPER_ADMIN
         *
         * Spring Security authority:
         *
         * ROLE_USER
         * ROLE_ADMIN
         * ROLE_SUPER_ADMIN
         */

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(
                        "ROLE_" + role
                );

        // =================================================
        // SPRING SECURITY USER
        // =================================================

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}