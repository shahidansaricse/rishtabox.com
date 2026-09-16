package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.CategoryRepository;
import com.rishtabox.backend.repository.FestivalRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.RelationshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FestivalRepository festivalRepository;
    private final RelationshipRepository relationshipRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FestivalRepository festivalRepository,
            RelationshipRepository relationshipRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.festivalRepository = festivalRepository;
        this.relationshipRepository = relationshipRepository;
    }

    public Product createProduct(
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            String categoryId,
            String festivalId,
            Long relationshipId) {

        // Find category
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"
                        ));

        // Find festival
        Festival festival = null;

        if (festivalId != null && !festivalId.isBlank()) {
            festival = festivalRepository
                    .findById(festivalId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Festival not found"
                            ));
        }

        // Find relationship
        Relationship relationship = null;

        if (relationshipId != null) {
            relationship = relationshipRepository
                    .findById(relationshipId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Relationship not found"
                            ));
        }

        // Create product
        Product product = new Product(
                name,
                description,
                price,
                originalPrice,
                image,
                stock,
                category,
                festival,
                relationship
        );

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));
    }

    public List<Product> getProductsByCategory(
            Long categoryId) {

        return productRepository
                .findByCategoryId(categoryId);
    }
}