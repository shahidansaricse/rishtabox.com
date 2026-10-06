 package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Slider;
import com.rishtabox.backend.service.SliderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sliders")
@CrossOrigin
public class SliderController {

    private final SliderService sliderService;


    public SliderController(
            SliderService sliderService
    ) {

        this.sliderService =
                sliderService;
    }


    // =====================================================
    // GET ALL SLIDERS
    // ADMIN
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Slider>> getAllSliders() {

        return ResponseEntity.ok(
                sliderService.getAllSliders()
        );
    }


    // =====================================================
    // GET ACTIVE SLIDERS
    // FRONTEND
    // =====================================================

    @GetMapping("/active")
    public ResponseEntity<List<Slider>> getActiveSliders() {

        return ResponseEntity.ok(
                sliderService.getActiveSliders()
        );
    }


    // =====================================================
    // GET SLIDER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Slider> getSlider(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                sliderService.getSliderById(id)
        );
    }


    // =====================================================
    // CREATE SLIDER
    // =====================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Slider> createSlider(

            // TITLE OPTIONAL
            @RequestParam(
                    value = "title",
                    required = false
            )
            String title,


            // SUBTITLE OPTIONAL
            @RequestParam(
                    value = "subtitle",
                    required = false
            )
            String subtitle,


            // IMAGE REQUIRED FOR CREATE
            @RequestParam("image")
            MultipartFile image,


            // LINK OPTIONAL
            @RequestParam(
                    value = "link",
                    required = false
            )
            String link,


            // DISPLAY ORDER OPTIONAL
            @RequestParam(
                    value = "displayOrder",
                    required = false
            )
            Integer displayOrder,


            // ACTIVE OPTIONAL
            @RequestParam(
                    value = "active",
                    required = false
            )
            Boolean active

    ) throws IOException {


        Slider slider =
                sliderService.createSlider(

                        title,
                        subtitle,
                        image,
                        link,
                        displayOrder,
                        active

                );


        return ResponseEntity.ok(
                slider
        );
    }


    // =====================================================
    // UPDATE SLIDER
    // =====================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Slider> updateSlider(

            @PathVariable Long id,


            // TITLE OPTIONAL
            @RequestParam(
                    value = "title",
                    required = false
            )
            String title,


            // SUBTITLE OPTIONAL
            @RequestParam(
                    value = "subtitle",
                    required = false
            )
            String subtitle,


            // IMAGE OPTIONAL DURING UPDATE
            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,


            // LINK OPTIONAL
            @RequestParam(
                    value = "link",
                    required = false
            )
            String link,


            // DISPLAY ORDER OPTIONAL
            @RequestParam(
                    value = "displayOrder",
                    required = false
            )
            Integer displayOrder,


            // ACTIVE OPTIONAL
            @RequestParam(
                    value = "active",
                    required = false
            )
            Boolean active

    ) throws IOException {


        Slider slider =
                sliderService.updateSlider(

                        id,
                        title,
                        subtitle,
                        image,
                        link,
                        displayOrder,
                        active

                );


        return ResponseEntity.ok(
                slider
        );
    }


    // =====================================================
    // UPDATE ACTIVE STATUS
    // =====================================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<Slider> updateStatus(

            @PathVariable Long id,

            @RequestParam("active")
            Boolean active

    ) {

        return ResponseEntity.ok(
                sliderService.updateStatus(
                        id,
                        active
                )
        );
    }


    // =====================================================
    // DELETE SLIDER
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlider(

            @PathVariable Long id

    ) {

        sliderService.deleteSlider(
                id
        );


        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Slider deleted successfully"
                )
        );
    }

}

