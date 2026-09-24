package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    boolean existsByEmailIgnoreCase(String email);

}