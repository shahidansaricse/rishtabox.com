package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.service.admin.AdminProductService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(
            AdminProductService adminProductService) {

        this.adminProductService = adminProductService;
    }

    // ================= GET ALL =================

    @GetMapping
    public List<Product> getAllProducts() {

        return adminProductService.getAllProducts();
    }

    // ================= GET ONE =================

    @GetMapping("/{id}")
    public Product getProduct(
            @PathVariable Long id) {

        return adminProductService.getProduct(id);
    }

    // ================= CREATE =================

    @PostMapping
    public Product createProduct(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam(required = false) Double originalPrice,
            @RequestParam(required = false) String image,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String festivalId,
            @RequestParam(required = false) String relationshipId) {

        return adminProductService.createProduct(
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
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam Double price,
            @RequestParam(required = false) Double originalPrice,
            @RequestParam(required = false) String image,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String festivalId,
            @RequestParam(required = false) String relationshipId) {

        return adminProductService.updateProduct(
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
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        adminProductService.deleteProduct(id);

        return "Product deleted successfully";
    }
}