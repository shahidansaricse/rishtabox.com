package com.rishtabox.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String image;

    @Column(nullable = false)
    private boolean pinned = false;

    public Category() {
    }

    public Category(
            String id,
            String name,
            String description,
            String image,
            boolean pinned) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.pinned = pinned;
    }

    // =========================
    // GETTERS
    // =========================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public boolean isPinned() {
        return pinned;
    }

    // =========================
    // SETTERS
    // =========================

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}