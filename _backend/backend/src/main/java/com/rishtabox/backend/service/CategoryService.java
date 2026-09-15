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

    public Category createCategory(String name) {

        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException(
                    "Category already exists"
            );
        }

        Category category = new Category(name);

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}