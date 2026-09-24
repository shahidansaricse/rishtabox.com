package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.repository.FestivalRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/festivals")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFestivalController {

    private final FestivalRepository festivalRepository;

    public AdminFestivalController(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    // =========================
    // GET ALL FESTIVALS
    // =========================
    @GetMapping
    public ResponseEntity<List<Festival>> getAllFestivals() {
        return ResponseEntity.ok(
                festivalRepository.findAll()
        );
    }

    // =========================
    // GET FESTIVAL BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<?> getFestival(
            @PathVariable String id) {

        return festivalRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Festival not found"
                                ))
                );
    }

    // =========================
    // CREATE FESTIVAL
    // =========================
    @PostMapping
    public ResponseEntity<?> createFestival(
            @RequestBody Festival festival) {

        try {

            if (festival.getId() == null ||
                    festival.getId().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Festival ID is required"
                        ));
            }

            if (festival.getName() == null ||
                    festival.getName().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Festival name is required"
                        ));
            }

            Festival savedFestival =
                    festivalRepository.save(festival);

            return ResponseEntity.ok(savedFestival);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================
    // UPDATE FESTIVAL
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFestival(
            @PathVariable String id,
            @RequestBody Festival festival) {

        try {

            Festival existingFestival =
                    festivalRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Festival not found"
                                    )
                            );

            if (festival.getName() == null ||
                    festival.getName().isBlank()) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Festival name is required"
                        ));
            }

            existingFestival.setName(
                    festival.getName().trim()
            );

            Festival updatedFestival =
                    festivalRepository.save(existingFestival);

            return ResponseEntity.ok(updatedFestival);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // =========================
    // DELETE FESTIVAL
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFestival(
            @PathVariable String id) {

        try {

            if (!festivalRepository.existsById(id)) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Festival not found"
                        ));
            }

            festivalRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Festival deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}