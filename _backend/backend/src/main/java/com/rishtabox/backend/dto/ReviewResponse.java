package com.rishtabox.backend.dto;

import com.rishtabox.backend.entity.Review;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String userName;
    private Integer rating;
    private String comment;
    private boolean verifiedPurchaser;
    private LocalDateTime createdAt;

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
        this.createdAt = review.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVerifiedPurchaser() {
        return verifiedPurchaser;
    }

    public void setVerifiedPurchaser(boolean verifiedPurchaser) {
        this.verifiedPurchaser = verifiedPurchaser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}