package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.OtpVerification;
import com.rishtabox.backend.repository.OtpVerificationRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private final OtpVerificationRepository otpRepository;
    private final JavaMailSender mailSender;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
            OtpVerificationRepository otpRepository,
            JavaMailSender mailSender) {

        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendOtp(
            String email,
            String purpose) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        email = email.trim().toLowerCase();

        String otp = generateOtp();

        // Delete previous OTP
        otpRepository.deleteByEmailAndPurpose(
                email,
                purpose
        );

        // Create new OTP
        OtpVerification verification =
                new OtpVerification();

        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setPurpose(purpose);

        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        verification.setAttempts(0);

        otpRepository.save(verification);

        // Send email
        sendEmail(
                email,
                otp,
                purpose
        );
    }

    @Transactional
    public boolean verifyOtp(
            String email,
            String otp,
            String purpose) {

        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (otp == null || otp.isBlank()) {
            throw new RuntimeException("OTP is required");
        }

        email = email.trim().toLowerCase();
        otp = otp.trim();

        OtpVerification verification =
                otpRepository
                        .findTopByEmailAndPurposeOrderByIdDesc(
                                email,
                                purpose
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "OTP not found or expired"
                                )
                        );

        // Check expiry
        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            otpRepository.delete(verification);

            throw new RuntimeException(
                    "OTP has expired"
            );
        }

        // Check attempts
        if (verification.getAttempts() >= 5) {

            otpRepository.delete(verification);

            throw new RuntimeException(
                    "Too many incorrect OTP attempts"
            );
        }

        // Check OTP
        if (!verification.getOtp().equals(otp)) {

            verification.setAttempts(
                    verification.getAttempts() + 1
            );

            otpRepository.save(verification);

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        // OTP correct → delete it
        otpRepository.delete(verification);

        return true;
    }

    private String generateOtp() {

        int number =
                100000 + secureRandom.nextInt(900000);

        return String.valueOf(number);
    }

    private void sendEmail(
            String email,
            String otp,
            String purpose) {

        String subject;

        if ("REGISTER".equalsIgnoreCase(purpose)) {
            subject = "RishtaBox - Signup OTP";
        } else {
            subject = "RishtaBox - Login OTP";
        }

        String messageText =
                "Your RishtaBox OTP is: "
                        + otp
                        + "\n\n"
                        + "This OTP is valid for 5 minutes."
                        + "\n"
                        + "Do not share this OTP with anyone."
                        + "\n\n"
                        + "Regards,\n"
                        + "RishtaBox";

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);
        message.setSubject(subject);
        message.setText(messageText);

        try {
            mailSender.send(message);
        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Email sending failed: "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }
}