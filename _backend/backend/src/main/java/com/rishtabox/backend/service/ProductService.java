package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.repository.CategoryRepository;
import com.rishtabox.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product createProduct(
            String name,
            String description,
            Double price,
            Double originalPrice,
            String image,
            Integer stock,
            Long categoryId) {

        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"
                        ));

        Product product = new Product(
                name,
                description,
                price,
                originalPrice,
                image,
                stock,
                category
        );

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));
    }

    public List<Product> getProductsByCategory(
            Long categoryId) {

        return productRepository
                .findByCategoryId(categoryId);
    }
}