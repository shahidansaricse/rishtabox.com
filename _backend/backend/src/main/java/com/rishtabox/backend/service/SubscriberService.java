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

                        + "✨ Welcome to RishtaBox!\n\n"

                        + "Thank you for becoming a part of the RishtaBox family. "
                        + "We're delighted to have you with us.\n\n"

                        + "🎁 From thoughtful surprises to beautifully curated gifts, "
                        + "we'll keep you updated with our latest collections, "
                        + "exclusive offers, and special selections for every occasion.\n\n"

                        + "💝 Whether you're celebrating a birthday, anniversary, festival, "
                        + "or simply expressing your love and appreciation, "
                        + "we're here to help you make every moment memorable.\n\n"

                        + "Discover something special:\n"
                        + "https://rishtabox.com\n\n"

                        + "Thank you for choosing RishtaBox.\n\n"

                        + "Warm Regards,\n"
                        + "RishtaBox Team\n"
                        + "Thoughtful Gifts. Meaningful Moments. ✨"
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