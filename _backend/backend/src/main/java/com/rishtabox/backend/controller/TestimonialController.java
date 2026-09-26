package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Testimonial;
import com.rishtabox.backend.repository.TestimonialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;

    public TestimonialController(TestimonialRepository testimonialRepository) {
        this.testimonialRepository = testimonialRepository;
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
                testimonialRepository.findByStatusIgnoreCase("PUBLISHED")
        );
    }


    // =========================================================
    // CREATE TESTIMONIAL
    // =========================================================

    @PostMapping
    public ResponseEntity<Testimonial> createTestimonial(
            @RequestBody Testimonial testimonial
    ) {

        if (testimonial.getStatus() == null ||
                testimonial.getStatus().isBlank()) {

            testimonial.setStatus("PUBLISHED");
        }

        return ResponseEntity.ok(
                testimonialRepository.save(testimonial)
        );
    }


    // =========================================================
    // UPDATE TESTIMONIAL
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Testimonial> updateTestimonial(
            @PathVariable Long id,
            @RequestBody Testimonial updated
    ) {

        return testimonialRepository.findById(id)
                .map(testimonial -> {

                    testimonial.setCustomerName(
                            updated.getCustomerName()
                    );

                    testimonial.setCustomerImage(
                            updated.getCustomerImage()
                    );

                    testimonial.setRating(
                            updated.getRating()
                    );

                    testimonial.setMessage(
                            updated.getMessage()
                    );

                    testimonial.setStatus(
                            updated.getStatus()
                    );

                    return ResponseEntity.ok(
                            testimonialRepository.save(testimonial)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
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
                            testimonialRepository.save(testimonial)
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================================
    // ADMIN - DELETE TESTIMONIAL
    // =========================================================

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteTestimonial(
            @PathVariable Long id
    ) {

        if (!testimonialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        testimonialRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}