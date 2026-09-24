package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.repository.CategoryRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    public AdminCategoryController(
            CategoryRepository categoryRepository) {

        this.categoryRepository = categoryRepository;
    }

    // =========================
    // GET ALL CATEGORIES
    // =========================
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {

        return ResponseEntity.ok(
                categoryRepository.findAll()
        );
    }

    // =========================
    // GET CATEGORY BY ID
    // Category ID = String
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(
            @PathVariable String id) {

        return categoryRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Category not found"
                                ))
                );
    }

    // =========================
    // CREATE CATEGORY
    // =========================
    @PostMapping
    public ResponseEntity<?> createCategory(
            @RequestBody Category category) {

        try {

            if (category.getId() == null ||
                    category.getId().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Category ID is required"
                        ));
            }

            if (category.getName() == null ||
                    category.getName().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Category name is required"
                        ));
            }

            Category savedCategory =
                    categoryRepository.save(category);

            return ResponseEntity.ok(savedCategory);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================
    // UPDATE CATEGORY
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable String id,
            @RequestBody Category category) {

        try {

            Category existingCategory =
                    categoryRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Category not found"
                                    )
                            );

            if (category.getName() == null ||
                    category.getName().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Category name is required"
                        ));
            }

            // Update name
            existingCategory.setName(
                    category.getName().trim()
            );

            // Update description
            existingCategory.setDescription(
                    category.getDescription()
            );

            // Update image
            existingCategory.setImage(
                    category.getImage()
            );

            Category updatedCategory =
                    categoryRepository.save(
                            existingCategory
                    );

            return ResponseEntity.ok(updatedCategory);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================
    // DELETE CATEGORY
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable String id) {

        try {

            if (!categoryRepository.existsById(id)) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Category not found"
                        ));
            }

            categoryRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Category deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}