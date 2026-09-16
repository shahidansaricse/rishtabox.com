package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
}