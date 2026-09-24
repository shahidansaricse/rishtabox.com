 package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Relationship;
import com.rishtabox.backend.repository.RelationshipRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminRelationshipService {

    private final RelationshipRepository relationshipRepository;

    public AdminRelationshipService(
            RelationshipRepository relationshipRepository) {

        this.relationshipRepository =
                relationshipRepository;
    }

    // =========================
    // GET ALL
    // =========================

    public List<Relationship> getAllRelationships() {

        return relationshipRepository.findAll();
    }

    // =========================
    // GET BY ID
    // =========================

    public Relationship getRelationship(String id) {

        if (id == null || id.isBlank()) {

            throw new RuntimeException(
                    "Relationship ID is required"
            );
        }

        return relationshipRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Relationship not found with id: "
                                        + id
                        )
                );
    }

    // =========================
    // CREATE
    // =========================

    @Transactional
    public Relationship createRelationship(
            Relationship relationship) {

        if (relationship == null) {

            throw new RuntimeException(
                    "Relationship data is required"
            );
        }

        if (relationship.getId() == null ||
                relationship.getId().isBlank()) {

            throw new RuntimeException(
                    "Relationship ID is required"
            );
        }

        if (relationship.getName() == null ||
                relationship.getName().isBlank()) {

            throw new RuntimeException(
                    "Relationship name is required"
            );
        }

        return relationshipRepository.save(
                relationship
        );
    }

    // =========================
    // UPDATE
    // =========================

    @Transactional
    public Relationship updateRelationship(
            String id,
            Relationship relationship) {

        if (id == null || id.isBlank()) {

            throw new RuntimeException(
                    "Relationship ID is required"
            );
        }

        if (relationship == null) {

            throw new RuntimeException(
                    "Relationship data is required"
            );
        }

        Relationship existingRelationship =
                getRelationship(id);

        BeanUtils.copyProperties(
                relationship,
                existingRelationship,
                "id"
        );

        return relationshipRepository.save(
                existingRelationship
        );
    }

    // =========================
    // DELETE
    // =========================

    @Transactional
    public void deleteRelationship(String id) {

        if (id == null || id.isBlank()) {

            throw new RuntimeException(
                    "Relationship ID is required"
            );
        }

        if (!relationshipRepository.existsById(id)) {

            throw new RuntimeException(
                    "Relationship not found with id: "
                            + id
            );
        }

        relationshipRepository.deleteById(id);
    }
}

