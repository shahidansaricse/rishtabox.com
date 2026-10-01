package com.rishtabox.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "relationships")
public class Relationship {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean pinned = false;

    public Relationship() {
    }

    public Relationship(
            String id,
            String name,
            String image,
            String description,
            boolean pinned) {

        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
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

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
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

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}