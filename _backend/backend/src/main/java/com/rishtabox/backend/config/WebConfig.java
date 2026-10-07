package com.rishtabox.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // =====================================================
    // UPLOAD DIRECTORIES
    // =====================================================

    @Value("${rishtabox.slider.upload-dir:./uploads/sliders}")
    private String sliderUploadDir;

    @Value("${rishtabox.category.upload-dir:./uploads/categories}")
    private String categoryUploadDir;

    @Value("${rishtabox.festival.upload-dir:./uploads/festivals}")
    private String festivalUploadDir;

    @Value("${rishtabox.relationship.upload-dir:./uploads/relationships}")
    private String relationshipUploadDir;

    @Value("${rishtabox.testimonial.upload-dir:./uploads/testimonials}")
    private String testimonialUploadDir;

    @Value("${rishtabox.blog.upload-dir:./uploads/blogs}")
    private String blogUploadDir;

    @Value("${rishtabox.product.upload-dir:./uploads/products}")
    private String productUploadDir;


    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        // =====================================================
        // SLIDERS
        // =====================================================

        Path sliderPath = Paths
                .get(sliderUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/sliders/**"
                )
                .addResourceLocations(
                        sliderPath.toUri().toString()
                );


        // =====================================================
        // CATEGORIES
        // =====================================================

        Path categoryPath = Paths
                .get(categoryUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/categories/**"
                )
                .addResourceLocations(
                        categoryPath.toUri().toString()
                );


        // =====================================================
        // FESTIVALS
        // =====================================================

        Path festivalPath = Paths
                .get(festivalUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/festivals/**"
                )
                .addResourceLocations(
                        festivalPath.toUri().toString()
                );


        // =====================================================
        // RELATIONSHIPS
        // =====================================================

        Path relationshipPath = Paths
                .get(relationshipUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/relationships/**"
                )
                .addResourceLocations(
                        relationshipPath.toUri().toString()
                );


        // =====================================================
        // TESTIMONIALS
        // =====================================================

        Path testimonialPath = Paths
                .get(testimonialUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/testimonials/**"
                )
                .addResourceLocations(
                        testimonialPath.toUri().toString()
                );


        // =====================================================
        // BLOGS
        // =====================================================

        Path blogPath = Paths
                .get(blogUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/blogs/**"
                )
                .addResourceLocations(
                        blogPath.toUri().toString()
                );


        // =====================================================
        // PRODUCTS
        // =====================================================

        Path productPath = Paths
                .get(productUploadDir)
                .toAbsolutePath()
                .normalize();

        registry
                .addResourceHandler(
                        "/uploads/products/**"
                )
                .addResourceLocations(
                        productPath.toUri().toString()
                );
    }
}