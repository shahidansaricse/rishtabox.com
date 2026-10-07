package com.rishtabox.backend.service;

import com.rishtabox.backend.entity.Blog;
import com.rishtabox.backend.repository.BlogRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class BlogService {

    private final BlogRepository blogRepository;

    @Value("${rishtabox.blog.upload-dir}")
    private String uploadDir;

    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    // =====================================================
    // GET PUBLISHED BLOGS
    // =====================================================

    public List<Blog> getPublishedBlogs() {

        return blogRepository
                .findByPublishedTrueOrderByCreatedAtDesc();
    }

    // =====================================================
    // GET ALL BLOGS
    // =====================================================

    public List<Blog> getAllBlogs() {

        return blogRepository
                .findAllByOrderByCreatedAtDesc();
    }

    // =====================================================
    // GET BLOG BY ID
    // =====================================================

    public Blog getBlogById(Long id) {

        return blogRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Blog not found"
                        )
                );
    }

    // =====================================================
    // CREATE BLOG
    // =====================================================

    public Blog createBlog(
            String title,
            String description,
            String content,
            MultipartFile image,
            String author,
            Boolean published
    ) throws IOException {

        if (image == null || image.isEmpty()) {

            throw new IllegalArgumentException(
                    "Blog image is required."
            );
        }

        validateImage(image);

        String imagePath = saveImage(image);

        Blog blog = new Blog();

        blog.setTitle(
                cleanRequiredValue(title)
        );

        blog.setDescription(
                cleanRequiredValue(description)
        );

        blog.setContent(
                cleanRequiredValue(content)
        );

        blog.setImage(imagePath);

        blog.setAuthor(
                cleanRequiredValue(author)
        );

        blog.setPublished(
                published != null
                        ? published
                        : true
        );

        return blogRepository.save(blog);
    }

    // =====================================================
    // UPDATE BLOG
    // =====================================================

    public Blog updateBlog(
            Long id,
            String title,
            String description,
            String content,
            MultipartFile image,
            String author,
            Boolean published
    ) throws IOException {

        Blog blog = getBlogById(id);

        blog.setTitle(
                cleanRequiredValue(title)
        );

        blog.setDescription(
                cleanRequiredValue(description)
        );

        blog.setContent(
                cleanRequiredValue(content)
        );

        blog.setAuthor(
                cleanRequiredValue(author)
        );

        if (published != null) {

            blog.setPublished(published);
        }

        if (image != null && !image.isEmpty()) {

            validateImage(image);

            String oldImage =
                    blog.getImage();

            String newImage =
                    saveImage(image);

            blog.setImage(newImage);

            deleteOldImage(oldImage);
        }

        return blogRepository.save(blog);
    }

    // =====================================================
    // DELETE BLOG
    // =====================================================

    public void deleteBlog(Long id) {

        Blog blog = getBlogById(id);

        deleteOldImage(
                blog.getImage()
        );

        blogRepository.delete(blog);
    }

    // =====================================================
    // CLEAN REQUIRED VALUE
    // =====================================================

    private String cleanRequiredValue(
            String value
    ) {

        if (
                value == null ||
                        value.trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Required blog field is missing."
            );
        }

        return value.trim();
    }

    // =====================================================
    // VALIDATE IMAGE
    // =====================================================

    private void validateImage(
            MultipartFile image
    ) {

        long maxSize =
                5 * 1024 * 1024;

        if (image.getSize() > maxSize) {

            throw new IllegalArgumentException(
                    "Image size must be less than 5 MB."
            );
        }

        String contentType =
                image.getContentType();

        if (
                contentType == null ||
                        !(
                                contentType.equalsIgnoreCase(
                                        "image/jpeg"
                                )
                                        ||
                                        contentType.equalsIgnoreCase(
                                                "image/png"
                                        )
                                        ||
                                        contentType.equalsIgnoreCase(
                                                "image/webp"
                                        )
                        )
        ) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed."
            );
        }
    }

    // =====================================================
    // SAVE IMAGE
    // =====================================================

    private String saveImage(
            MultipartFile image
    ) throws IOException {

        Path directory =
                Paths.get(uploadDir);

        Files.createDirectories(
                directory
        );

        String extension =
                getExtension(
                        image.getOriginalFilename()
                );

        String fileName =
                UUID.randomUUID()
                        .toString()
                        + extension;

        Path filePath =
                directory.resolve(fileName);

        Files.copy(
                image.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        return "/uploads/blogs/" + fileName;
    }

    // =====================================================
    // GET FILE EXTENSION
    // =====================================================

    private String getExtension(
            String fileName
    ) {

        if (
                fileName == null ||
                        !fileName.contains(".")
        ) {

            return ".jpg";
        }

        String extension =
                fileName.substring(
                        fileName.lastIndexOf(".")
                ).toLowerCase();

        if (
                extension.equals(".jpeg") ||
                        extension.equals(".jpg") ||
                        extension.equals(".png") ||
                        extension.equals(".webp")
        ) {

            return extension;
        }

        return ".jpg";
    }

    // =====================================================
    // DELETE OLD IMAGE
    // =====================================================

    private void deleteOldImage(
            String imagePath
    ) {

        if (
                imagePath == null ||
                        imagePath.isBlank()
        ) {

            return;
        }

        try {

            String fileName =
                    Paths.get(imagePath)
                            .getFileName()
                            .toString();

            Path file =
                    Paths.get(uploadDir)
                            .resolve(fileName);

            Files.deleteIfExists(file);

        } catch (Exception e) {

            System.err.println(
                    "Could not delete old blog image: "
                            + e.getMessage()
            );
        }
    }
}