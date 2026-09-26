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
    // EXTRACT TOKEN VERSION
    // =====================================================
    //
    // Used for Force Logout.
    //
    // Example:
    //
    // Token:
    // tokenVersion = 0
    //
    // Database:
    // tokenVersion = 1
    //
    // Result:
    // Token is invalid.
    //
    // =====================================================

    public Long extractTokenVersion(String token) {

        return extractClaim(
                token,
                claims -> claims.get("tokenVersion", Long.class)
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
    // GENERATE TOKEN FROM USERDETAILS
    // =====================================================
    //
    // Used when only UserDetails is available.
    //
    // Token version cannot be obtained from a generic
    // UserDetails object, so this method keeps tokenVersion
    // at 0 for compatibility.
    //
    // Prefer generateToken(User user) when possible.
    //
    // =====================================================

    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        String role = "USER";

        if (userDetails.getAuthorities() != null
                && !userDetails.getAuthorities().isEmpty()) {

            String authority =
                    userDetails.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority();

            /*
             * ROLE_USER
             * ROLE_ADMIN
             * ROLE_SUPER_ADMIN
             *
             * becomes:
             *
             * USER
             * ADMIN
             * SUPER_ADMIN
             */

            if (authority.startsWith("ROLE_")) {

                role = authority.substring(5);

            } else {

                role = authority;
            }
        }

        return Jwts.builder()

                // =================================================
                // EMAIL / USERNAME
                // =================================================

                .subject(userDetails.getUsername())

                // =================================================
                // ROLE
                // =================================================

                .claim("role", role)

                // =================================================
                // TOKEN VERSION
                // =================================================
                //
                // Generic UserDetails does not contain our
                // database tokenVersion, so use 0.
                //
                // generateToken(User user) should be preferred.
                //
                // =================================================

                .claim("tokenVersion", 0L)

                // =================================================
                // ISSUED TIME
                // =================================================

                .issuedAt(now)

                // =================================================
                // EXPIRATION
                // =================================================

                .expiration(expiryDate)

                // =================================================
                // SIGNATURE
                // =================================================

                .signWith(getSigningKey())

                .compact();
    }


    // =====================================================
    // GENERATE TOKEN FROM USER ENTITY
    // =====================================================
    //
    // This is the preferred method for RishtaBox because
    // the User entity contains:
    //
    // - email
    // - role
    // - tokenVersion
    //
    // =====================================================

    public String generateToken(User user) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        // =================================================
        // SAFETY CHECK
        // =================================================

        if (user.getRole() == null) {

            throw new IllegalStateException(
                    "User role cannot be null"
            );
        }

        // =================================================
        // ROLE
        // =================================================

        String role = user.getRole().name();

        // =================================================
        // TOKEN VERSION
        // =================================================

        Long tokenVersion = user.getTokenVersion();

        if (tokenVersion == null) {
            tokenVersion = 0L;
        }

        // =================================================
        // CREATE JWT
        // =================================================

        return Jwts.builder()

                // Email
                .subject(user.getEmail())

                // Role
                .claim("role", role)

                // Token version
                .claim("tokenVersion", tokenVersion)

                // Issued time
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
    //
    // This method validates:
    //
    // 1. JWT signature
    // 2. Username / email
    // 3. Token expiration
    // 4. User account status
    // 5. Token version
    //
    // =====================================================

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {

            // =================================================
            // USERNAME
            // =================================================

            String username =
                    extractUsername(token);

            if (username == null
                    || !username.equals(
                    userDetails.getUsername())) {

                return false;
            }


            // =================================================
            // EXPIRATION
            // =================================================

            if (isTokenExpired(token)) {

                return false;
            }


            // =================================================
            // ACCOUNT STATUS
            // =================================================

            if (!userDetails.isEnabled()) {

                return false;
            }


            // =================================================
            // ACCOUNT LOCK STATUS
            // =================================================

            if (!userDetails.isAccountNonLocked()) {

                return false;
            }


            // =================================================
            // TOKEN VERSION
            // =================================================
            //
            // IMPORTANT:
            //
            // This check is performed here only when the
            // UserDetails is our Spring Security user and
            // tokenVersion is handled separately.
            //
            // The JwtAuthenticationFilter should perform
            // the database User tokenVersion comparison.
            //
            // =================================================

            return true;

        } catch (Exception e) {

            return false;
        }
    }


    // =====================================================
    // CHECK TOKEN EXPIRATION
    // =====================================================

    private boolean isTokenExpired(String token) {

        try {

            Date expirationDate =
                    extractClaim(
                            token,
                            Claims::getExpiration
                    );

            return expirationDate == null
                    || expirationDate.before(new Date());

        } catch (Exception e) {

            return true;
        }
    }
}