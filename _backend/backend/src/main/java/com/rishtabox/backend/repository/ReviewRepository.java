package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByProductIdOrderByCreatedAtDesc(
            Long productId
    );

    List<Review> findAllByOrderByCreatedAtDesc();

    Optional<Review> findByUserIdAndProductId(
            Long userId,
            Long productId
    );
}