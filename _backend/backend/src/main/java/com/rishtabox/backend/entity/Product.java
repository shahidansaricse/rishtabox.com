 package com.rishtabox.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;

    private Double originalPrice;

    private String image;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "festival_id")
    private Festival festival;

    @ManyToOne
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    public Product(
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            Category category,
            Festival festival,
            Relationship relationship) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.image = image;
        this.stock = stock;
        this.category = category;
        this.festival = festival;
        this.relationship = relationship;
    }
}
