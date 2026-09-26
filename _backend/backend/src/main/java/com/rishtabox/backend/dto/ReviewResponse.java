package com.rishtabox.backend.dto;

import com.rishtabox.backend.entity.Review;

import java.time.LocalDateTime;

public class ReviewResponse {

    // =========================================================
    // REVIEW DETAILS
    // =========================================================

    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private Integer rating;
    private String comment;
    private boolean verifiedPurchaser;

    // =========================================================
    // APPROVAL STATUS
    // =========================================================

    private boolean approved;

    // =========================================================
    // CREATED DATE
    // =========================================================

    private LocalDateTime createdAt;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ReviewResponse() {
    }

    public ReviewResponse(Review review) {

        this.id = review.getId();

        if (review.getProduct() != null) {
            this.productId = review.getProduct().getId();
            this.productName = review.getProduct().getName();
        }

        if (review.getUser() != null) {
            this.userName = review.getUser().getName();
        }

        this.rating = review.getRating();
        this.comment = review.getComment();
        this.verifiedPurchaser = review.isVerifiedPurchaser();

        // IMPORTANT
        this.approved = review.isApproved();

        this.createdAt = review.getCreatedAt();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public boolean isVerifiedPurchaser() {
        return verifiedPurchaser;
    }

    public boolean isApproved() {
        return approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =========================================================
    // SETTERS
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVerifiedPurchaser(boolean verifiedPurchaser) {
        this.verifiedPurchaser = verifiedPurchaser;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}