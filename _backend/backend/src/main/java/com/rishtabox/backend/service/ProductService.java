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
import java.util.stream.Collectors;

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

    // =========================================================
    // CREATE PRODUCT
    // Category / Festival / Relationship are OPTIONAL
    // =========================================================

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

        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Category category = null;

        if (categoryId != null && !categoryId.isBlank()) {

            category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Category not found"
                            ));
        }

        // -----------------------------------------------------
        // FESTIVAL
        // -----------------------------------------------------

        Festival festival = null;

        if (festivalId != null && !festivalId.isBlank()) {

            festival = festivalRepository
                    .findById(festivalId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Festival not found"
                            ));
        }

        // -----------------------------------------------------
        // RELATIONSHIP
        // -----------------------------------------------------

        Relationship relationship = null;

        if (relationshipId != null && !relationshipId.isBlank()) {

            relationship = relationshipRepository
                    .findById(relationshipId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Relationship not found"
                            ));
        }

        // -----------------------------------------------------
        // STOCK VALIDATION
        // -----------------------------------------------------

        if (stock == null || stock < 0) {

            throw new RuntimeException(
                    "Stock cannot be negative"
            );
        }

        // -----------------------------------------------------
        // CREATE PRODUCT
        // -----------------------------------------------------

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

        // -----------------------------------------------------
        // NEW PRODUCT IS ACTIVE
        // -----------------------------------------------------

        product.setActive(true);

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        return productRepository.save(product);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // CUSTOMER WEBSITE
    //
    // IMPORTANT:
    // Inactive products are also returned.
    //
    // Frontend decides:
    //
    // active = true + stock > 0
    //       => In Stock
    //
    // active = false OR stock = 0
    //       => Out of Stock
    // =========================================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    // =========================================================
    // GET ALL PRODUCTS FOR ADMIN
    // ACTIVE + INACTIVE
    // =========================================================

    public List<Product> getAllProductsForAdmin() {

        return productRepository.findAll();
    }

    // =========================================================
    // GET PRODUCT BY ID
    // PUBLIC
    //
    // Inactive product bhi return karega.
    // Frontend usko Out of Stock dikha sakta hai.
    // =========================================================

    public Product getProductById(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));
    }

    // =========================================================
    // UPDATE PRODUCT
    // Category / Festival / Relationship are OPTIONAL
    // =========================================================

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

        // -----------------------------------------------------
        // FIND EXISTING PRODUCT
        // -----------------------------------------------------

        Product product = productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        // -----------------------------------------------------
        // STOCK VALIDATION
        // -----------------------------------------------------

        if (stock == null || stock < 0) {

            throw new RuntimeException(
                    "Stock cannot be negative"
            );
        }

        // -----------------------------------------------------
        // CATEGORY
        // Blank = remove old category
        // -----------------------------------------------------

        Category category = null;

        if (categoryId != null && !categoryId.isBlank()) {

            category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Category not found"
                            ));
        }

        // -----------------------------------------------------
        // FESTIVAL
        // Blank = remove old festival
        // -----------------------------------------------------

        Festival festival = null;

        if (festivalId != null && !festivalId.isBlank()) {

            festival = festivalRepository
                    .findById(festivalId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Festival not found"
                            ));
        }

        // -----------------------------------------------------
        // RELATIONSHIP
        // Blank = remove old relationship
        // -----------------------------------------------------

        Relationship relationship = null;

        if (relationshipId != null && !relationshipId.isBlank()) {

            relationship = relationshipRepository
                    .findById(relationshipId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Relationship not found"
                            ));
        }

        // -----------------------------------------------------
        // UPDATE BASIC PRODUCT DETAILS
        // -----------------------------------------------------

        product.setName(name);

        product.setDescription(description);

        product.setPrice(price);

        product.setOriginalPrice(originalPrice);

        product.setImage(image);

        product.setStock(stock);

        // -----------------------------------------------------
        // UPDATE RELATIONS INDEPENDENTLY
        // -----------------------------------------------------

        product.setCategory(category);

        product.setFestival(festival);

        product.setRelationship(relationship);

        // -----------------------------------------------------
        // IMPORTANT
        //
        // Do NOT automatically change active here.
        //
        // active = admin control
        // stock  = inventory control
        // -----------------------------------------------------

        // -----------------------------------------------------
        // SAVE UPDATED PRODUCT
        // -----------------------------------------------------

        return productRepository.save(product);
    }

    // =========================================================
    // SOFT DELETE / DEACTIVATE PRODUCT
    //
    // Product database se delete nahi hoga.
    //
    // active = false
    //
    // Customer website par frontend:
    // active = false
    // => Out of Stock
    // =========================================================

    public void deleteProduct(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        product.setActive(false);

        productRepository.save(product);
    }

    // =========================================================
    // RESTORE PRODUCT
    //
    // active = true
    //
    // NOTE:
    // Agar stock 0 hai, frontend phir bhi
    // Out of Stock dikhayega.
    // =========================================================

    public Product restoreProduct(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        product.setActive(true);

        return productRepository.save(product);
    }

    // =========================================================
    // DECREASE STOCK
    //
    // Order successful hone ke baad call karein.
    //
    // Example:
    //
    // stock = 20
    // quantity = 2
    //
    // new stock = 18
    //
    // IMPORTANT:
    // active automatic false nahi hoga.
    // =========================================================

    public Product decreaseStock(
            Long productId,
            Integer quantity) {

        // -----------------------------------------------------
        // VALIDATE QUANTITY
        // -----------------------------------------------------

        if (quantity == null || quantity <= 0) {

            throw new RuntimeException(
                    "Invalid quantity"
            );
        }

        // -----------------------------------------------------
        // FIND PRODUCT
        // -----------------------------------------------------

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        // -----------------------------------------------------
        // CURRENT STOCK
        // -----------------------------------------------------

        Integer currentStock =
                product.getStock();

        if (currentStock == null) {
            currentStock = 0;
        }

        // -----------------------------------------------------
        // CHECK STOCK
        // -----------------------------------------------------

        if (currentStock < quantity) {

            throw new RuntimeException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }

        // -----------------------------------------------------
        // CALCULATE NEW STOCK
        // -----------------------------------------------------

        int newStock =
                currentStock - quantity;

        // -----------------------------------------------------
        // UPDATE STOCK
        // -----------------------------------------------------

        product.setStock(newStock);

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        return productRepository.save(product);
    }

    // =========================================================
    // GET PRODUCTS BY CATEGORY
    //
    // Includes inactive + out-of-stock products.
    //
    // Frontend can show:
    //
    // active = false
    // OR
    // stock = 0
    //
    // => Out of Stock
    // =========================================================

    public List<Product> getProductsByCategory(
            String categoryId) {

        if (categoryId == null || categoryId.isBlank()) {

            throw new RuntimeException(
                    "Category ID is required"
            );
        }

        return productRepository
                .findAll()
                .stream()
                .filter(product ->
                        product.getCategory() != null
                                &&
                                product.getCategory().getId() != null
                                &&
                                categoryId.equals(
                                        product.getCategory().getId()
                                )
                )
                .collect(Collectors.toList());
    }
}