package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository
        extends JpaRepository<Category, String> {

    boolean existsByName(String name);
}