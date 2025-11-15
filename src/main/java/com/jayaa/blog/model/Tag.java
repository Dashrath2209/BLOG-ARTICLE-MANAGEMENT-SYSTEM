package com.jayaa.blog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Entity
@Table(name = "tags")
@Data
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    // ⭐ ADD @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Article> articles = new HashSet<>();
}