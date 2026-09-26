package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonialRepository
        extends JpaRepository<Testimonial, Long> {

    List<Testimonial> findByStatusIgnoreCase(String status);
}