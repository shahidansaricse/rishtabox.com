package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.ProductRequest;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // =========================================================
    // CREATE PRODUCT
    // POST /api/products
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody ProductRequest request) {

        Product product = productService.createProduct(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getOriginalPrice(),
                request.getImage(),
                request.getStock(),
                request.getCategoryId(),
                request.getFestivalId(),
                request.getRelationshipId()
        );

        return ResponseEntity.ok(product);
    }

    // =========================================================
    // GET ALL ACTIVE PRODUCTS
    // GET /api/products
    // PUBLIC
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    // =========================================================
    // GET ALL PRODUCTS FOR ADMIN
    // GET /api/products/admin/all
    // ACTIVE + INACTIVE
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {

        return ResponseEntity.ok(
                productService.getAllProductsForAdmin()
        );
    }

    // =========================================================
    // GET PRODUCT BY ID
    // GET /api/products/{id}
    // PUBLIC
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        Product product = productService.getProductById(id);

        return ResponseEntity.ok(product);
    }

    // =========================================================
    // UPDATE PRODUCT
    // PUT /api/products/{id}
    // ADMIN ONLY
    //
    // Category / Festival / Relationship
    // are handled independently
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request) {

        Product product = productService.updateProduct(
                id,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getOriginalPrice(),
                request.getImage(),
                request.getStock(),
                request.getCategoryId(),
                request.getFestivalId(),
                request.getRelationshipId()
        );

        return ResponseEntity.ok(product);
    }

    // =========================================================
    // SOFT DELETE PRODUCT
    // DELETE /api/products/{id}
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Product deactivated successfully"
                )
        );
    }

    // =========================================================
    // RESTORE PRODUCT
    // PUT /api/products/{id}/restore
    // ADMIN ONLY
    // =========================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<Product> restoreProduct(
            @PathVariable Long id) {

        Product product = productService.restoreProduct(id);

        return ResponseEntity.ok(product);
    }

    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // GET /api/products/category/{categoryId}
    // PUBLIC
    // ONLY ACTIVE PRODUCTS
    // =========================================================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
        );
    }
}