package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    // =====================================================
    // UPLOAD DIRECTORY
    // =====================================================

    @Value("${rishtabox.category.upload-dir:./uploads/categories}")
    private String uploadDir;


    public CategoryService(
            CategoryRepository categoryRepository
    ) {

        this.categoryRepository =
                categoryRepository;
    }


    // =====================================================
    // CREATE CATEGORY
    // =====================================================

    public Category createCategory(

            String name,

            String description,

            MultipartFile image,

            Boolean pinned

    ) throws IOException {


        // -------------------------------------------------
        // NAME VALIDATION
        // -------------------------------------------------

        if (
                name == null ||
                        name.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Category name is required."
            );
        }


        name = name.trim();


        // -------------------------------------------------
        // DUPLICATE CHECK
        // -------------------------------------------------

        if (
                categoryRepository.existsByName(name)
        ) {

            throw new RuntimeException(
                    "Category already exists"
            );
        }


        // -------------------------------------------------
        // GENERATE ID
        // -------------------------------------------------

        String id =
                name
                        .toLowerCase()
                        .replaceAll(
                                "[^a-z0-9]+",
                                "-"
                        )
                        .replaceAll(
                                "(^-|-$)",
                                ""
                        );


        // -------------------------------------------------
        // IMAGE
        // -------------------------------------------------

        String imagePath = null;


        if (
                image != null &&
                        !image.isEmpty()
        ) {

            validateImage(image);

            imagePath =
                    saveImage(image);
        }


        // -------------------------------------------------
        // CREATE CATEGORY
        // -------------------------------------------------

        Category category =
                new Category(
                        id,
                        name,
                        imagePath,
                        cleanOptionalValue(
                                description
                        ),
                        pinned != null
                                ? pinned
                                : false
                );


        return categoryRepository.save(
                category
        );
    }


    // =====================================================
    // GET ALL CATEGORIES
    // =====================================================

    public List<Category> getAllCategories() {

        return categoryRepository.findAll();
    }


    // =====================================================
    // GET CATEGORY BY ID
    // =====================================================

    public Category getCategoryById(
            String id
    ) {

        return categoryRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Category not found: " + id
                        )
                );
    }


    // =====================================================
    // UPDATE CATEGORY
    // =====================================================

    public Category updateCategory(

            String id,

            String name,

            String description,

            MultipartFile image,

            Boolean pinned

    ) throws IOException {


        Category category =
                getCategoryById(id);


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (
                name != null &&
                        !name.trim().isEmpty()
        ) {

            category.setName(
                    name.trim()
            );
        }


        // -------------------------------------------------
        // DESCRIPTION
        // -------------------------------------------------

        category.setDescription(
                cleanOptionalValue(
                        description
                )
        );


        // -------------------------------------------------
        // PINNED
        // -------------------------------------------------

        if (pinned != null) {

            category.setPinned(
                    pinned
            );
        }


        // -------------------------------------------------
        // IMAGE UPDATE
        // -------------------------------------------------

        /*
         * Image is optional during update.
         *
         * No new image:
         * old image remains.
         */

        if (
                image != null &&
                        !image.isEmpty()
        ) {

            validateImage(image);


            String oldImage =
                    category.getImage();


            String newImage =
                    saveImage(image);


            category.setImage(
                    newImage
            );


            deleteOldImage(
                    oldImage
            );
        }


        return categoryRepository.save(
                category
        );
    }


    // =====================================================
    // PIN / UNPIN CATEGORY
    // =====================================================

    public Category togglePin(

            String id,

            boolean pinned

    ) {

        Category category =
                getCategoryById(id);


        category.setPinned(
                pinned
        );


        return categoryRepository.save(
                category
        );
    }


    // =====================================================
    // DELETE CATEGORY
    // =====================================================

    public void deleteCategory(
            String id
    ) {

        Category category =
                getCategoryById(id);


        // Delete image first
        deleteOldImage(
                category.getImage()
        );


        categoryRepository.delete(
                category
        );
    }


    // =====================================================
    // CLEAN OPTIONAL VALUE
    // =====================================================

    private String cleanOptionalValue(
            String value
    ) {

        if (
                value == null ||
                        value.trim().isEmpty()
        ) {

            return null;
        }


        return value.trim();
    }


    // =====================================================
    // VALIDATE IMAGE
    // =====================================================

    private void validateImage(
            MultipartFile image
    ) {

        // -------------------------------------------------
        // MAX 5 MB
        // -------------------------------------------------

        long maxSize =
                5 * 1024 * 1024;


        if (
                image.getSize() > maxSize
        ) {

            throw new IllegalArgumentException(
                    "Image size must be less than 5 MB."
            );
        }


        // -------------------------------------------------
        // CONTENT TYPE
        // -------------------------------------------------

        String contentType =
                image.getContentType();


        if (
                contentType == null ||
                        !(
                                contentType.equalsIgnoreCase(
                                        "image/jpeg"
                                )
                                        ||
                                        contentType.equalsIgnoreCase(
                                                "image/png"
                                        )
                                        ||
                                        contentType.equalsIgnoreCase(
                                                "image/webp"
                                        )
                        )
        ) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed."
            );
        }
    }


    // =====================================================
    // SAVE IMAGE
    // =====================================================

    private String saveImage(
            MultipartFile image
    ) throws IOException {


        Path directory =
                Paths.get(
                        uploadDir
                );


        // Create directory if missing
        Files.createDirectories(
                directory
        );


        // -------------------------------------------------
        // EXTENSION
        // -------------------------------------------------

        String extension =
                getExtension(
                        image.getOriginalFilename()
                );


        // -------------------------------------------------
        // UNIQUE FILE NAME
        // -------------------------------------------------

        String fileName =
                UUID.randomUUID()
                        .toString()
                        + extension;


        Path filePath =
                directory.resolve(
                        fileName
                );


        // -------------------------------------------------
        // SAVE FILE
        // -------------------------------------------------

        Files.copy(
                image.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // -------------------------------------------------
        // DATABASE PATH
        // -------------------------------------------------

        return "/uploads/categories/"
                + fileName;
    }


    // =====================================================
    // GET FILE EXTENSION
    // =====================================================

    private String getExtension(
            String fileName
    ) {

        if (
                fileName == null ||
                        !fileName.contains(".")
        ) {

            return ".jpg";
        }


        String extension =
                fileName.substring(
                        fileName.lastIndexOf(".")
                ).toLowerCase();


        if (
                extension.equals(".jpeg") ||
                        extension.equals(".jpg") ||
                        extension.equals(".png") ||
                        extension.equals(".webp")
        ) {

            return extension;
        }


        return ".jpg";
    }


    // =====================================================
    // DELETE OLD IMAGE
    // =====================================================

    private void deleteOldImage(
            String imagePath
    ) {

        if (
                imagePath == null ||
                        imagePath.isBlank()
        ) {

            return;
        }


        try {

            String fileName =
                    Paths.get(
                                    imagePath
                            )
                            .getFileName()
                            .toString();


            Path file =
                    Paths.get(
                                    uploadDir
                            )
                            .resolve(
                                    fileName
                            );


            Files.deleteIfExists(
                    file
            );


        } catch (Exception e) {

            /*
             * Don't stop database operation
             * just because old image deletion failed.
             */

            System.err.println(
                    "Could not delete old category image: "
                            + e.getMessage()
            );
        }
    }

}