package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================================================
    // GET ACTIVE PRODUCTS
    // Used by customer website
    // =========================================================

    List<Product> findByActiveTrue();


    // =========================================================
    // GET ACTIVE PRODUCTS BY CATEGORY
    // Used by customer website
    // =========================================================

    List<Product> findByCategoryIdAndActiveTrue(
            String categoryId
    );


    // =========================================================
    // GET ALL PRODUCTS BY CATEGORY
    // Used by admin / category management
    // =========================================================

    List<Product> findByCategoryId(
            String categoryId
    );


    // =========================================================
    // GET ALL PRODUCTS BY FESTIVAL
    // Used by admin / festival management
    // =========================================================

    List<Product> findByFestivalId(
            String festivalId
    );
}