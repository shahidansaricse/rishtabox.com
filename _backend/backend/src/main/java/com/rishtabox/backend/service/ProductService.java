package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Category;
import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.entity.Product;
import com.rishtabox.backend.entity.Relationship;

import com.rishtabox.backend.repository.CategoryRepository;
import com.rishtabox.backend.repository.FestivalRepository;
import com.rishtabox.backend.repository.ProductRepository;
import com.rishtabox.backend.repository.RelationshipRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;


@Service
public class ProductService {


    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final FestivalRepository festivalRepository;

    private final RelationshipRepository relationshipRepository;


    // =========================================================
    // PRODUCT IMAGE UPLOAD DIRECTORY
    // =========================================================

    @Value("${rishtabox.product.upload-dir:./uploads/products}")
    private String uploadDir;


    // =========================================================
    // CREATE PRODUCT UPLOAD DIRECTORY
    // =========================================================

    @PostConstruct
    public void createUploadDirectory() {

        try {

            Path directory =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(directory);

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "PRODUCT UPLOAD DIRECTORY:"
            );

            System.out.println(directory);

            System.out.println(
                    "DIRECTORY EXISTS: "
                            + Files.exists(directory)
            );

            System.out.println(
                    "DIRECTORY IS DIRECTORY: "
                            + Files.isDirectory(directory)
            );

            System.out.println(
                    "========================================"
            );

        } catch (IOException e) {

            System.err.println(
                    "FAILED TO CREATE PRODUCT UPLOAD DIRECTORY:"
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FestivalRepository festivalRepository,
            RelationshipRepository relationshipRepository
    ) {

        this.productRepository =
                productRepository;

        this.categoryRepository =
                categoryRepository;

        this.festivalRepository =
                festivalRepository;

        this.relationshipRepository =
                relationshipRepository;
    }


    // =========================================================
    // CREATE PRODUCT
    // =========================================================

    public Product createProduct(

            String name,

            String description,

            Double price,

            Double originalPrice,

            MultipartFile image,

            Integer stock,

            String categoryId,

            String festivalId,

            String relationshipId

    ) throws IOException {


        // -----------------------------------------------------
        // PRODUCT NAME
        // -----------------------------------------------------

        if (
                name == null ||
                        name.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Product name is required."
            );
        }


        // -----------------------------------------------------
        // STOCK
        // -----------------------------------------------------

        if (
                stock == null ||
                        stock < 0
        ) {

            throw new IllegalArgumentException(
                    "Stock cannot be negative."
            );
        }


        // -----------------------------------------------------
        // IMAGE
        // -----------------------------------------------------

        if (
                image == null ||
                        image.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Product image is required."
            );
        }


        validateImage(image);


        // -----------------------------------------------------
        // SAVE IMAGE
        // -----------------------------------------------------

        String imagePath =
                saveImage(image);


        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Category category =
                getCategory(categoryId);


        // -----------------------------------------------------
        // FESTIVAL
        // -----------------------------------------------------

        Festival festival =
                getFestival(festivalId);


        // -----------------------------------------------------
        // RELATIONSHIP
        // -----------------------------------------------------

        Relationship relationship =
                getRelationship(relationshipId);


        // -----------------------------------------------------
        // CREATE PRODUCT
        // -----------------------------------------------------

        Product product =
                new Product(

                        name.trim(),

                        cleanOptionalValue(
                                description
                        ),

                        price,

                        originalPrice,

                        imagePath,

                        stock,

                        category,

                        festival,

                        relationship

                );


        // -----------------------------------------------------
        // ACTIVE
        // -----------------------------------------------------

        product.setActive(true);


        // -----------------------------------------------------
        // SAVE PRODUCT
        // -----------------------------------------------------

