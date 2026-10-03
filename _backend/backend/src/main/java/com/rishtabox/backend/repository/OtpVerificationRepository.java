package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByIdDesc(
            String email,
            String purpose
    );

    void deleteByEmailAndPurpose(
            String email,
            String purpose
    );
}