package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.repository.FestivalRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class FestivalController {

    private final FestivalRepository festivalRepository;


    @Value("${rishtabox.festival.upload-dir:./uploads/festivals}")
    private String uploadDir;


    public FestivalController(
            FestivalRepository festivalRepository
    ) {

        this.festivalRepository =
                festivalRepository;
    }


    // =========================================================
    // GET ALL FESTIVALS
    // =========================================================

    @GetMapping
    public List<Festival> getAllFestivals() {

        return festivalRepository.findAll();
    }


    // =========================================================
    // GET FESTIVAL BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Festival getFestivalById(
            @PathVariable String id
    ) {

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Festival not found: " + id
                        )
                );
    }


    // =========================================================
    // CREATE FESTIVAL
    // =========================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Festival> createFestival(

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
                    "Festival name is required."
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


        Festival festival =
                new Festival();


        festival.setName(
                name
        );


        festival.setDescription(
                cleanOptionalValue(
                        description
                )
        );


        festival.setImage(
                imagePath
        );


        festival.setPinned(
                pinned != null
                        ? pinned
                        : false
        );


        return ResponseEntity.ok(
                festivalRepository.save(
                        festival
                )
        );
    }


    // =========================================================
    // UPDATE FESTIVAL
    // =========================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Festival> updateFestival(

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


        Festival festival =
                festivalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Festival not found: " + id
                                )
                        );


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        if (
                name != null &&
                        !name.trim().isEmpty()
        ) {

            festival.setName(
                    name.trim()
            );
        }


        // -------------------------------------------------
        // DESCRIPTION
        // -------------------------------------------------

        festival.setDescription(
                cleanOptionalValue(
                        description
                )
        );


        // -------------------------------------------------
        // PINNED
        // -------------------------------------------------

        if (pinned != null) {

            festival.setPinned(
                    pinned
            );
        }


        // -------------------------------------------------
        // IMAGE
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
                    festival.getImage();


            String newImage =
                    saveImage(image);


            festival.setImage(
                    newImage
            );


            deleteOldImage(
                    oldImage
            );
        }


        return ResponseEntity.ok(
                festivalRepository.save(
                        festival
                )
        );
    }


    // =========================================================
    // PIN / UNPIN FESTIVAL
    // =========================================================

    @PutMapping("/{id}/pin")
    public Festival toggleFestivalPin(

            @PathVariable String id,

            @RequestParam boolean pinned

    ) {

        Festival festival =
                festivalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Festival not found: " + id
                                )
                        );


        festival.setPinned(
                pinned
        );


        return festivalRepository.save(
                festival
        );
    }


    // =========================================================
    // DELETE FESTIVAL
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteFestival(

            @PathVariable String id

    ) {

        Festival festival =
                festivalRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Festival not found: " + id
                                )
                        );


        deleteOldImage(
                festival.getImage()
        );


        festivalRepository.delete(
                festival
        );


        return "Festival deleted successfully";
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


        return "/uploads/festivals/"
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
                    "Could not delete old festival image: "
                            + e.getMessage()
            );
        }
    }

}