package com.rishtabox.backend.security;

import com.rishtabox.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // =====================================================
    // CONFIGURATION
    // =====================================================

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // =====================================================
    // SIGNING KEY
    // =====================================================

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // =====================================================
    // EXTRACT USERNAME / EMAIL
    // =====================================================

    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    // =====================================================
    // EXTRACT ROLE
    // =====================================================

    public String extractRole(String token) {

        return extractClaim(
                token,
                claims -> claims.get("role", String.class)
        );
    }

    // =====================================================
    // GENERIC CLAIM EXTRACTOR
    // =====================================================

    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    // =====================================================
    // EXTRACT ALL CLAIMS
    // =====================================================

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =====================================================
    // GENERATE TOKEN
    // =====================================================

    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        /*
         * Spring Security authority:
         *
         * ROLE_USER
         * ROLE_ADMIN
         * ROLE_SUPER_ADMIN
         *
         * We store the clean role in JWT:
         *
         * USER
         * ADMIN
         * SUPER_ADMIN
         */

        String role = "USER";

        if (userDetails.getAuthorities() != null
                && !userDetails.getAuthorities().isEmpty()) {

            String authority =
                    userDetails.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

            if (authority.startsWith("ROLE_")) {

                role = authority.substring(5);

            } else {

                role = authority;
            }
        }

        // =================================================
        // BUILD JWT
        // =================================================

        return Jwts.builder()

                // User email
                .subject(userDetails.getUsername())

                // User role
                .claim("role", role)

                // Created time
                .issuedAt(now)

                // Expiration
                .expiration(expiryDate)

                // Sign token
                .signWith(getSigningKey())

                .compact();
    }

    // =====================================================
    // GENERATE TOKEN FROM USER ENTITY
    // =====================================================

    public String generateToken(User user) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        String role = user.getRole().name();

        return Jwts.builder()

                // Email
                .subject(user.getEmail())

                // Role
                .claim("role", role)

                // Created
                .issuedAt(now)

                // Expiration
                .expiration(expiryDate)

                // Signature
                .signWith(getSigningKey())

                .compact();
    }

    // =====================================================
    // VALIDATE TOKEN
    // =====================================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {

            String username =
                    extractUsername(token);

            return username != null
                    && username.equals(
                    userDetails.getUsername()
            )
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }

    // =====================================================
    // CHECK TOKEN EXPIRATION
    // =====================================================

    private boolean isTokenExpired(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }
}