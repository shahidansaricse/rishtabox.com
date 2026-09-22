package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.service.BlogService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // =========================================================
    // GET PUBLISHED BLOGS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Blog>> getPublishedBlogs() {

        return ResponseEntity.ok(
                blogService.getPublishedBlogs()
        );
    }

    // =========================================================
    // GET ALL BLOGS
    // =========================================================

    @GetMapping("/all")
    public ResponseEntity<List<Blog>> getAllBlogs() {

        return ResponseEntity.ok(
                blogService.getAllBlogs()
        );
    }

    // =========================================================
    // GET BLOG BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(
            @PathVariable Long id) {

        try {

            return ResponseEntity.ok(
                    blogService.getBlogById(id)
            );

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    // =========================================================
    // CREATE BLOG
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody Blog blog) {

        try {

            Blog savedBlog = blogService.createBlog(blog);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBlog);

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    // =========================================================
    // UPDATE BLOG
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
            @PathVariable Long id,
            @RequestBody Blog blog) {

        try {

            Blog updatedBlog =
                    blogService.updateBlog(id, blog);

            return ResponseEntity.ok(updatedBlog);

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    // =========================================================
    // DELETE BLOG
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(
            @PathVariable Long id) {

        try {

            blogService.deleteBlog(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Blog deleted successfully"
                    )
            );

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }
}