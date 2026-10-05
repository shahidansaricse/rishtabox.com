package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Subscriber;
import com.rishtabox.backend.repository.SubscriberRepository;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final JavaMailSender mailSender;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            JavaMailSender mailSender) {

        this.subscriberRepository = subscriberRepository;
        this.mailSender = mailSender;
    }


    // =========================================================
    // CUSTOMER - SUBSCRIBE
    // =========================================================

    @Transactional
    public String subscribe(String email) {

        String cleanEmail =
                email == null
                        ? ""
                        : email.trim().toLowerCase();


        // =====================================================
        // VALIDATE EMAIL
        // =====================================================

        if (cleanEmail.isEmpty()) {

            throw new IllegalArgumentException(
                    "Email is required."
            );
        }


        if (!cleanEmail.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Please enter a valid email address."
            );
        }


        // =====================================================
        // CHECK DUPLICATE
        // =====================================================

        if (subscriberRepository
                .existsByEmailIgnoreCase(cleanEmail)) {

            return "You are already subscribed!";
        }


        // =====================================================
        // SAVE SUBSCRIBER
        // =====================================================

        Subscriber subscriber =
                new Subscriber(cleanEmail);

        subscriberRepository.save(subscriber);


        // =====================================================
        // SEND THANK-YOU EMAIL
        // =====================================================

        try {

            sendThankYouEmail(cleanEmail);

        } catch (Exception error) {

            /*
             * The subscriber has already been saved.
             *
             * If email sending fails, we don't want
             * to lose the subscriber.
             */

            error.printStackTrace();

            return "Thank you for subscribing to RishtaBox! "
                    + "Your subscription has been saved.";
        }


        return "Thank you for subscribing to RishtaBox!";
    }


    // =========================================================
    // SEND THANK-YOU EMAIL
    // =========================================================

    private void sendThankYouEmail(String email) {

        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setTo(email);


        message.setSubject(
                "Thanks for subscribing to RishtaBox!"
        );


        message.setText(
                "Hello,\n\n"

                        + "Thank you for subscribing to RishtaBox!\n\n"

                        + "We are happy to have you with us.\n\n"

                        + "You will receive updates about our "
                        + "latest gifts, collections, offers, "
                        + "and special occasions.\n\n"

                        + "Visit RishtaBox:\n"
                        + "https://rishtabox.com\n\n"

                        + "Regards,\n"
                        + "RishtaBox Team"
        );


        mailSender.send(message);
    }


    // =========================================================
    // ADMIN - GET ALL SUBSCRIBERS
    // =========================================================

    public List<Subscriber> getAllSubscribers() {

        return subscriberRepository.findAll();
    }


    // =========================================================
    // ADMIN - GET SUBSCRIBER COUNT
    // =========================================================

    public long getSubscriberCount() {

        return subscriberRepository.count();
    }


    // =========================================================
    // ADMIN - DELETE SUBSCRIBER
    // =========================================================

    @Transactional
    public void deleteSubscriber(Long id) {

        if (!subscriberRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Subscriber not found."
            );
        }


        subscriberRepository.deleteById(id);
    }
}