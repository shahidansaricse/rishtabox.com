 package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.RelationshipRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relationships")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class RelationshipController {

    private final RelationshipRepository relationshipRepository;

    public RelationshipController(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    // GET ALL RELATIONSHIPS
    @GetMapping
    public List<Relationship> getAllRelationships() {
        return relationshipRepository.findAll();
    }

    // CREATE RELATIONSHIP
    @PostMapping
    public Relationship createRelationship(
            @RequestBody Relationship relationship) {

        return relationshipRepository.save(relationship);
    }

    // GET RELATIONSHIP BY STRING ID
    @GetMapping("/{id}")
    public Relationship getRelationshipById(
            @PathVariable String id) {

        return relationshipRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Relationship not found"));
    }
}

