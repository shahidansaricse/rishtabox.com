package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.service.CategoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(
            CategoryService categoryService
    ) {

        this.categoryService =
                categoryService;
    }


    // =====================================================
    // CREATE CATEGORY
    // =====================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Category> createCategory(

            @RequestParam("name")
            String name,

            @RequestParam(
                    value = "description",
                    required = false
            )
            String description,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            @RequestParam(
                    value = "pinned",
                    required = false
            )
            Boolean pinned

    ) throws IOException {


        Category category =
                categoryService.createCategory(
                        name,
                        description,
                        image,
                        pinned
                );


        return ResponseEntity.ok(
                category
        );
    }


    // =====================================================
    // GET ALL CATEGORIES
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }


    // =====================================================
    // GET CATEGORY BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(
            @PathVariable String id
    ) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }


    // =====================================================
    // UPDATE CATEGORY
    // =====================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Category> updateCategory(

            @PathVariable String id,

            @RequestParam("name")
            String name,

            @RequestParam(
                    value = "description",
                    required = false
            )
            String description,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            @RequestParam(
                    value = "pinned",
                    required = false
            )
            Boolean pinned

    ) throws IOException {


        Category category =
                categoryService.updateCategory(
                        id,
                        name,
                        description,
                        image,
                        pinned
                );


        return ResponseEntity.ok(
                category
        );
    }


    // =====================================================
    // PIN / UNPIN CATEGORY
    // =====================================================

    @PutMapping("/{id}/pin")
    public ResponseEntity<Category> toggleCategoryPin(

            @PathVariable String id,

            @RequestParam boolean pinned

    ) {

        return ResponseEntity.ok(
                categoryService.togglePin(
                        id,
                        pinned
                )
        );
    }


    // =====================================================
    // DELETE CATEGORY
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(

            @PathVariable String id

    ) {

        categoryService.deleteCategory(
                id
        );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Category deleted successfully"
                )
        );
    }

}