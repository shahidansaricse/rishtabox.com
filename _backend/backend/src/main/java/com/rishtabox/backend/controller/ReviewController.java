package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.ReviewRequest;
import com.rishtabox.backend.dto.ReviewResponse;
import com.rishtabox.backend.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {

        List<ReviewResponse> reviews =
                reviewService.getAllReviews();

        return ResponseEntity.ok(reviews);
    }

    // =========================================================
    // GET REVIEWS BY PRODUCT ID
    // =========================================================

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId) {

        List<ReviewResponse> reviews =
                reviewService.getReviewsByProduct(productId);

        return ResponseEntity.ok(reviews);
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        try {

            // ==============================================
            // CHECK AUTHENTICATION
            // ==============================================

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "message",
                                        "Please login before submitting a review."
                                )
                        );
            }

            // ==============================================
            // DEBUG AUTHENTICATED USER
            // ==============================================

            System.out.println(
                    "Review authenticated user: "
                            + authentication.getName()
            );

            System.out.println(
                    "Review authentication type: "
                            + authentication.getClass()
                            .getSimpleName()
            );

            // ==============================================
            // CREATE REVIEW
            // ==============================================

            ReviewResponse response =
                    reviewService.createReview(
                            request,
                            authentication
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException exception) {

            System.out.println(
                    "Review creation error: "
                            + exception.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage() != null
                                            ? exception.getMessage()
                                            : "Unable to create review."
                            )
                    );
        }
    }
}