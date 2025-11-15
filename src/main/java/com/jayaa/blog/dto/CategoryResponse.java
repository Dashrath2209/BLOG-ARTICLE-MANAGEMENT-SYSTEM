package com.jayaa.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer articleCount; // How many articles in this category
    private LocalDateTime createdAt;
}