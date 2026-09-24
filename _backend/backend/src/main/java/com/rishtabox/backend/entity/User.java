package com.rishtabox.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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
    // ROLES
    // =====================================================

    public enum Role {

        // Normal customer
        USER,

        // Store administrator
        ADMIN,

        // Highest-level administrator
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
}