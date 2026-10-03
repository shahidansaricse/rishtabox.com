package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.SignupVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignupVerificationRepository
        extends JpaRepository<SignupVerification, Long> {

    Optional<SignupVerification>
    findTopByPhoneAndEmailOrderByIdDesc(
            String phone,
            String email
    );

    Optional<SignupVerification>
    findByVerificationToken(
            String verificationToken
    );

    void deleteByPhoneAndEmail(
            String phone,
            String email
    );
}