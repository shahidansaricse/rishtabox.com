package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // =========================
    // CREATE CATEGORY
    // =========================

    public Category createCategory(String name) {

        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException(
                    "Category already exists"
            );
        }

        // Create ID from category name
        String id = name
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        Category category = new Category(
                id,
                name,
                null,
                null,
                false
        );

        return categoryRepository.save(category);
    }

    // =========================
    // GET ALL CATEGORIES
    // =========================

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // =========================
    // UPDATE CATEGORY
    // =========================

    public Category updateCategory(
            String id,
            Category updatedCategory) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " + id
                                ));

        category.setName(
                updatedCategory.getName()
        );

        category.setDescription(
                updatedCategory.getDescription()
        );

        category.setImage(
                updatedCategory.getImage()
        );

        category.setPinned(
                updatedCategory.isPinned()
        );

        return categoryRepository.save(category);
    }

    // =========================
    // PIN / UNPIN CATEGORY
    // =========================

    public Category togglePin(
            String id,
            boolean pinned) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found: " + id
                                ));

        category.setPinned(pinned);

        return categoryRepository.save(category);
    }
}