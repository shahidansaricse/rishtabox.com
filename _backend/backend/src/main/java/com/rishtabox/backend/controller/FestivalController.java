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

    @GetMapping
    public List<Festival> getAllFestivals() {
        return festivalRepository.findAll();
    }

    @GetMapping("/{id}")
    public Festival getFestivalById(@PathVariable String id) {

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Festival not found: " + id));
    }

    @PostMapping
    public Festival createFestival(@RequestBody Festival festival) {
        return festivalRepository.save(festival);
    }
}