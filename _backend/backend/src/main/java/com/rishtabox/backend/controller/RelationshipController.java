package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.RelationshipRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relationships")
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class RelationshipController {

    private final RelationshipRepository relationshipRepository;


    @Value("${rishtabox.relationship.upload-dir:./uploads/relationships}")
    private String uploadDir;


    public RelationshipController(
            RelationshipRepository relationshipRepository
    ) {

        this.relationshipRepository =
                relationshipRepository;
    }


    // =========================================================
    // GET ALL RELATIONSHIPS
    // =========================================================

    @GetMapping
    public List<Relationship> getAllRelationships() {

        return relationshipRepository.findAll();
    }


    // =========================================================
    // CREATE RELATIONSHIP
    // =========================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Relationship> createRelationship(

            @RequestParam("name")
            String name,

            @RequestParam(
                    value = "description",
                    required = false
            )
            String description,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            @RequestParam(
                    value = "pinned",
                    required = false
            )
            Boolean pinned

    ) throws IOException {


        if (
                name == null ||
                        name.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Relationship name is required."
            );
        }


        name = name.trim();


        String imagePath = null;


        if (
                image != null &&
                        !image.isEmpty()
        ) {

            validateImage(image);

            imagePath =
                    saveImage(image);
        }


        Relationship relationship =
                new Relationship();


        relationship.setName(
                name
        );


        relationship.setDescription(
                cleanOptionalValue(
                        description
                )
        );


        relationship.setImage(
                imagePath
        );


        relationship.setPinned(
                pinned != null
                        ? pinned
                        : false
        );


        return ResponseEntity.ok(
                relationshipRepository.save(
                        relationship
                )
        );
    }


    // =========================================================
    // GET RELATIONSHIP BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Relationship getRelationshipById(

            @PathVariable String id

    ) {

        return relationshipRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Relationship not found: " + id
                        )
                );
    }


    // =========================================================
    // UPDATE RELATIONSHIP
    // =========================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Relationship> updateRelationship(

            @PathVariable String id,

            @RequestParam("name")
            String name,

            @RequestParam(
                    value = "description",
                    required = false
            )
            String description,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            @RequestParam(
                    value = "pinned",
                    required = false
            )
            Boolean pinned

    ) throws IOException {


        Relationship relationship =
                relationshipRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Relationship not found: " + id
                                )
                        );


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (
                name != null &&
                        !name.trim().isEmpty()
        ) {

            relationship.setName(
                    name.trim()
            );
        }


        // -------------------------------------------------
        // DESCRIPTION
        // -------------------------------------------------

        relationship.setDescription(
                cleanOptionalValue(
                        description
                )
        );


        // -------------------------------------------------
        // PINNED
        // -------------------------------------------------

        if (pinned != null) {

            relationship.setPinned(
                    pinned
            );
        }


        // -------------------------------------------------
        // IMAGE UPDATE
        // -------------------------------------------------

        /*
         * No new image:
         * old image remains.
         */

        if (
                image != null &&
                        !image.isEmpty()
        ) {

            validateImage(image);


            String oldImage =
                    relationship.getImage();


            String newImage =
                    saveImage(image);


            relationship.setImage(
                    newImage
            );


            deleteOldImage(
                    oldImage
            );
        }


        return ResponseEntity.ok(
                relationshipRepository.save(
                        relationship
                )
        );
    }


    // =========================================================
    // PIN / UNPIN RELATIONSHIP
    // =========================================================

    @PutMapping("/{id}/pin")
    public Relationship toggleRelationshipPin(

            @PathVariable String id,

            @RequestParam boolean pinned

    ) {

        Relationship relationship =
                relationshipRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Relationship not found: " + id
                                )
                        );


        relationship.setPinned(
                pinned
        );


        return relationshipRepository.save(
                relationship
        );
    }


    // =========================================================
    // DELETE RELATIONSHIP
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteRelationship(

            @PathVariable String id

    ) {

        Relationship relationship =
                relationshipRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Relationship not found: " + id
                                )
                        );


        deleteOldImage(
                relationship.getImage()
        );


        relationshipRepository.delete(
                relationship
        );


        return "Relationship deleted successfully";
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

        long maxSize =
                5 * 1024 * 1024;


        if (
                image.getSize() > maxSize
        ) {

            throw new IllegalArgumentException(
                    "Image size must be less than 5 MB."
            );
        }


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


    // =========================================================
    // SAVE IMAGE
    // =========================================================

    private String saveImage(
            MultipartFile image
    ) throws IOException {


        Path directory =
                Paths.get(
                        uploadDir
                );


        Files.createDirectories(
                directory
        );


        String extension =
                getExtension(
                        image.getOriginalFilename()
                );


        String fileName =
                UUID.randomUUID()
                        .toString()
                        + extension;


        Path filePath =
                directory.resolve(
                        fileName
                );


        Files.copy(
                image.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        return "/uploads/relationships/"
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

            System.err.println(
                    "Could not delete old relationship image: "
                            + e.getMessage()
            );
        }
    }

}