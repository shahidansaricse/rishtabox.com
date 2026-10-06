package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Slider;
import com.rishtabox.backend.repository.SliderRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class SliderService {

    private final SliderRepository sliderRepository;


    // =====================================================
    // UPLOAD DIRECTORY
    // =====================================================

    @Value("${rishtabox.slider.upload-dir:./uploads/sliders}")
    private String uploadDir;


    public SliderService(
            SliderRepository sliderRepository
    ) {

        this.sliderRepository =
                sliderRepository;
    }


    // =====================================================
    // GET ALL SLIDERS
    // =====================================================

    public List<Slider> getAllSliders() {

        return sliderRepository
                .findAllByOrderByDisplayOrderAsc();
    }


    // =====================================================
    // GET ACTIVE SLIDERS
    // =====================================================

    public List<Slider> getActiveSliders() {

        return sliderRepository
                .findByActiveTrueOrderByDisplayOrderAsc();
    }


    // =====================================================
    // GET SLIDER BY ID
    // =====================================================

    public Slider getSliderById(
            Long id
    ) {

        return sliderRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Slider not found with id: " + id
                        )
                );
    }


    // =====================================================
    // CREATE SLIDER
    // =====================================================

    public Slider createSlider(

            String title,

            String subtitle,

            MultipartFile image,

            String link,

            Integer displayOrder,

            Boolean active

    ) throws IOException {


        // -------------------------------------------------
        // IMAGE REQUIRED
        // -------------------------------------------------

        if (
                image == null ||
                        image.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Slider image is required."
            );
        }


        // -------------------------------------------------
        // VALIDATE IMAGE
        // -------------------------------------------------

        validateImage(image);


        // -------------------------------------------------
        // SAVE IMAGE
        // -------------------------------------------------

        String imagePath =
                saveImage(image);


        // -------------------------------------------------
        // CREATE SLIDER
        // -------------------------------------------------

        Slider slider =
                new Slider();


        // Title optional
        slider.setTitle(
                cleanOptionalValue(title)
        );


        // Subtitle optional
        slider.setSubtitle(
                cleanOptionalValue(subtitle)
        );


        // Image required
        slider.setImage(
                imagePath
        );


        // Link optional
        slider.setLink(
                cleanOptionalValue(link)
        );


        // Display order
        slider.setDisplayOrder(
                displayOrder != null
                        ? displayOrder
                        : 0
        );


        // Active
        slider.setActive(
                active != null
                        ? active
                        : true
        );


        return sliderRepository.save(
                slider
        );
    }


    // =====================================================
    // UPDATE SLIDER
    // =====================================================

    public Slider updateSlider(

            Long id,

            String title,

            String subtitle,

            MultipartFile image,

            String link,

            Integer displayOrder,

            Boolean active

    ) throws IOException {


        Slider slider =
                getSliderById(id);


        // -------------------------------------------------
        // OPTIONAL TEXT FIELDS
        // -------------------------------------------------

        slider.setTitle(
                cleanOptionalValue(title)
        );


        slider.setSubtitle(
                cleanOptionalValue(subtitle)
        );


        slider.setLink(
                cleanOptionalValue(link)
        );


        // -------------------------------------------------
        // DISPLAY ORDER
        // -------------------------------------------------

        if (displayOrder != null) {

            slider.setDisplayOrder(
                    displayOrder
            );
        }


        // -------------------------------------------------
        // ACTIVE STATUS
        // -------------------------------------------------

        if (active != null) {

            slider.setActive(
                    active
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
                    slider.getImage();


            String newImage =
                    saveImage(image);


            slider.setImage(
                    newImage
            );


            deleteOldImage(
                    oldImage
            );
        }


        return sliderRepository.save(
                slider
        );
    }


    // =====================================================
    // UPDATE ONLY STATUS
    // =====================================================

    public Slider updateStatus(

            Long id,

            Boolean active

    ) {

        Slider slider =
                getSliderById(id);


        slider.setActive(
                active
        );


        return sliderRepository.save(
                slider
        );
    }


    // =====================================================
    // DELETE SLIDER
    // =====================================================

    public void deleteSlider(
            Long id
    ) {

        Slider slider =
                getSliderById(id);


        deleteOldImage(
                slider.getImage()
        );


        sliderRepository.delete(
                slider
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

        return "/uploads/sliders/"
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
                    "Could not delete old slider image: "
                            + e.getMessage()
            );
        }
    }

}

