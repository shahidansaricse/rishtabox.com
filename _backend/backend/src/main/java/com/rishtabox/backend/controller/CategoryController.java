package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // =========================
    // CREATE CATEGORY
    // =========================

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestParam String name) {

        return ResponseEntity.ok(
                categoryService.createCategory(name)
        );
    }

    // =========================
    // GET ALL CATEGORIES
    // =========================

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    // =========================
    // UPDATE CATEGORY
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id,
            @RequestBody Category updatedCategory) {

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        id,
                        updatedCategory
                )
        );
    }

    // =========================
    // PIN / UNPIN CATEGORY
    // =========================

    @PutMapping("/{id}/pin")
    public ResponseEntity<Category> toggleCategoryPin(
            @PathVariable String id,
            @RequestParam boolean pinned) {

        return ResponseEntity.ok(
                categoryService.togglePin(
                        id,
                        pinned
                )
        );
    }
}