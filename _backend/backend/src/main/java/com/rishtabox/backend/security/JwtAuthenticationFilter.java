package com.rishtabox.backend.security;

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

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "JWT REQUEST: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        try {

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                System.out.println(
                        "JWT: Authorization header missing"
                );

                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader
                    .substring(7)
                    .trim();

            if (token.isEmpty()) {

                System.out.println(
                        "JWT: Empty token"
                );

                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.extractEmail(token);

            System.out.println(
                    "JWT email: " + email
            );

            if (email == null || email.isBlank()) {

                System.out.println(
                        "JWT: Email is empty"
                );

                filterChain.doFilter(request, response);
                return;
            }

            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                System.out.println(
                        "JWT: Authentication already exists"
                );

                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            boolean validToken =
                    jwtService.isTokenValid(
                            token,
                            userDetails
                    );

            System.out.println(
                    "JWT database email: "
                            + userDetails.getUsername()
            );

            System.out.println(
                    "JWT valid: " + validToken
            );

            if (validToken) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "JWT AUTHENTICATED: "
                                + userDetails.getUsername()
                );

            } else {

                System.out.println(
                        "JWT: Token validation failed"
                );
            }

        } catch (Exception exception) {

            System.out.println(
                    "JWT ERROR: "
                            + exception.getClass().getSimpleName()
                            + " - "
                            + exception.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }

        System.out.println(
                "AUTHENTICATION PRESENT: "
                        + (
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() != null
                )
        );

        filterChain.doFilter(request, response);
    }
}