package com.rishtabox.backend.repository;

import com.rishtabox.backend.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    List<Blog> findAllByOrderByCreatedAtDesc();

    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();
}