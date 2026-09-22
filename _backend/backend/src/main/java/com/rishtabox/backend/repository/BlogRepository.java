package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Get published blogs, newest first
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();

    // Get all blogs, newest first
    List<Blog> findAllByOrderByCreatedAtDesc();
}