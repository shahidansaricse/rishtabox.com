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


        // =====================================================
        // CHECK ROLE
        // =====================================================

        if (user.getRole() == null) {

            throw new UsernameNotFoundException(
                    "User role is not assigned for email: "
                            + email
            );
        }


        // =====================================================
        // DATABASE ROLE
        // =====================================================

        String role = user.getRole().name();


        /*
         * Database:
         *
         * USER
         * ADMIN
         * SUPER_ADMIN
         *
         * Spring Security:
         *
         * ROLE_USER
         * ROLE_ADMIN
         * ROLE_SUPER_ADMIN
         */


        // =====================================================
        // CREATE AUTHORITY
        // =====================================================

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(
                        "ROLE_" + role
                );


        // =====================================================
        // RETURN SPRING SECURITY USER
        // =====================================================
        //
        // Account status:
        //
        // user.isActive() = true
        //      -> account enabled
        //
        // user.isActive() = false
        //      -> account disabled
        //
        // =====================================================

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                true,                  // enabled
                true,                  // accountNonExpired
                true,                  // credentialsNonExpired
                user.isActive(),       // accountNonLocked
                Collections.singletonList(authority)
        );
    }
}