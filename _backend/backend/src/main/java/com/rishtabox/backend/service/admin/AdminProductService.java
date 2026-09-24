package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.CategoryRepository;
import com.rishtabox.backend.repository.FestivalRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.RelationshipRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FestivalRepository festivalRepository;
    private final RelationshipRepository relationshipRepository;

    public AdminProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FestivalRepository festivalRepository,
            RelationshipRepository relationshipRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.festivalRepository = festivalRepository;
        this.relationshipRepository = relationshipRepository;
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // =========================================================
    // GET PRODUCT BY ID
    // Product ID = Long
    // =========================================================

    public Product getProduct(Long id) {

        if (id == null) {
            throw new RuntimeException("Product ID is required");
        }

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        ));
    }

    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @Transactional
    public Product createProduct(
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            String categoryId,
            String festivalId,
            String relationshipId) {

        // -------------------------
        // VALIDATION
        // -------------------------

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product name is required");
        }

        if (price == null || price < 0) {
            throw new RuntimeException("Product price is invalid");
        }

        if (originalPrice != null && originalPrice < 0) {
            throw new RuntimeException("Original price is invalid");
        }

        if (stock != null && stock < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        // -------------------------
        // CREATE PRODUCT
        // -------------------------

        Product product = new Product();

        product.setName(name.trim());
        product.setDescription(description);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setImage(image);
        product.setStock(stock);

        // =====================================================
        // CATEGORY
        // Category ID = String
        // =====================================================

        if (categoryId != null && !categoryId.isBlank()) {

            Category category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Category not found with id: "
                                            + categoryId
                            ));

            product.setCategory(category);
        }

        // =====================================================
        // FESTIVAL
        // Festival ID = String
        // =====================================================

        if (festivalId != null && !festivalId.isBlank()) {

            Festival festival = festivalRepository
                    .findById(festivalId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Festival not found with id: "
                                            + festivalId
                            ));

            product.setFestival(festival);
        }

        // =====================================================
        // RELATIONSHIP
        // Relationship ID = String
        // =====================================================

        if (relationshipId != null && !relationshipId.isBlank()) {

            Relationship relationship = relationshipRepository
                    .findById(relationshipId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Relationship not found with id: "
                                            + relationshipId
                            ));

            product.setRelationship(relationship);
        }

        return productRepository.save(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Transactional
    public Product updateProduct(
            Long id,
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            String categoryId,
            String festivalId,
            String relationshipId) {

        // Product ID = Long
        Product product = getProduct(id);

        // -------------------------
        // VALIDATION
        // -------------------------

        if (name == null || name.isBlank()) {
            throw new RuntimeException("Product name is required");
        }

        if (price == null || price < 0) {
            throw new RuntimeException("Product price is invalid");
        }

        if (originalPrice != null && originalPrice < 0) {
            throw new RuntimeException("Original price is invalid");
        }

        if (stock != null && stock < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        // -------------------------
        // BASIC PRODUCT DATA
        // -------------------------

        product.setName(name.trim());
        product.setDescription(description);
        product.setPrice(price);
        product.setOriginalPrice(originalPrice);
        product.setImage(image);
        product.setStock(stock);

        // =====================================================
        // CATEGORY
        // Category ID = String
        // =====================================================

        if (categoryId != null && !categoryId.isBlank()) {

            Category category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Category not found with id: "
                                            + categoryId
                            ));

            product.setCategory(category);

        } else {

            product.setCategory(null);
        }

        // =====================================================
        // FESTIVAL
        // Festival ID = String
        // =====================================================

        if (festivalId != null && !festivalId.isBlank()) {

            Festival festival = festivalRepository
                    .findById(festivalId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Festival not found with id: "
                                            + festivalId
                            ));

            product.setFestival(festival);

        } else {

            product.setFestival(null);
        }

        // =====================================================
        // RELATIONSHIP
        // Relationship ID = String
        // =====================================================

        if (relationshipId != null && !relationshipId.isBlank()) {

            Relationship relationship = relationshipRepository
                    .findById(relationshipId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Relationship not found with id: "
                                            + relationshipId
                            ));

            product.setRelationship(relationship);

        } else {

            product.setRelationship(null);
        }

        return productRepository.save(product);
    }

    // =========================================================
    // DELETE PRODUCT
    // Product ID = Long
    // =========================================================

    @Transactional
    public void deleteProduct(Long id) {

        if (id == null) {
            throw new RuntimeException("Product ID is required");
        }

        if (!productRepository.existsById(id)) {
            throw new RuntimeException(
                    "Product not found with id: " + id
            );
        }

        productRepository.deleteById(id);
    }
}