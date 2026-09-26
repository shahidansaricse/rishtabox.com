package com.rishtabox.backend.service;

import com.rishtabox.backend.dto.ReviewRequest;
import com.rishtabox.backend.dto.ReviewResponse;
import com.rishtabox.backend.entity.Order;
import com.rishtabox.backend.entity.OrderItem;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.Review;
import com.rishtabox.backend.entity.User;
import com.rishtabox.backend.repository.OrderRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.ReviewRepository;
import com.rishtabox.backend.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    @Transactional
    public ReviewResponse createReview(
            ReviewRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            throw new RuntimeException(
                    "Please login to submit a review"
            );
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );

        if (request == null || request.getProductId() == null) {

            throw new RuntimeException(
                    "Product ID is required"
            );
        }

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        )
                );

        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        if (request.getComment() == null
                || request.getComment().trim().isEmpty()) {

            throw new RuntimeException(
                    "Comment cannot be empty"
            );
        }

        String comment = request.getComment().trim();

        if (comment.length() < 3) {

            throw new RuntimeException(
                    "Comment must contain at least 3 characters"
            );
        }

        if (comment.length() > 1000) {

            throw new RuntimeException(
                    "Comment cannot exceed 1000 characters"
            );
        }

        boolean alreadyReviewed =
                reviewRepository
                        .findByUserIdAndProductId(
                                user.getId(),
                                product.getId()
                        )
                        .isPresent();

        if (alreadyReviewed) {

            throw new RuntimeException(
                    "You have already reviewed this product"
            );
        }

        boolean purchased = hasEligiblePurchase(
                user.getId(),
                product.getId()
        );

        if (!purchased) {

            throw new RuntimeException(
                    "You can review this product only after purchasing it."
            );
        }

        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(comment);
        review.setVerifiedPurchaser(true);

        // New reviews remain pending
        review.setApproved(false);

        review.setCreatedAt(LocalDateTime.now());

        Review savedReview =
                reviewRepository.save(review);

        return new ReviewResponse(savedReview);
    }

    // =========================================================
    // CHECK ELIGIBLE PURCHASE
    // =========================================================

    private boolean hasEligiblePurchase(
            Long userId,
            Long productId) {

        List<Order> orders =
                orderRepository.findByUserId(userId);

        if (orders == null || orders.isEmpty()) {
            return false;
        }

        for (Order order : orders) {

            if (order == null) {
                continue;
            }

            String orderStatus = order.getOrderStatus();
            String paymentStatus = order.getPaymentStatus();

            if ("CANCELLED".equalsIgnoreCase(orderStatus)) {
                continue;
            }

            if (!"PLACED".equalsIgnoreCase(orderStatus)) {
                continue;
            }

            if (!"PAID".equalsIgnoreCase(paymentStatus)) {
                continue;
            }

            if (order.getItems() == null
                    || order.getItems().isEmpty()) {

                continue;
            }

            for (OrderItem orderItem : order.getItems()) {

                if (orderItem == null
                        || orderItem.getProduct() == null) {

                    continue;
                }

                Long orderedProductId =
                        orderItem.getProduct().getId();

                if (productId.equals(orderedProductId)) {
                    return true;
                }
            }
        }

        return false;
    }

    // =========================================================
    // GET REVIEWS BY PRODUCT
    // =========================================================

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProduct(
            Long productId) {

        return reviewRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {

        return reviewRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ReviewResponse::new)
                .toList();
    }

    // =========================================================
    // ADMIN - APPROVE / REJECT REVIEW
    // =========================================================

    @Transactional
    public ReviewResponse updateReviewApproval(
            Long reviewId,
            boolean approved) {

        System.out.println(
                "Updating review ID: " + reviewId
        );

        System.out.println(
                "New approved status: " + approved
        );

        if (reviewId == null) {

            throw new IllegalArgumentException(
                    "Review ID is required"
            );
        }

        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Review not found with ID: " + reviewId
                        )
                );

        review.setApproved(approved);

        Review updatedReview =
                reviewRepository.saveAndFlush(review);

        System.out.println(
                "Review updated successfully. ID: "
                        + updatedReview.getId()
        );

        System.out.println(
                "Saved approved status: "
                        + updatedReview.isApproved()
        );

        return new ReviewResponse(updatedReview);
    }

    // =========================================================
    // ADMIN - DELETE REVIEW
    // =========================================================

    @Transactional
    public void deleteReview(Long reviewId) {

        if (reviewId == null) {

            throw new IllegalArgumentException(
                    "Review ID is required"
            );
        }

        if (!reviewRepository.existsById(reviewId)) {

            throw new RuntimeException(
                    "Review not found with ID: " + reviewId
            );
        }

        reviewRepository.deleteById(reviewId);
    }
}