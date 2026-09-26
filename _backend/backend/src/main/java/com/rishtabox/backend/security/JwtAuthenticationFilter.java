package com.rishtabox.backend.security;

import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }


    // =====================================================
    // JWT FILTER
    // =====================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        // =====================================================
        // GET AUTHORIZATION HEADER
        // =====================================================

        String authHeader =
                request.getHeader("Authorization");


        /*
         * No JWT
         *
         * Example:
         *
         * GET /api/products
         *
         * If endpoint is public, continue normally.
         */

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        // =====================================================
        // EXTRACT JWT
        // =====================================================

        String jwt =
                authHeader.substring(7).trim();


        if (jwt.isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }


        try {

            // =================================================
            // EXTRACT EMAIL
            // =================================================

            String email =
                    jwtService.extractUsername(jwt);


            if (email == null || email.isBlank()) {

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // DON'T RE-AUTHENTICATE
            // =================================================

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // LOAD DATABASE USER
            // =================================================

            User databaseUser =
                    userRepository
                            .findByEmail(email)
                            .orElse(null);


            // =================================================
            // USER DOES NOT EXIST
            // =================================================

            if (databaseUser == null) {

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // CHECK ACCOUNT STATUS
            // =================================================
            //
            // active = false
            //
            // means account is blocked.
            //
            // A blocked user must not be able to continue
            // using an already-issued JWT.
            //
            // =================================================

            if (!databaseUser.isActive()) {

                SecurityContextHolder.clearContext();

                System.out.println(
                        "JWT rejected: account is blocked - "
                                + email
                );

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // LOAD USERDETAILS
            // =================================================
            //
            // Important:
            //
            // UserDetails is created from the CURRENT
            // database role.
            //
            // Therefore if:
            //
            // ADMIN -> USER
            //
            // or:
            //
            // USER -> ADMIN
            //
            // the current database role is used for the
            // authentication authorities.
            //
            // =================================================

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);


            // =================================================
            // VALIDATE JWT
            // =================================================

            if (!jwtService.isTokenValid(
                    jwt,
                    userDetails)) {

                SecurityContextHolder.clearContext();

                System.out.println(
                        "JWT rejected: invalid or expired token - "
                                + email
                );

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // TOKEN VERSION CHECK
            // =================================================
            //
            // This is what makes FORCE LOGOUT work.
            //
            // Example:
            //
            // Database:
            //
            // tokenVersion = 1
            //
            // JWT:
            //
            // tokenVersion = 0
            //
            // Result:
            //
            // JWT is rejected.
            //
            // =================================================

            Long jwtTokenVersion =
                    jwtService.extractTokenVersion(jwt);


            Long databaseTokenVersion =
                    databaseUser.getTokenVersion();


            // =================================================
            // SAFETY FOR OLD TOKENS
            // =================================================
            //
            // Tokens created before tokenVersion was added
            // may not contain this claim.
            //
            // We treat a missing claim as version 0.
            //
            // =================================================

            if (jwtTokenVersion == null) {

                jwtTokenVersion = 0L;
            }


            if (databaseTokenVersion == null) {

                databaseTokenVersion = 0L;
            }


            // =================================================
            // COMPARE TOKEN VERSION
            // =================================================

            if (!jwtTokenVersion.equals(
                    databaseTokenVersion)) {

                SecurityContextHolder.clearContext();

                System.out.println(
                        "JWT rejected: token version mismatch - "
                                + email
                );

                filterChain.doFilter(request, response);
                return;
            }


            // =================================================
            // CREATE AUTHENTICATION
            // =================================================

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );


            // =================================================
            // REQUEST DETAILS
            // =================================================

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );


            // =================================================
            // SET SECURITY CONTEXT
            // =================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);


        } catch (Exception exception) {

            // =================================================
            // JWT ERROR
            // =================================================

            SecurityContextHolder.clearContext();

            System.out.println(
                    "JWT Authentication failed: "
                            + exception.getMessage()
            );
        }


        // =====================================================
        // CONTINUE FILTER CHAIN
        // =====================================================

        filterChain.doFilter(request, response);
    }
}