package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.RelationshipRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class RelationshipController {

    private final RelationshipRepository relationshipRepository;

    public RelationshipController(
            RelationshipRepository relationshipRepository) {

        this.relationshipRepository = relationshipRepository;
    }

    // =========================
    // GET ALL RELATIONSHIPS
    // =========================

    @GetMapping
    public List<Relationship> getAllRelationships() {
        return relationshipRepository.findAll();
    }

    // =========================
    // CREATE RELATIONSHIP
    // =========================

    @PostMapping
    public Relationship createRelationship(
            @RequestBody Relationship relationship) {

        return relationshipRepository.save(relationship);
    }

    // =========================
    // GET RELATIONSHIP BY ID
    // =========================

    @GetMapping("/{id}")
    public Relationship getRelationshipById(
            @PathVariable String id) {

        return relationshipRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Relationship not found: " + id
                        ));
    }

    // =========================
    // UPDATE RELATIONSHIP
    // =========================

    @PutMapping("/{id}")
    public Relationship updateRelationship(
            @PathVariable String id,
            @RequestBody Relationship updatedRelationship) {

        Relationship relationship =
                relationshipRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Relationship not found: " + id
                                ));

        relationship.setName(
                updatedRelationship.getName()
        );

        relationship.setImage(
                updatedRelationship.getImage()
        );

        relationship.setDescription(
                updatedRelationship.getDescription()
        );

        relationship.setPinned(
                updatedRelationship.isPinned()
        );

        return relationshipRepository.save(relationship);
    }

    // =========================
    // PIN / UNPIN RELATIONSHIP
    // =========================

    @PutMapping("/{id}/pin")
    public Relationship toggleRelationshipPin(
            @PathVariable String id,
            @RequestParam boolean pinned) {

        Relationship relationship =
                relationshipRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Relationship not found: " + id
                                ));

        relationship.setPinned(pinned);

        return relationshipRepository.save(relationship);
    }
}