package com.rishtabox.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "product_id"}
                )
        }
)
public class Review {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // USER RELATION
    // =========================================================

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;


    // =========================================================
    // PRODUCT RELATION
    // =========================================================

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    // =========================================================
    // REVIEW DETAILS
    // =========================================================

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String comment;

    @Column(nullable = false)
    private boolean verifiedPurchaser;


    // =========================================================
    // APPROVAL STATUS
    // =========================================================

    @Column(nullable = false)
    private boolean approved = false;


    // =========================================================
    // CREATED DATE
    // =========================================================

    private LocalDateTime createdAt;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Review() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
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