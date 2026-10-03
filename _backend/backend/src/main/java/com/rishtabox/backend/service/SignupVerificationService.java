package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.SignupVerification;
import com.rishtabox.backend.repository.SignupVerificationRepository;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class SignupVerificationService {

    private final SignupVerificationRepository repository;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public SignupVerificationService(
            SignupVerificationRepository repository) {

        this.repository = repository;
    }

    // =========================================================
    // CREATE SIGNUP VERIFICATION
    // =========================================================
    //
    // Mobile number is stored.
    // Mobile OTP is NOT required.
    //
    // Email verification is required.
    // =========================================================

    public SignupVerification createVerification(
            String phone,
            String email) {

        phone = normalizePhone(phone);
        email = normalizeEmail(email);

        repository.deleteByPhoneAndEmail(
                phone,
                email
        );

        SignupVerification verification =
                new SignupVerification();

        verification.setPhone(phone);
        verification.setEmail(email);

        // Mobile OTP has been completely removed.
        // Only email verification is required.
        verification.setEmailVerified(false);

        verification.setVerificationToken(
                generateToken()
        );

        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(10)
        );

        return repository.save(verification);
    }

    // =========================================================
    // FIND EXISTING VERIFICATION
    // =========================================================

    public SignupVerification getVerification(
            String phone,
            String email) {

        phone = normalizePhone(phone);
        email = normalizeEmail(email);

        return repository
                .findTopByPhoneAndEmailOrderByIdDesc(
                        phone,
                        email
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Signup verification not found"
                        )
                );
    }

    // =========================================================
    // MARK EMAIL AS VERIFIED
    // =========================================================

    public SignupVerification markEmailVerified(
            String phone,
            String email) {

        SignupVerification verification =
                getVerification(
                        phone,
                        email
                );

        checkExpiry(verification);

        verification.setEmailVerified(true);

        return repository.save(verification);
    }

    // =========================================================
    // CHECK FINAL VERIFICATION
    // =========================================================
    //
    // Requirements:
    //
    // 1. Token must be valid
    // 2. Token must not be expired
    // 3. Email must be verified
    //
    // Mobile OTP is NOT checked.
    // =========================================================

    public SignupVerification verifyForRegistration(
            String token) {

        if (token == null ||
                token.isBlank()) {

            throw new RuntimeException(
                    "Verification token is required"
            );
        }

        SignupVerification verification =
                repository
                        .findByVerificationToken(
                                token.trim()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid verification token"
                                )
                        );

        checkExpiry(verification);

        if (!verification.isEmailVerified()) {

            throw new RuntimeException(
                    "Email is not verified"
            );
        }

        return verification;
    }

    // =========================================================
    // DELETE VERIFICATION
    // =========================================================

    public void deleteVerification(
            SignupVerification verification) {

        repository.delete(verification);
    }

    // =========================================================
    // EXPIRY CHECK
    // =========================================================

    private void checkExpiry(
            SignupVerification verification) {

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            repository.delete(verification);

            throw new RuntimeException(
                    "Signup verification has expired"
            );
        }
    }

    // =========================================================
    // GENERATE SECURE RANDOM TOKEN
    // =========================================================

    private String generateToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        StringBuilder token =
                new StringBuilder();

        for (byte b : bytes) {

            token.append(
                    String.format(
                            "%02x",
                            b
                    )
            );
        }

        return token.toString();
    }

    // =========================================================
    // NORMALIZE PHONE
    // =========================================================
    //
    // Mobile number is still required and stored.
    // No OTP is sent or verified.
    // =========================================================

    private String normalizePhone(
            String phone) {

        if (phone == null ||
                phone.isBlank()) {

            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        phone =
                phone.trim()
                        .replaceAll(
                                "[\\s-]",
                                ""
                        );

        if (phone.startsWith("+91")) {

            phone = phone.substring(1);
        }

        if (phone.matches("\\d{10}")) {

            return "91" + phone;
        }

        if (phone.matches("91\\d{10}")) {

            return phone;
        }

        throw new RuntimeException(
                "Invalid mobile number"
        );
    }

    // =========================================================
    // NORMALIZE EMAIL
    // =========================================================

    private String normalizeEmail(
            String email) {

        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }

        return email
                .trim()
                .toLowerCase();
    }
}