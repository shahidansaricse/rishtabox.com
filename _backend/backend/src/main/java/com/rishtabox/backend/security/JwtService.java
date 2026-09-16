package com.rishtabox.backend.security;

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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    // Create signing key
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }


    // Generate JWT token
    public String generateToken(String email) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }


    // Extract email from token
    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }


    // Extract any claim
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    // Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Check token expiry
    private boolean isTokenExpired(String token) {

        Date expirationDate =
                extractClaim(
                        token,
                        Claims::getExpiration
                );

        return expirationDate.before(new Date());
    }


    // Validate token
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String email =
                extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}