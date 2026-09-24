package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Festival;
import com.rishtabox.backend.repository.FestivalRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminFestivalService {

    private final FestivalRepository festivalRepository;

    public AdminFestivalService(
            FestivalRepository festivalRepository) {

        this.festivalRepository = festivalRepository;
    }

    // =========================
    // GET ALL FESTIVALS
    // =========================
    public List<Festival> getAllFestivals() {

        return festivalRepository.findAll();
    }

    // =========================
    // GET FESTIVAL BY ID
    // Festival ID = String
    // =========================
    public Festival getFestival(String id) {

        if (id == null || id.isBlank()) {
            throw new RuntimeException(
                    "Festival ID is required"
            );
        }

        return festivalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Festival not found with id: "
                                        + id
                        )
                );
    }

    // =========================
    // CREATE FESTIVAL
    // =========================
    @Transactional
    public Festival createFestival(
            Festival festival) {

        if (festival == null) {
            throw new RuntimeException(
                    "Festival data is required"
            );
        }

        if (festival.getId() == null ||
                festival.getId().isBlank()) {

            throw new RuntimeException(
                    "Festival ID is required"
            );
        }

        if (festival.getName() == null ||
                festival.getName().isBlank()) {

            throw new RuntimeException(
                    "Festival name is required"
            );
        }

        festival.setId(
                festival.getId().trim()
        );

        festival.setName(
                festival.getName().trim()
        );

        return festivalRepository.save(festival);
    }

    // =========================
    // UPDATE FESTIVAL
    // Festival ID = String
    // =========================
    @Transactional
    public Festival updateFestival(
            String id,
            Festival festival) {

        if (festival == null) {
            throw new RuntimeException(
                    "Festival data is required"
            );
        }

        Festival existingFestival =
                getFestival(id);

        if (festival.getName() == null ||
                festival.getName().isBlank()) {

            throw new RuntimeException(
                    "Festival name is required"
            );
        }

        BeanUtils.copyProperties(
                festival,
                existingFestival,
                "id"
        );

        existingFestival.setName(
                existingFestival.getName().trim()
        );

        return festivalRepository.save(
                existingFestival
        );
    }

    // =========================
    // DELETE FESTIVAL
    // Festival ID = String
    // =========================
    @Transactional
    public void deleteFestival(String id) {

        if (id == null || id.isBlank()) {
            throw new RuntimeException(
                    "Festival ID is required"
            );
        }

        if (!festivalRepository.existsById(id)) {

            throw new RuntimeException(
                    "Festival not found with id: " + id
            );
        }

        festivalRepository.deleteById(id);
    }
}