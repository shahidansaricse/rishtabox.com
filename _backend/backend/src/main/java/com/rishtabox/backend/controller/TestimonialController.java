package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Testimonial;
import com.rishtabox.backend.repository.TestimonialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;

    /*
     * =========================================================
     * TESTIMONIAL UPLOAD DIRECTORY
     * =========================================================
     *
     * This value comes from application.properties:
     *
     * rishtabox.testimonial.upload-dir=
     * C:/Users/MD SHAHID ANSARI/RishtaBox/Frontend/uploads/testimonials
     *
     */

    @Value("${rishtabox.testimonial.upload-dir}")
    private String testimonialUploadDir;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TestimonialController(
            TestimonialRepository testimonialRepository
    ) {

        this.testimonialRepository =
                testimonialRepository;
    }


    // =========================================================
    // GET TESTIMONIAL UPLOAD DIRECTORY
    // =========================================================

    private Path getTestimonialUploadDirectory() {

        return Paths
                .get(testimonialUploadDir)
                .toAbsolutePath()
                .normalize();
    }


    // =========================================================
    // ADMIN - GET ALL TESTIMONIALS
    // =========================================================

    @GetMapping("/admin/all")
    public ResponseEntity<List<Testimonial>> getAllTestimonials() {

        return ResponseEntity.ok(
                testimonialRepository.findAll()
        );
    }


    // =========================================================
    // CUSTOMER - GET ONLY PUBLISHED TESTIMONIALS
    // =========================================================

    @GetMapping("/published")
    public ResponseEntity<List<Testimonial>> getPublishedTestimonials() {

        return ResponseEntity.ok(
                testimonialRepository.findByStatusIgnoreCase(
                        "PUBLISHED"
                )
        );
    }


    // =========================================================
    // CREATE TESTIMONIAL
    // =========================================================

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTestimonial(

            @RequestParam("customerName")
            String customerName,

            @RequestParam("rating")
            Integer rating,

            @RequestParam("message")
            String message,

            @RequestParam(value = "status", required = false)
            String status,

            @RequestParam(value = "image", required = false)
            MultipartFile image

    ) {

        try {

            // =================================================
            // VALIDATION
            // =================================================

            if (
                    customerName == null ||
                            customerName.isBlank()
            ) {

                return ResponseEntity.badRequest()
                        .body(
                                "Customer name is required."
                        );
            }


            if (
                    message == null ||
                            message.isBlank()
            ) {

                return ResponseEntity.badRequest()
                        .body(
                                "Testimonial message is required."
                        );
            }


            if (
                    rating == null ||
                            rating < 1 ||
                            rating > 5
            ) {

                return ResponseEntity.badRequest()
                        .body(
                                "Rating must be between 1 and 5."
                        );
            }


            // =================================================
            // CREATE TESTIMONIAL
            // =================================================

            Testimonial testimonial =
                    new Testimonial();


            testimonial.setCustomerName(
                    customerName.trim()
            );


            testimonial.setRating(
                    rating
            );


            testimonial.setMessage(
                    message.trim()
            );


            if (
                    status == null ||
                            status.isBlank()
            ) {

                testimonial.setStatus(
                        "PUBLISHED"
                );

            } else {

                testimonial.setStatus(
                        status.toUpperCase()
                );
            }


            // =================================================
            // IMAGE UPLOAD
            // =================================================

            if (
                    image != null &&
                            !image.isEmpty()
            ) {

                String imagePath =
                        saveTestimonialImage(
                                image
                        );

                testimonial.setCustomerImage(
                        imagePath
                );
            }


            // =================================================
            // SAVE TESTIMONIAL
            // =================================================

            Testimonial saved =
                    testimonialRepository.save(
                            testimonial
                    );


            System.out.println(
                    "TESTIMONIAL CREATED: ID = " +
                            saved.getId()
            );


            return ResponseEntity.ok(
                    saved
            );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to create testimonial: " +
                                    e.getMessage()
                    );
        }
    }


    // =========================================================
    // UPDATE TESTIMONIAL
    // =========================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> updateTestimonial(

            @PathVariable Long id,

            @RequestParam("customerName")
            String customerName,

            @RequestParam("rating")
            Integer rating,

            @RequestParam("message")
            String message,

            @RequestParam(value = "status", required = false)
            String status,

            @RequestParam(value = "image", required = false)
            MultipartFile image

    ) {

        try {

            return testimonialRepository.findById(id)

                    .map(testimonial -> {

                        try {

                            // =================================
                            // VALIDATION
                            // =================================

                            if (
                                    customerName == null ||
                                            customerName.isBlank()
                            ) {

                                return ResponseEntity.badRequest()
                                        .body(
                                                "Customer name is required."
                                        );
                            }


                            if (
                                    message == null ||
                                            message.isBlank()
                            ) {

                                return ResponseEntity.badRequest()
                                        .body(
                                                "Testimonial message is required."
                                        );
                            }


                            if (
                                    rating == null ||
                                            rating < 1 ||
                                            rating > 5
                            ) {

                                return ResponseEntity.badRequest()
                                        .body(
                                                "Rating must be between 1 and 5."
                                        );
                            }


                            // =================================
                            // UPDATE BASIC DATA
                            // =================================

                            testimonial.setCustomerName(
                                    customerName.trim()
                            );


                            testimonial.setRating(
                                    rating
                            );


                            testimonial.setMessage(
                                    message.trim()
                            );


                            if (
                                    status != null &&
                                            !status.isBlank()
                            ) {

                                testimonial.setStatus(
                                        status.toUpperCase()
                                );
                            }


                            // =================================
                            // NEW IMAGE
                            // =================================

                            if (
                                    image != null &&
                                            !image.isEmpty()
                            ) {

                                String oldImage =
                                        testimonial.getCustomerImage();


                                // Delete old image
                                deleteOldTestimonialImage(
                                        oldImage
                                );


                                // Save new image
                                String newImagePath =
                                        saveTestimonialImage(
                                                image
                                        );


                                testimonial.setCustomerImage(
                                        newImagePath
                                );
                            }


                            // =================================
                            // SAVE UPDATED TESTIMONIAL
                            // =================================

                            Testimonial saved =
                                    testimonialRepository.save(
                                            testimonial
                                    );


                            System.out.println(
                                    "TESTIMONIAL UPDATED: ID = " +
                                            saved.getId()
                            );


                            return ResponseEntity.ok(
                                    saved
                            );


                        } catch (Exception e) {

                            e.printStackTrace();

                            return ResponseEntity
                                    .internalServerError()
                                    .body(
                                            "Unable to update testimonial: " +
                                                    e.getMessage()
                                    );
                        }

                    })

                    .orElseGet(() ->
                            ResponseEntity
                                    .notFound()
                                    .build()
                    );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to update testimonial."
                    );
        }
    }


    // =========================================================
    // ADMIN - CHANGE STATUS
    // =========================================================

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<Testimonial> updateStatus(

            @PathVariable Long id,

            @RequestParam String status

    ) {

        return testimonialRepository.findById(id)

                .map(testimonial -> {

                    testimonial.setStatus(
                            status.toUpperCase()
                    );


                    return ResponseEntity.ok(
                            testimonialRepository.save(
                                    testimonial
                            )
                    );

                })

                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    // =========================================================
    // ADMIN - DELETE TESTIMONIAL
    // =========================================================

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteTestimonial(

            @PathVariable Long id

    ) {

        if (
                !testimonialRepository.existsById(id)
        ) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        testimonialRepository.deleteById(
                id
        );


        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // SAVE TESTIMONIAL IMAGE
    // =========================================================

    private String saveTestimonialImage(
            MultipartFile image
    ) throws IOException {

        // =====================================================
        // CHECK IMAGE
        // =====================================================

        if (
                image == null ||
                        image.isEmpty()
        ) {

            return null;
        }


        // =====================================================
        // MAX 5 MB
        // =====================================================

        long maxSize =
                5L * 1024L * 1024L;


        if (
                image.getSize() > maxSize
        ) {

            throw new IOException(
                    "Image size must be less than 5 MB."
            );
        }


        // =====================================================
        // CHECK CONTENT TYPE
        // =====================================================

        String contentType =
                image.getContentType();


        if (
                contentType == null ||
                        !contentType.startsWith("image/")
        ) {

            throw new IOException(
                    "Only image files are allowed."
            );
        }


        // =====================================================
        // GET CORRECT TESTIMONIAL DIRECTORY
        // =====================================================

        Path testimonialUploadDirectory =
                getTestimonialUploadDirectory();


        // =====================================================
        // PRINT DIRECTORY
        // =====================================================

        System.out.println(
                "===================================="
        );

        System.out.println(
                "TESTIMONIAL UPLOAD DIRECTORY:"
        );

        System.out.println(
                testimonialUploadDirectory
        );

        System.out.println(
                "DIRECTORY EXISTS BEFORE CREATE: " +
                        Files.exists(
                                testimonialUploadDirectory
                        )
        );


        // =====================================================
        // CREATE DIRECTORY
        // =====================================================

        Files.createDirectories(
                testimonialUploadDirectory
        );


        System.out.println(
                "DIRECTORY EXISTS AFTER CREATE: " +
                        Files.exists(
                                testimonialUploadDirectory
                        )
        );


        // =====================================================
        // GET ORIGINAL FILE NAME
        // =====================================================

        String originalFilename =
                image.getOriginalFilename();


        // =====================================================
        // GET EXTENSION
        // =====================================================

        String extension =
                "";


        if (
                originalFilename != null &&
                        originalFilename.contains(".")
        ) {

            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf(".")
                    ).toLowerCase();
        }


        // =====================================================
        // ALLOWED EXTENSIONS
        // =====================================================

        if (
                !extension.equals(".jpg") &&
                        !extension.equals(".jpeg") &&
                        !extension.equals(".png") &&
                        !extension.equals(".webp") &&
                        !extension.equals(".gif")
        ) {

            throw new IOException(
                    "Allowed image formats: JPG, JPEG, PNG, WEBP, GIF."
            );
        }


        // =====================================================
        // UNIQUE FILE NAME
        // =====================================================

        String filename =
                UUID.randomUUID() +
                        extension;


        // =====================================================
        // FINAL PHYSICAL FILE PATH
        // =====================================================

        Path targetPath =
                testimonialUploadDirectory.resolve(
                        filename
                );


        // =====================================================
        // SAVE FILE
        // =====================================================

        Files.copy(
                image.getInputStream(),
                targetPath,
                StandardCopyOption.REPLACE_EXISTING
        );


        // =====================================================
        // VERIFY FILE
        // =====================================================

        System.out.println(
                "TESTIMONIAL IMAGE SAVE PATH:"
        );

        System.out.println(
                targetPath.toAbsolutePath()
        );

        System.out.println(
                "FILE EXISTS: " +
                        Files.exists(targetPath)
        );

        System.out.println(
                "FILE SIZE: " +
                        Files.size(targetPath) +
                        " bytes"
        );

        System.out.println(
                "===================================="
        );


        // =====================================================
        // RETURN FRONTEND URL
        // =====================================================

        return "/uploads/testimonials/" +
                filename;
    }


    // =========================================================
    // DELETE OLD TESTIMONIAL IMAGE
    // =========================================================

    private void deleteOldTestimonialImage(
            String imagePath
    ) {

        try {

            if (
                    imagePath == null ||
                            imagePath.isBlank()
            ) {

                return;
            }


            // =================================================
            // ONLY TESTIMONIAL IMAGES
            // =================================================

            if (
                    !imagePath.startsWith(
                            "/uploads/testimonials/"
                    )
            ) {

                return;
            }


            // =================================================
            // GET FILE NAME
            // =================================================

            String filename =
                    imagePath.substring(
                            imagePath.lastIndexOf("/") + 1
                    );


            // =================================================
            // GET TESTIMONIAL DIRECTORY
            // =================================================

            Path testimonialUploadDirectory =
                    getTestimonialUploadDirectory();


            // =================================================
            // GET OLD FILE PATH
            // =================================================

            Path file =
                    testimonialUploadDirectory.resolve(
                            filename
                    );


            // =================================================
            // DELETE
            // =================================================

            boolean existed =
                    Files.exists(file);


            Files.deleteIfExists(
                    file
            );


            System.out.println(
                    "===================================="
            );

            System.out.println(
                    "OLD TESTIMONIAL IMAGE:"
            );

            System.out.println(
                    file.toAbsolutePath()
            );

            System.out.println(
                    "OLD FILE EXISTED: " +
                            existed
            );

            System.out.println(
                    "OLD FILE EXISTS AFTER DELETE: " +
                            Files.exists(file)
            );

            System.out.println(
                    "===================================="
            );


        } catch (Exception e) {

            /*
             * Do not fail testimonial update
             * only because old image deletion failed.
             */

            System.err.println(
                    "Unable to delete old testimonial image: " +
                            e.getMessage()
            );
        }
    }

}