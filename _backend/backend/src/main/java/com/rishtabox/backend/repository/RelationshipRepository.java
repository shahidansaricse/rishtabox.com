package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationshipRepository
        extends JpaRepository<Relationship, String> {
}