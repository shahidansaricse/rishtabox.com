package com.rishtabox.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double price;

    private Double originalPrice;

    private String image;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {
    }

    public Product(
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            Category category) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.image = image;
        this.stock = stock;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public String getImage() {
        return image;
    }

    public Integer getStock() {
        return stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}