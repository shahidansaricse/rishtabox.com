package com.rishtabox.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "festivals")
public class Festival {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "image")
    private String image;

    public Festival() {
    }

    public Festival(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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
}