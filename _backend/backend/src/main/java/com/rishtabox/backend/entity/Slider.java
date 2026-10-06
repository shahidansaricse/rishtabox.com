package com.rishtabox.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sliders")
public class Slider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =====================================================
    // TITLE - OPTIONAL
    // =====================================================

    @Column(nullable = true)
    private String title;


    // =====================================================
    // SUBTITLE - OPTIONAL
    // =====================================================

    @Column(length = 1000, nullable = true)
    private String subtitle;


    // =====================================================
    // IMAGE - REQUIRED
    // =====================================================

    @Column(nullable = false)
    private String image;


    // =====================================================
    // LINK - OPTIONAL
    // =====================================================

    @Column(length = 500, nullable = true)
    private String link;


    // =====================================================
    // DISPLAY ORDER
    // =====================================================

    @Column(name = "display_order")
    private Integer displayOrder = 0;


    // =====================================================
    // ACTIVE STATUS
    // =====================================================

    @Column(nullable = false)
    private Boolean active = true;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Slider() {
    }


    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
