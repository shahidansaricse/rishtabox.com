package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository
        extends JpaRepository<Festival, String> {
}