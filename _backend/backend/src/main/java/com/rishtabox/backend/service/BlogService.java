package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.repository.BlogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    // =========================================================
    // GET PUBLISHED BLOGS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Blog> getPublishedBlogs() {

        return blogRepository
                .findByPublishedTrueOrderByCreatedAtDesc();
    }

    // =========================================================
    // GET ALL BLOGS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Blog> getAllBlogs() {

        return blogRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // =========================================================
    // GET BLOG BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public Blog getBlogById(Long id) {

        return blogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Blog not found"
                        )
                );
    }

    // =========================================================
    // CREATE BLOG
    // =========================================================

    @Transactional
    public Blog createBlog(Blog blog) {

        validateBlog(blog);

        blog.setId(null);

        return blogRepository.save(blog);
    }

    // =========================================================
    // UPDATE BLOG
    // =========================================================

    @Transactional
    public Blog updateBlog(Long id, Blog updatedBlog) {

        validateBlog(updatedBlog);

        Blog existingBlog = getBlogById(id);

        existingBlog.setTitle(updatedBlog.getTitle());
        existingBlog.setDescription(updatedBlog.getDescription());
        existingBlog.setContent(updatedBlog.getContent());
        existingBlog.setImage(updatedBlog.getImage());
        existingBlog.setAuthor(updatedBlog.getAuthor());
        existingBlog.setPublished(updatedBlog.getPublished());

        return blogRepository.save(existingBlog);
    }

    // =========================================================
    // DELETE BLOG
    // =========================================================

    @Transactional
    public void deleteBlog(Long id) {

        if (!blogRepository.existsById(id)) {

            throw new RuntimeException(
                    "Blog not found"
            );
        }

        blogRepository.deleteById(id);
    }

    // =========================================================
    // VALIDATE BLOG
    // =========================================================

    private void validateBlog(Blog blog) {

        if (blog == null) {
            throw new RuntimeException(
                    "Blog data is required"
            );
        }

        if (blog.getTitle() == null
                || blog.getTitle().trim().isEmpty()) {

            throw new RuntimeException(
                    "Blog title is required"
            );
        }

        if (blog.getDescription() == null
                || blog.getDescription().trim().isEmpty()) {

            throw new RuntimeException(
                    "Blog description is required"
            );
        }

        if (blog.getContent() == null
                || blog.getContent().trim().isEmpty()) {

            throw new RuntimeException(
                    "Blog content is required"
            );
        }

        if (blog.getImage() == null
                || blog.getImage().trim().isEmpty()) {

            throw new RuntimeException(
                    "Blog image is required"
            );
        }

        if (blog.getAuthor() == null
                || blog.getAuthor().trim().isEmpty()) {

            throw new RuntimeException(
                    "Blog author is required"
            );
        }

        if (blog.getPublished() == null) {
            blog.setPublished(true);
        }
    }
}