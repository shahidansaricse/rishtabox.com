package com.rishtabox.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =====================================================
    // BASIC USER INFORMATION
    // =====================================================

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;


    // =====================================================
    // PASSWORD
    // =====================================================

    @JsonIgnore
    @Column(nullable = false)
    private String password;


    // =====================================================
    // USER ROLE
    // =====================================================

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;


    // =====================================================
    // ACCOUNT STATUS
    // =====================================================

    @Column(nullable = false)
    private boolean active = true;


    // =====================================================
    // CREATED DATE
    // =====================================================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // =====================================================
    // TOKEN VERSION
    // =====================================================
    //
    // Used for Force Logout.
    //
    // Every JWT contains this version.
    //
    // When Force Logout is performed:
    //
    // tokenVersion = tokenVersion + 1
    //
    // All previously issued tokens become invalid.
    //
    // =====================================================

    @Column(nullable = false)
    private Long tokenVersion = 0L;


    // =====================================================
    // ROLES
    // =====================================================

    public enum Role {
        USER,
        ADMIN,
        SUPER_ADMIN
    }


    // =====================================================
    // CONSTRUCTORS
    // =====================================================

    public User() {
    }


    public User(
            String name,
            String email,
            String phone,
            String password) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;

        this.role = Role.USER;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.tokenVersion = 0L;
    }


    // =====================================================
    // JPA CREATE HANDLER
    // =====================================================

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (role == null) {
            role = Role.USER;
        }

        if (tokenVersion == null) {
            tokenVersion = 0L;
        }

        // New users are active by default
        active = true;
    }


    // =====================================================
    // GETTERS
    // =====================================================

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    @JsonIgnore
    public String getPassword() {
        return password;
    }


    public Role getRole() {
        return role;
    }


    public boolean isActive() {
        return active;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public Long getTokenVersion() {
        return tokenVersion;
    }


    // =====================================================
    // SETTERS
    // =====================================================

    public void setId(Long id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public void setRole(Role role) {
        this.role = role;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public void setTokenVersion(Long tokenVersion) {
        this.tokenVersion = tokenVersion;
    }
}