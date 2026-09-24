package com.rishtabox.backend.controller.admin;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.repository.BlogRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/blogs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlogController {

    private final BlogRepository blogRepository;

    public AdminBlogController(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    // Get all blogs
    @GetMapping
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogRepository.findAll());
    }

    // Get blog
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlog(@PathVariable Long id) {

        return blogRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(Map.of(
                                        "message",
                                        "Blog not found"
                                ))
                );
    }

    // Create blog
    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody Blog blog) {

        try {

            blog.setId(null);

            return ResponseEntity.ok(
                    blogRepository.save(blog)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // Update blog
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(
            @PathVariable Long id,
            @RequestBody Blog blog) {

        try {

            Blog existingBlog = blogRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Blog not found")
                    );

            /*
             * Copy fields from the request object to existingBlog
             * according to your Blog entity fields.
             *
             * Example:
             *
             * existingBlog.setTitle(blog.getTitle());
             * existingBlog.setContent(blog.getContent());
             * existingBlog.setImage(blog.getImage());
             * existingBlog.setPublished(blog.isPublished());
             */

            return ResponseEntity.ok(
                    blogRepository.save(existingBlog)
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }

    // Delete blog
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(
            @PathVariable Long id) {

        try {

            if (!blogRepository.existsById(id)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Blog not found"
                        ));
            }

            blogRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Blog deleted successfully"
                    )
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()
                    ));
        }
    }
}