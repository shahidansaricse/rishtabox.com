package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Slider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SliderRepository
        extends JpaRepository<Slider, Long> {

    List<Slider> findAllByOrderByDisplayOrderAsc();

    List<Slider> findByActiveTrueOrderByDisplayOrderAsc();
}