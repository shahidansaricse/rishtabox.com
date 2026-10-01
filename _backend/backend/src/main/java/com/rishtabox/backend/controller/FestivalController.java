package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.repository.FestivalRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivals")
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class FestivalController {

    private final FestivalRepository festivalRepository;

    public FestivalController(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }

    // =========================
    // GET ALL FESTIVALS
    // =========================

    @GetMapping
    public List<Festival> getAllFestivals() {
        return festivalRepository.findAll();
    }

    // =========================
    // GET FESTIVAL BY ID
    // =========================

    @GetMapping("/{id}")
    public Festival getFestivalById(@PathVariable String id) {

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Festival not found: " + id
                        ));
    }

    // =========================
    // CREATE FESTIVAL
    // =========================

    @PostMapping
    public Festival createFestival(
            @RequestBody Festival festival) {

        return festivalRepository.save(festival);
    }

    // =========================
    // UPDATE FESTIVAL
    // =========================

    @PutMapping("/{id}")
    public Festival updateFestival(
            @PathVariable String id,
            @RequestBody Festival updatedFestival) {

        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Festival not found: " + id
                        ));

        festival.setName(updatedFestival.getName());
        festival.setImage(updatedFestival.getImage());
        festival.setDescription(
                updatedFestival.getDescription()
        );
        festival.setPinned(
                updatedFestival.isPinned()
        );

        return festivalRepository.save(festival);
    }

    // =========================
    // PIN / UNPIN FESTIVAL
    // =========================

    @PutMapping("/{id}/pin")
    public Festival toggleFestivalPin(
            @PathVariable String id,
            @RequestParam boolean pinned) {

        Festival festival = festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Festival not found: " + id
                        ));

        festival.setPinned(pinned);

        return festivalRepository.save(festival);
    }
}