package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.RelationshipRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/relationships")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRelationshipController {

    private final RelationshipRepository relationshipRepository;

    public AdminRelationshipController(
            RelationshipRepository relationshipRepository) {

        this.relationshipRepository = relationshipRepository;
    }

    // =========================
    // GET ALL RELATIONSHIPS
    // =========================

    @GetMapping
    public ResponseEntity<List<Relationship>> getAllRelationships() {

        return ResponseEntity.ok(
                relationshipRepository.findAll()
        );
    }

    // =========================
    // GET RELATIONSHIP BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<?> getRelationship(
            @PathVariable String id) {

        return relationshipRepository.findById(id)
                .<ResponseEntity<?>>map(
                        ResponseEntity::ok
                )
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(
                                        Map.of(
                                                "message",
                                                "Relationship not found"
                                        )
                                )
                );
    }

    // =========================
    // CREATE RELATIONSHIP
    // =========================

    @PostMapping
    public ResponseEntity<?> createRelationship(
            @RequestBody Relationship relationship) {

        try {

            Relationship savedRelationship =
                    relationshipRepository.save(relationship);

            return ResponseEntity.ok(
                    savedRelationship
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================
    // UPDATE RELATIONSHIP
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRelationship(
            @PathVariable String id,
            @RequestBody Relationship relationship) {

        try {

            Relationship existingRelationship =
                    relationshipRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Relationship not found"
                                    )
                            );

            // Update fields
            existingRelationship.setName(
                    relationship.getName()
            );

            Relationship updatedRelationship =
                    relationshipRepository.save(
                            existingRelationship
                    );

            return ResponseEntity.ok(
                    updatedRelationship
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    // =========================
    // DELETE RELATIONSHIP
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelationship(
            @PathVariable String id) {

        try {

            if (!relationshipRepository.existsById(id)) {

                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Relationship not found"
                                )
                        );
            }

            relationshipRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Relationship deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}