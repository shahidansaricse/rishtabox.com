package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Product> createProduct(

            @RequestParam("name")
            String name,

            @RequestParam(value = "description", required = false)
            String description,

            @RequestParam("price")
            Double price,

            @RequestParam(value = "originalPrice", required = false)
            Double originalPrice,

            @RequestParam("image")
            MultipartFile image,

            @RequestParam("stock")
            Integer stock,

            @RequestParam(value = "categoryId", required = false)
            String categoryId,

            @RequestParam(value = "festivalId", required = false)
            String festivalId,

            @RequestParam(value = "relationshipId", required = false)
            String relationshipId

    ) throws IOException {

        Product product = productService.createProduct(

                name,
                description,
                price,
                originalPrice,
                image,
                stock,
                categoryId,
                festivalId,
                relationshipId

        );

        return ResponseEntity.ok(product);
    }


    // =========================================================
    // GET ALL ACTIVE PRODUCTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }


    // =========================================================
    // GET ALL PRODUCTS FOR ADMIN
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {

        return ResponseEntity.ok(
                productService.getAllProductsForAdmin()
        );
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(

            @PathVariable Long id

    ) {

        Product product =
                productService.getProductById(id);

        return ResponseEntity.ok(product);
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Product> updateProduct(

            @PathVariable Long id,

            @RequestParam("name")
            String name,

            @RequestParam(value = "description", required = false)
            String description,

            @RequestParam("price")
            Double price,

            @RequestParam(value = "originalPrice", required = false)
            Double originalPrice,

            @RequestParam(value = "image", required = false)
            MultipartFile image,

            @RequestParam("stock")
            Integer stock,

            @RequestParam(value = "categoryId", required = false)
            String categoryId,

            @RequestParam(value = "festivalId", required = false)
            String festivalId,

            @RequestParam(value = "relationshipId", required = false)
            String relationshipId

    ) throws IOException {

        Product product =
                productService.updateProduct(

                        id,
                        name,
                        description,
                        price,
                        originalPrice,
                        image,
                        stock,
                        categoryId,
                        festivalId,
                        relationshipId

                );

        return ResponseEntity.ok(product);
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(

            @PathVariable Long id

    ) {

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
    // =========================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<Product> restoreProduct(

            @PathVariable Long id

    ) {

        Product product =
                productService.restoreProduct(id);

        return ResponseEntity.ok(product);
    }


    // =========================================================
    // PRODUCTS BY CATEGORY
    // =========================================================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(

            @PathVariable String categoryId

    ) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
        );
    }
}