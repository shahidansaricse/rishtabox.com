package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository
        extends JpaRepository<Subscriber, Long> {

    Optional<Subscriber> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}