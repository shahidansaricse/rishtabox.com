package com.rishtabox.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "festivals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Festival {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;
}