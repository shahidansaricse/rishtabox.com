 package com.rishtabox.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Relationship {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    private String image;
}

