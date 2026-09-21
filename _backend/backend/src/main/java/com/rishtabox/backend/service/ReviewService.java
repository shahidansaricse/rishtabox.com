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

        // 1. Check whether the user is logged in
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            throw new RuntimeException(
                    "Please login to submit a review"
            );
        }

        // 2. Find the logged-in user
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );

        // 3. Validate product ID
        if (request == null || request.getProductId() == null) {

            throw new RuntimeException(
                    "Product ID is required"
            );
        }

        // 4. Find product
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        )
                );

        // 5. Validate rating
        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }

        // 6. Validate comment
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

        // 7. Check whether the user has already reviewed this product
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

        // 8. Check whether the user purchased the product
        boolean purchased = hasEligiblePurchase(
                user.getId(),
                product.getId()
        );

        // 9. Only purchased users can submit reviews
        if (!purchased) {

            throw new RuntimeException(
                    "You can review this product only after purchasing it."
            );
        }

        // 10. Create Review entity
        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(comment);

        // Only eligible purchasers reach this point
        review.setVerifiedPurchaser(true);

        review.setCreatedAt(LocalDateTime.now());

        // 11. Save Review entity
        Review savedReview = reviewRepository.save(review);

        // 12. Convert entity into ReviewResponse
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

            // 1. Cancelled orders are not eligible
            if ("CANCELLED".equalsIgnoreCase(orderStatus)) {
                continue;
            }

            // 2. Only PLACED orders are eligible
            if (!"PLACED".equalsIgnoreCase(orderStatus)) {
                continue;
            }

            // 3. Only PAID orders are eligible
            if (!"PAID".equalsIgnoreCase(paymentStatus)) {
                continue;
            }

            // 4. Check order items
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
}