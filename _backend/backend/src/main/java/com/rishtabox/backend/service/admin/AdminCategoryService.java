package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.repository.CategoryRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(String id) {

        if (id == null || id.isBlank()) {
            throw new RuntimeException("Category ID is required");
        }

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found with id: " + id
                        ));
    }

    @Transactional
    public Category createCategory(Category category) {

        if (category == null) {
            throw new RuntimeException("Category data is required");
        }

        if (category.getId() == null || category.getId().isBlank()) {
            throw new RuntimeException("Category ID is required");
        }

        if (category.getName() == null || category.getName().isBlank()) {
            throw new RuntimeException("Category name is required");
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(
            String id,
            Category category) {

        if (id == null || id.isBlank()) {
            throw new RuntimeException("Category ID is required");
        }

        if (category == null) {
            throw new RuntimeException("Category data is required");
        }

        Category existingCategory = getCategory(id);

        BeanUtils.copyProperties(
                category,
                existingCategory,
                "id"
        );

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(String id) {

        if (id == null || id.isBlank()) {
            throw new RuntimeException("Category ID is required");
        }

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException(
                    "Category not found with id: " + id
            );
        }

        categoryRepository.deleteById(id);
    }
}