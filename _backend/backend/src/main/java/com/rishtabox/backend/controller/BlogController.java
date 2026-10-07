package com.rishtabox.backend.controller;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.service.BlogService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin
public class BlogController {

    private final BlogService blogService;

    public BlogController(
            BlogService blogService
    ) {
        this.blogService = blogService;
    }


    // =====================================================
    // GET PUBLISHED BLOGS
    // FRONTEND
    // =====================================================

    @GetMapping
    public ResponseEntity<List<Blog>> getPublishedBlogs() {

        return ResponseEntity.ok(
                blogService.getPublishedBlogs()
        );
    }


    // =====================================================
    // GET ALL BLOGS
    // ADMIN
    // =====================================================

    @GetMapping("/all")
    public ResponseEntity<List<Blog>> getAllBlogs() {

        return ResponseEntity.ok(
                blogService.getAllBlogs()
        );
    }


    // =====================================================
    // GET BLOG BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(
            @PathVariable Long id
    ) {

        try {

            return ResponseEntity.ok(
                    blogService.getBlogById(id)
            );

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // CREATE BLOG
    // IMAGE REQUIRED
    // =====================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> createBlog(

            @RequestParam("title")
            String title,

            @RequestParam("description")
            String description,

            @RequestParam("content")
            String content,

            @RequestParam("image")
            MultipartFile image,

            @RequestParam("author")
            String author,

            @RequestParam(
                    value = "published",
                    required = false
            )
            Boolean published

    ) throws IOException {

        try {

            Blog savedBlog =
                    blogService.createBlog(
                            title,
                            description,
                            content,
                            image,
                            author,
                            published
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBlog);

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // UPDATE BLOG
    // IMAGE OPTIONAL
    // =====================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> updateBlog(

            @PathVariable Long id,

            @RequestParam("title")
            String title,

            @RequestParam("description")
            String description,

            @RequestParam("content")
            String content,

            @RequestParam(
                    value = "image",
                    required = false
            )
            MultipartFile image,

            @RequestParam("author")
            String author,

            @RequestParam(
                    value = "published",
                    required = false
            )
            Boolean published

    ) throws IOException {

        try {

            Blog updatedBlog =
                    blogService.updateBlog(
                            id,
                            title,
                            description,
                            content,
                            image,
                            author,
                            published
                    );

            return ResponseEntity.ok(
                    updatedBlog
            );

        } catch (RuntimeException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // DELETE BLOG
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(
            @PathVariable Long id
    ) {

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
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }
}