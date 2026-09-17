package com.rishtabox.backend.dto;

public class ProductRequest {

    private String name;
    private String description;

    private Double price;
    private Double originalPrice;

    private String image;
    private Integer stock;

    private String categoryId;
    private String festivalId;
    private String relationshipId;


    // =========================
    // Name
    // =========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // =========================
    // Description
    // =========================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // =========================
    // Price
    // =========================

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    // =========================
    // Original Price
    // =========================

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }


    // =========================
    // Image
    // =========================

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    // =========================
    // Stock
    // =========================

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }


    // =========================
    // Category ID
    // =========================

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }


    // =========================
    // Festival ID
    // =========================

    public String getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(String festivalId) {
        this.festivalId = festivalId;
    }


    // =========================
    // Relationship ID
    // =========================

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }
}