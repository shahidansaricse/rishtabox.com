package com.rishtabox.backend.controller;

import com.rishtabox.backend.dto.ProductRequest;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                productService.createProduct(
                        request.getName(),
                        request.getDescription(),
                        request.getPrice(),
                        request.getOriginalPrice(),
                        request.getImage(),
                        request.getStock(),
                        request.getCategoryId(),
                        request.getFestivalId(),
                        request.getRelationshipId()
                )
        );
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    // GET PRODUCTS BY CATEGORY
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
        );
    }
}