        return productRepository.save(product);
    }


    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }


    // =========================================================
    // GET ALL PRODUCTS FOR ADMIN
    // =========================================================

    public List<Product> getAllProductsForAdmin() {

        return productRepository.findAll();
    }


    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    public Product getProductById(
            Long id
    ) {

        return productRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Product not found"
                                )
                );
    }


    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    public Product updateProduct(

            Long id,

            String name,

            String description,

            Double price,

            Double originalPrice,

            MultipartFile image,

            Integer stock,

            String categoryId,

            String festivalId,

            String relationshipId

    ) throws IOException {


        // -----------------------------------------------------
        // FIND PRODUCT
        // -----------------------------------------------------

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found"
                                        )
                        );


        // -----------------------------------------------------
        // VALIDATE NAME
        // -----------------------------------------------------

        if (
                name == null ||
                        name.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Product name is required."
            );
        }


        // -----------------------------------------------------
        // VALIDATE STOCK
        // -----------------------------------------------------

        if (
                stock == null ||
                        stock < 0
        ) {

            throw new IllegalArgumentException(
                    "Stock cannot be negative."
            );
        }


        // -----------------------------------------------------
        // CATEGORY
        // -----------------------------------------------------

        Category category =
                getCategory(categoryId);


        // -----------------------------------------------------
        // FESTIVAL
        // -----------------------------------------------------

        Festival festival =
                getFestival(festivalId);


        // -----------------------------------------------------
        // RELATIONSHIP
        // -----------------------------------------------------

        Relationship relationship =
                getRelationship(relationshipId);


        // -----------------------------------------------------
        // BASIC DETAILS
        // -----------------------------------------------------

        product.setName(
                name.trim()
        );

        product.setDescription(
                cleanOptionalValue(description)
        );

        product.setPrice(price);

        product.setOriginalPrice(
                originalPrice
        );

        product.setStock(stock);


        // -----------------------------------------------------
        // RELATIONS
        // -----------------------------------------------------

        product.setCategory(category);

        product.setFestival(festival);

        product.setRelationship(
                relationship
        );


        // -----------------------------------------------------
        // IMAGE UPDATE
        // -----------------------------------------------------

        if (
                image != null &&
                        !image.isEmpty()
        ) {

            // Validate new image
            validateImage(image);


            // Get old image path
            String oldImage =
                    product.getImage();


            // Save new image
            String newImage =
                    saveImage(image);


            // Update database image path
            product.setImage(newImage);


            // Delete old uploaded image
            deleteOldImage(oldImage);
        }


        // -----------------------------------------------------
        // SAVE UPDATED PRODUCT
        // -----------------------------------------------------

        return productRepository.save(product);
    }


    // =========================================================
    // GET CATEGORY
    // =========================================================

    private Category getCategory(
            String categoryId
    ) {

        if (
                categoryId == null ||
                        categoryId.isBlank()
        ) {

            return null;
        }


        return categoryRepository
                .findById(
                        categoryId.trim()
                )
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Category not found: "
                                                + categoryId
                                )
                );
    }


    // =========================================================
    // GET FESTIVAL
    // =========================================================

    private Festival getFestival(
            String festivalId
    ) {

        if (
                festivalId == null ||
                        festivalId.isBlank()
        ) {

            return null;
        }


        return festivalRepository
                .findById(
                        festivalId.trim()
                )
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Festival not found: "
                                                + festivalId
                                )
                );
    }


    // =========================================================
    // GET RELATIONSHIP
    // =========================================================

    private Relationship getRelationship(
            String relationshipId
    ) {

        if (
                relationshipId == null ||
                        relationshipId.isBlank()
        ) {

            return null;
        }


        return relationshipRepository
                .findById(
                        relationshipId.trim()
                )
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Relationship not found: "
                                                + relationshipId
                                )
                );
    }


    // =========================================================
    // DELETE PRODUCT
    // =========================================================

    public void deleteProduct(
            Long id
    ) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found"
                                        )
                        );


        product.setActive(false);


        productRepository.save(product);
    }


    // =========================================================
    // RESTORE PRODUCT
    // =========================================================

    public Product restoreProduct(
            Long id
    ) {

        Product product =
                productRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found"
                                        )
                        );


        product.setActive(true);


        return productRepository.save(product);
    }


    // =========================================================
    // DECREASE STOCK
    // =========================================================

    public Product decreaseStock(

            Long productId,

            Integer quantity

    ) {

        if (
                quantity == null ||
                        quantity <= 0
        ) {

            throw new RuntimeException(
                    "Invalid quantity"
            );
        }


        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Product not found"
                                        )
                        );


        Integer currentStock =
                product.getStock();


        if (
                currentStock == null
        ) {

            currentStock = 0;
        }


        if (
                currentStock < quantity
        ) {

            throw new RuntimeException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }


        int newStock =
                currentStock - quantity;


        product.setStock(newStock);


        return productRepository.save(product);
    }


    // =========================================================
    // GET PRODUCTS BY CATEGORY
    // =========================================================

    public List<Product> getProductsByCategory(
            String categoryId
    ) {

        if (
                categoryId == null ||
                        categoryId.isBlank()
        ) {

            throw new RuntimeException(
                    "Category ID is required"
            );
        }


        return productRepository
                .findAll()
                .stream()
                .filter(

                        product ->

                                product.getCategory() != null

                                        &&

                                        product.getCategory().getId() != null

                                        &&

                                        categoryId.equals(
                                                product
                                                        .getCategory()
                                                        .getId()
                                        )

                )
                .collect(
                        Collectors.toList()
                );
    }


    // =========================================================
    // CLEAN OPTIONAL VALUE
    // =========================================================

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


    // =========================================================
    // VALIDATE IMAGE
    // =========================================================

    private void validateImage(
            MultipartFile image
    ) {

        // -----------------------------------------------------
        // NULL / EMPTY
        // -----------------------------------------------------

        if (
                image == null ||
                        image.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Product image is required."
            );
        }


        // -----------------------------------------------------
        // MAX 5 MB
        // -----------------------------------------------------

        long maxSize =
                5L * 1024L * 1024L;


        if (
                image.getSize() > maxSize
        ) {

            throw new IllegalArgumentException(
                    "Image size must be less than 5 MB."
            );
        }


        // -----------------------------------------------------
        // CONTENT TYPE
        // -----------------------------------------------------

        String contentType =
                image.getContentType();


        if (
                contentType == null
                        ||
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


    // =========================================================
    // SAVE IMAGE
    // =========================================================

    private String saveImage(
            MultipartFile image
    ) throws IOException {


        System.out.println(
                "🔥🔥 SAVE IMAGE METHOD CALLED 🔥🔥"
        );


        // -----------------------------------------------------
        // UPLOAD DIRECTORY
        // -----------------------------------------------------

        Path directory =
                Paths.get(uploadDir)
                        .toAbsolutePath()
                        .normalize();


        // -----------------------------------------------------
        // CREATE DIRECTORY
        // -----------------------------------------------------

        Files.createDirectories(
                directory
        );


        // -----------------------------------------------------
        // DEBUG - DIRECTORY
        // -----------------------------------------------------

        System.out.println(
                "========================================"
        );

        System.out.println(
                "PRODUCT IMAGE UPLOAD"
        );

        System.out.println(
                "UPLOAD DIRECTORY:"
        );

        System.out.println(
                directory
        );

        System.out.println(
                "DIRECTORY EXISTS: "
                        + Files.exists(directory)
        );

        System.out.println(
                "DIRECTORY IS DIRECTORY: "
                        + Files.isDirectory(directory)
        );

        System.out.println(
                "CAN WRITE DIRECTORY: "
                        + Files.isWritable(directory)
        );

        System.out.println(
                "ORIGINAL FILE NAME:"
        );

        System.out.println(
                image.getOriginalFilename()
        );

        System.out.println(
                "CONTENT TYPE:"
        );

        System.out.println(
                image.getContentType()
        );

        System.out.println(
                "FILE SIZE:"
        );

        System.out.println(
                image.getSize()
        );


        // -----------------------------------------------------
        // EXTENSION
        // -----------------------------------------------------

        String extension =
                getExtension(
                        image.getOriginalFilename()
                );


        // -----------------------------------------------------
        // UNIQUE FILE NAME
        // -----------------------------------------------------

        String fileName =
                UUID.randomUUID()
                        .toString()
                        + extension;


        // -----------------------------------------------------
        // FILE PATH
        // -----------------------------------------------------

        Path filePath =
                directory
                        .resolve(fileName)
                        .normalize();


        // -----------------------------------------------------
        // SECURITY CHECK
        // -----------------------------------------------------

        if (
                !filePath.startsWith(directory)
        ) {

            throw new IOException(
                    "Invalid product image path."
            );
        }


        // -----------------------------------------------------
        // DEBUG - BEFORE SAVE
        // -----------------------------------------------------

        System.out.println(
                "========================================"
        );

        System.out.println(
                "BEFORE FILE SAVE"
        );

        System.out.println(
                "FILE PATH:"
        );

        System.out.println(
                filePath
        );

        System.out.println(
                "FILE EXISTS BEFORE:"
                        + Files.exists(filePath)
        );

        System.out.println(
                "========================================"
        );


        // -----------------------------------------------------
        // SAVE FILE
        // -----------------------------------------------------

        Files.copy(

                image.getInputStream(),

                filePath,

                StandardCopyOption.REPLACE_EXISTING

        );


        // -----------------------------------------------------
        // PHYSICAL FILE CHECK
        // -----------------------------------------------------

        System.out.println(
                "========================================"
        );

        System.out.println(
                "PRODUCT FILE CHECK"
        );

        System.out.println(
                "uploadDir = "
                        + uploadDir
        );

        System.out.println(
                "directory = "
                        + directory
        );

        System.out.println(
                "filePath = "
                        + filePath
        );

        System.out.println(
                "absolute = "
                        + filePath.toAbsolutePath()
        );

        System.out.println(
                "exists = "
                        + Files.exists(filePath)
        );

        System.out.println(
                "isFile = "
                        + Files.isRegularFile(filePath)
        );


        if (
                Files.exists(filePath)
        ) {

            System.out.println(
                    "size = "
                            + Files.size(filePath)
            );
        }


        System.out.println(
                "========================================"
        );


        // -----------------------------------------------------
        // VERIFY FILE
        // -----------------------------------------------------

        if (
                !Files.exists(filePath)
        ) {

            throw new IOException(
                    "Product image was not created at: "
                            + filePath
            );
        }


        if (
                !Files.isRegularFile(filePath)
        ) {

            throw new IOException(
                    "Product image path is not a regular file: "
                            + filePath
            );
        }


        // -----------------------------------------------------
        // FINAL DEBUG
        // -----------------------------------------------------

        System.out.println(
                "PRODUCT IMAGE SAVED:"
        );

        System.out.println(
                filePath
        );

        System.out.println(
                "FILE EXISTS:"
                        + Files.exists(filePath)
        );

        System.out.println(
                "FILE SIZE AFTER SAVE:"
                        + Files.size(filePath)
        );

        System.out.println(
                "========================================"
        );


        // -----------------------------------------------------
        // DATABASE IMAGE PATH
        // -----------------------------------------------------

        return "/uploads/products/"
                + fileName;
    }


    // =========================================================
    // GET FILE EXTENSION
    // =========================================================

    private String getExtension(
            String fileName
    ) {

        if (
                fileName == null ||
                        fileName.isBlank() ||
                        !fileName.contains(".")
        ) {

            return ".jpg";
        }


        String extension =
                fileName
                        .substring(
                                fileName.lastIndexOf(".")
                        )
                        .toLowerCase();


        if (
                extension.equals(".jpeg")
                        ||
                        extension.equals(".jpg")
                        ||
                        extension.equals(".png")
                        ||
                        extension.equals(".webp")
        ) {

            return extension;
        }


        return ".jpg";
    }


    // =========================================================
    // DELETE OLD IMAGE
    // =========================================================

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

            // -------------------------------------------------
            // GET FILE NAME
            // -------------------------------------------------

            String fileName =
                    Paths.get(imagePath)
                            .getFileName()
                            .toString();


            // -------------------------------------------------
            // UPLOAD DIRECTORY
            // -------------------------------------------------

            Path uploadDirectory =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();


            // -------------------------------------------------
            // FILE
            // -------------------------------------------------

            Path file =
                    uploadDirectory
                            .resolve(fileName)
                            .normalize();


            // -------------------------------------------------
            // SECURITY CHECK
            // -------------------------------------------------

            if (
                    !file.startsWith(
                            uploadDirectory
                    )
            ) {

                System.err.println(
                        "Invalid old image path: "
                                + imagePath
                );

                return;
            }


            // -------------------------------------------------
            // DELETE OLD FILE
            // -------------------------------------------------

            Files.deleteIfExists(file);


            System.out.println(
                    "OLD PRODUCT IMAGE DELETED:"
            );

            System.out.println(file);


        } catch (Exception e) {

            System.err.println(
                    "Could not delete old product image: "
                            + e.getMessage()
            );
        }
    }

}