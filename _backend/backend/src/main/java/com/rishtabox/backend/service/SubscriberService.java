package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Subscriber;
import com.rishtabox.backend.repository.SubscriberRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberService(
            SubscriberRepository subscriberRepository) {

        this.subscriberRepository = subscriberRepository;
    }

    @Transactional
    public String subscribe(String email) {

        String cleanEmail =
                email == null
                        ? ""
                        : email.trim().toLowerCase();

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

        if (subscriberRepository
                .existsByEmailIgnoreCase(cleanEmail)) {

            return "You are already subscribed!";
        }

        Subscriber subscriber =
                new Subscriber(cleanEmail);

        subscriberRepository.save(subscriber);

        return "Thank you for subscribing to RishtaBox!";
    }
}