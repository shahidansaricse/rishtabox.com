package com.rishtabox.backend.service.admin;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.repository.BlogRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminBlogService {

    private final BlogRepository blogRepository;

    public AdminBlogService(
            BlogRepository blogRepository) {

        this.blogRepository = blogRepository;
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlog(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Blog ID is required"
            );
        }

        return blogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Blog not found with id: " + id
                        )
                );
    }

    @Transactional
    public Blog createBlog(Blog blog) {

        if (blog == null) {
            throw new RuntimeException(
                    "Blog data is required"
            );
        }

        blog.setId(null);

        return blogRepository.save(blog);
    }

    @Transactional
    public Blog updateBlog(
            Long id,
            Blog blog) {

        if (blog == null) {
            throw new RuntimeException(
                    "Blog data is required"
            );
        }

        Blog existingBlog = getBlog(id);

        BeanUtils.copyProperties(
                blog,
                existingBlog,
                "id"
        );

        return blogRepository.save(
                existingBlog
        );
    }

    @Transactional
    public void deleteBlog(Long id) {

        if (id == null) {
            throw new RuntimeException(
                    "Blog ID is required"
            );
        }

        if (!blogRepository.existsById(id)) {
            throw new RuntimeException(
                    "Blog not found with id: " + id
            );
        }

        blogRepository.deleteById(id);
    }
}